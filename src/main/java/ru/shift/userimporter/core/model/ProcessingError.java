package ru.shift.userimporter.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "file_processing_errors")
@Getter @Setter @NoArgsConstructor
public class ProcessingError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private UploadedFile file;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "error_code", length = 30, nullable = false)
    private String errorCode;

    @Column(name = "raw_data")
    private String rawData;
}
