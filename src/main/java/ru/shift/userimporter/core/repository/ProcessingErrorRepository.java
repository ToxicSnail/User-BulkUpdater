package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shift.userimporter.core.model.ProcessingError;


public interface ProcessingErrorRepository extends JpaRepository<ProcessingError, Integer> {
}
