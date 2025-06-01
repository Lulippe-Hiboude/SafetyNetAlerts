package com.lulippe.safetynetalerts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootTest(properties = "data.saving.enabled=true")
@ActiveProfiles("test")
class SafetyNetAlertsApplicationTests {

    @Test
    void contextLoads() {
    }

    @AfterEach
    void cleanUpBackupFiles() throws IOException {
        String savedPath = "data/saved-data/test-data";
        Path backupDir = Paths.get(savedPath);
        if (Files.exists(backupDir) && Files.isDirectory(backupDir)) {
            try (Stream<Path> files = Files.list(backupDir)) {
                files.filter(path -> path.getFileName().toString().startsWith("data-saved"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println("Failed to delete file: " + path + " due to: " + e.getMessage());
                            }
                        });
            }
        }
    }
}
