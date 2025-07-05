package ru.shift.userimporter.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "errors")
@Entity
@Table(name = "uploaded_files")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "original_filename", nullable = false, length = 50)
    private String originalFilename;

    @Column(name = "storage_path", nullable = false, length = 512, unique = true)
    private String storagePath;

    @Column(nullable = false, length = 40)
    private String hash;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FileStatus status = FileStatus.NEW;

    private Integer totalRows;
    private Integer processedRows;
    private Integer validRows;
    private Integer invalidRows;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessingError> errors = new ArrayList<>();

    public void addError(ProcessingError err) {
        err.setFile(this);
        errors.add(err);
    }
}