package com.lulippe.safetynetalerts.integration;

import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@SpringBootTest(properties = "data.saving.enabled=true")
@ExtendWith(MockitoExtension.class)
class DataManagerSaveIT {
    @Autowired
    private DataManager dataManager;

    @Value("${data.saved.path}")
    private String savedDirPath;

    @Test
    @DisplayName("should Save data on Shutdown")
    void shouldSaveDataOnShutdown() {
        //when
        dataManager.saveDataOnShutdown();

        //then
        Path saveDir = Paths.get(savedDirPath);
        try (Stream<Path> files = Files.list(saveDir)) {
            boolean found = files.anyMatch(f -> f.getFileName().toString().startsWith("data-saved"));
            assertTrue(found, "Un fichier de sauvegarde doit être créé");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
