package ru.shift.userimporter.api.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.shift.userimporter.api.dto.DetailedFileStatisticDto;
import ru.shift.userimporter.api.dto.FileIdResponse;
import ru.shift.userimporter.api.dto.FileResponseDto;
import java.util.List;

import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.service.FileService;
import ru.shift.userimporter.core.service.StatisticService;
import org.springframework.validation.annotation.Validated;
import ru.shift.userimporter.core.service.FileProcessingService;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;
    private final StatisticService statService;
    private final FileProcessingService procService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileIdResponse upload(@RequestPart("file") MultipartFile file) throws Exception {
        return new FileIdResponse(fileService.upload(file));
    }

    @PostMapping("/{fileId}/processing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startProcessing(@PathVariable Integer fileId) {
        procService.startAsync(fileId);
    }

    @GetMapping("/{fileId}/statistics")
    public DetailedFileStatisticDto detailed(@PathVariable Integer fileId) {
        return statService.detailed(fileId);
    }

    @GetMapping("/statistics")
    public List<FileResponseDto> getFileStatistics(
            @RequestParam(value = "status", required = false) FileStatus status) {
        return statService.list(status);
    }
}
