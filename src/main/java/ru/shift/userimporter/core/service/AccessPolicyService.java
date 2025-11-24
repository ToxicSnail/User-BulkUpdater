package ru.shift.userimporter.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.shift.userimporter.config.AccessPolicyProperties;
import ru.shift.userimporter.core.model.FileStatus;
import ru.shift.userimporter.core.model.UploadedFile;
import ru.shift.userimporter.core.repository.UploadedFileRepository;
import ru.shift.userimporter.core.service.security.SecurityUtils;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AccessPolicyService {
    private final AccessPolicyProperties props;
    private final UploadedFileRepository fileRepo;

    public void checkWriteWindow(SecurityUtils.CurrentUser user) {
        if (!user.isOperator()) {
            return; // ADMIN/AUDITOR/SERVICE не ограничены по времени
        }
        LocalTime now = LocalTime.now();
        if (now.isBefore(props.getWorkdayStart()) || now.isAfter(props.getWorkdayEnd())) {
            throw new AccessDeniedException("Write operations are allowed only between "
                    + props.getWorkdayStart() + " and " + props.getWorkdayEnd());
        }
    }

    public void assertFileRead(SecurityUtils.CurrentUser user, UploadedFile file) {
        if (user.isAdmin() || user.isAuditor()) {
            return;
        }
        if (user.isOperator() && user.username().equals(file.getOwner())) {
            return;
        }
        throw new AccessDeniedException("Forbidden to access this file");
    }

    public void assertFileProcessing(SecurityUtils.CurrentUser user, UploadedFile file) {
        checkWriteWindow(user);
        if (user.isAdmin()) {
            return;
        }
        if (user.isOperator() && user.username().equals(file.getOwner())) {
            if (props.getMaxActiveTasksPerUser() > 0) {
                long active = fileRepo.countByOwnerAndStatus(user.username(), FileStatus.IN_PROGRESS);
                if (active >= props.getMaxActiveTasksPerUser()) {
                    throw new AccessDeniedException("Parallel processing limit reached");
                }
            }
            return;
        }
        throw new AccessDeniedException("Forbidden to start processing");
    }
}
