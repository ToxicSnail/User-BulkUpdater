package ru.shift.userimporter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalTime;

@Getter
@Setter
@ConfigurationProperties(prefix = "access-policy")
public class AccessPolicyProperties {
    /**
     * Начало разрешенного окна для операций записи.
     */
    private LocalTime workdayStart = LocalTime.of(6, 0);
    /**
     * Конец разрешенного окна для операций записи.
     */
    private LocalTime workdayEnd = LocalTime.of(22, 0);

    /**
     * Лимит параллельных задач обработки на пользователя.
     * 0 или отрицательное значение отключает проверку.
     */
    private int maxActiveTasksPerUser = 1;
}
