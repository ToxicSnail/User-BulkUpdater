package ru.shift.userimporter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "file-storage")
public class StorageProperties {
    /** Это будет каталог, куда копируем загружаемые файлы. */
    private Path location = Path.of("uploads");

    public Path getLocation() { return location; }
    public void setLocation(Path location) { this.location = location; }
}
