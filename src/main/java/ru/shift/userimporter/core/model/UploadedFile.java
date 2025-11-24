package ru.shift.userimporter.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "original_filename", length = 100, nullable = false)
    private String originalFilename;

    @Column(name = "storage_path", length = 512, nullable = false, unique = true)
    private String storagePath;

    @Column(name = "hash", length = 40, nullable = false)
    private String hash;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private FileStatus status = FileStatus.NEW;

    @Column(name = "inserted_rows", nullable = false)
    private int insertedRows;

    @Column(name = "updated_rows", nullable = false)
    private int updatedRows;

    @Column(name = "owner", length = 100, nullable = false)
    private String owner = "system";

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessingError> errors = new ArrayList<>();

    public void addError(ProcessingError err) {
        err.setFile(this);
        errors.add(err);
    }
}
