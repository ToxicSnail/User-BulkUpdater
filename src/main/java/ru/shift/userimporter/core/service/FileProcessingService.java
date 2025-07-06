package ru.shift.userimporter.core.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.userimporter.core.exception.ConflictException;
import ru.shift.userimporter.core.exception.LineValidator;
import ru.shift.userimporter.core.model.*;
import ru.shift.userimporter.core.repository.ClientRepository;
import ru.shift.userimporter.core.repository.ProcessingErrorRepository;
import ru.shift.userimporter.core.repository.UploadedFileRepository;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {
    private final UploadedFileRepository fileRepo;
    private final ClientRepository       clientRepo;
    private final ProcessingErrorRepository errRepo;
    private final ObjectProvider<FileProcessingService> self;

    @Transactional
    public void startAsync(Integer fileId) {
        UploadedFile file = fileRepo.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("file not found"));

        if (file.getStatus() != FileStatus.NEW) {
            throw new ConflictException("processing already started");
        }

        file.setStatus(FileStatus.IN_PROGRESS);
        fileRepo.save(file);

        self.getObject().processAsync(fileId);  //теперь processAsync идет так
    }

    @Async("fileExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAsync(Integer fileId) {
        UploadedFile file = fileRepo.findById(fileId).orElseThrow();
        Path         path = Path.of(file.getStoragePath());

        ProcessStats st = readAndProcessFile(path, file);

        file.setInsertedRows(st.getInserted());
        file.setUpdatedRows(st.getUpdated());
        fileRepo.save(file);
    }

    private ProcessStats readAndProcessFile(Path path, UploadedFile file) {
        int total = 0, inserted = 0, updated = 0, invalid = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                total++;

                if (line.isBlank()) {
                    registerError(file, total, LineValidator.Err.INVALID_FORMAT, line);
                    invalid++;
                    continue;
                }

                Client parsed;
                try {
                    parsed = ClientCsvParser.parse(line);
                } catch (ValidationException ex) {
                    LineValidator.Err code = LineValidator.Err.valueOf(ex.getMessage());
                    registerError(file, total, code, line);
                    invalid++;
                    continue;
                }

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
            }

            file.setStatus(FileStatus.DONE);
        } catch (Exception ex) {
            log.error("processing failed for file {}", file.getId(), ex);
            file.setStatus(FileStatus.FAILED);
        }

        return new ProcessStats(total, inserted, updated, invalid);
    }
    private void registerError(UploadedFile file,
                               int rowNumber,
                               LineValidator.Err code,
                               String rawData) {

        ProcessingError pe = new ProcessingError();
        pe.setFile(file);
        pe.setRowNumber(rowNumber);
        pe.setErrorCode(code.name());
        pe.setErrorMessage(code.getDescription()); // у enum Err есть поле description
        pe.setRawData(rawData);
        errRepo.save(pe);
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