package ru.shift.userimporter.core.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.core.exception.ConflictException;
import ru.shift.userimporter.core.exception.LineValidator;
import ru.shift.userimporter.core.model.*;
import ru.shift.userimporter.core.repository.*;

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
            throw new ConflictException("processing already started");
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

        ProcessStats s = readAndProcessFile(path, meta);

        meta.setTotalRows(s.getTotal());
        meta.setProcessedRows(s.getInserted() + s.getUpdated() + s.getInvalid());
        meta.setValidRows(s.getInserted());
        meta.setInvalidRows(s.getInvalid());
        fileRepo.save(meta);
    }

    private ProcessStats readAndProcessFile(Path path, FileMeta meta) {
        int total = 0, inserted = 0, updated = 0, invalid = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                total++;                              // считаем строку сразу

                if (line.isBlank()) {
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
            meta.setStatus(FileStatus.DONE);
        } catch (Exception ex) {
            log.error("processing failed for file {}", meta.getId(), ex);
            meta.setStatus(FileStatus.FAILED);
        }

        return new ProcessStats(total, inserted, updated, invalid);
    }

    private Client parseLine(String line) {
        String[] f   = line.split(",", -1);
        LineValidator.Err err = LineValidator.validate(f);
        if (err != null) throw new ValidationException(err.name());

        Client c = new Client();
        c.setLastName(f[0]);
        c.setFirstName(f[1]);
        c.setMiddleName(f[2].isBlank() ? null : f[2]);
        c.setEmail(f[3]);
        c.setPhone(f[4]);
        c.setBirthDate(LocalDate.parse(f[5]));
        return c;
    }

    @Getter
    @AllArgsConstructor
    private static class ProcessStats {
        private final int total;
        private final int inserted;
        private final int updated;
        private final int invalid;
    }
}
