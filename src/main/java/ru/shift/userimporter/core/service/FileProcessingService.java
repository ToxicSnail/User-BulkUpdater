package ru.shift.userimporter.core.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.core.exception.LineValidator;
import ru.shift.userimporter.core.model.*;
import ru.shift.userimporter.core.repository.*;
import ru.shift.userimporter.core.service.ClientCsvParser;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileMetaRepository fileRepo;
    private final ClientRepository clientRepo;
    private final ProcessingErrorRepository errRepo;
    private final ObjectProvider<FileProcessingService> self;

    @Transactional
    public void startAsync(Integer fileId) {
        FileMeta meta = fileRepo.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("file not found"));

        if (meta.getStatus() != FileStatus.NEW) {
            throw new IllegalStateException("processing already started");
        }

        meta.setStatus(FileStatus.IN_PROGRESS);
        fileRepo.save(meta);

        self.getObject().processAsync(fileId);  //теперь processAsync идет так
    }

    @Async("fileExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAsync(Integer fileId) {
        FileMeta meta = fileRepo.findById(fileId).orElseThrow();
        Path path = Path.of(meta.getStoragePath());

        int[] cnt = readAndProcessFile(path, meta);

        meta.setTotalRows(cnt[0]);
        meta.setProcessedRows(cnt[1] + cnt[2] + cnt[3]);
        meta.setValidRows(cnt[1]);
        meta.setInvalidRows(cnt[3]);

        fileRepo.save(meta);
    }

    private int[] readAndProcessFile(Path path, FileMeta meta) {
        int total = 0, inserted = 0, updated = 0, invalid = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                total++;                              // считаем строку сразу

                if (line.isBlank()) {                 // пустая = ошибка
                    ProcessingError pe = new ProcessingError();
                    pe.setFile(meta);
                    pe.setLineNumber(total);
                    pe.setErrorMessage("EMPTY_LINE");
                    pe.setRawData(""); 
                    errRepo.save(pe);

                    invalid++;
                    continue;
                }

                try {
                    Client parsed = ClientCsvParser.parse(line);    // вынес в парсер

                    Optional<Client> storedOpt = clientRepo.findByPhone(parsed.getPhone());
                    if (storedOpt.isPresent()) {
                        Client stored = storedOpt.get();
                        stored.setFirstName(parsed.getFirstName());
                        stored.setLastName(parsed.getLastName());
                        stored.setMiddleName(parsed.getMiddleName());
                        stored.setEmail(parsed.getEmail());
                        stored.setBirthDate(parsed.getBirthDate());
                        clientRepo.save(stored);
                        updated++;
                    } else {
                        clientRepo.save(parsed);
                        inserted++;
                    }
                } catch (ValidationException ex) {
                    ProcessingError pe = new ProcessingError();
                    pe.setFile(meta);
                    pe.setLineNumber(total);
                    pe.setErrorMessage(ex.getMessage());
                    pe.setRawData(line);
                    errRepo.save(pe);
                    invalid++;
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("IO error while processing file", ex);
        }
        return new int[]{total, inserted, updated, invalid};
    }
}
