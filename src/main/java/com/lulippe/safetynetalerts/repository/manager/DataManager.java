package com.lulippe.safetynetalerts.repository.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.exception.json_exception.DataLoadingException;
import com.lulippe.safetynetalerts.utils.JsonUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Class responsible for managing the application's data lifecycle.
 * <p>
 * The {@code DataManager} handles:
 * <ul>
 *   <li>Loading the initial data at application startup (either from a default JSON resource or the latest saved file)</li>
 *   <li>Providing in-memory access to a unique instance of {@link DataContainer}</li>
 *   <li>Persisting data into timestamped JSON files during application shutdown (if enabled)</li>
 * </ul>
 * <p>
 * This component is annotated with {@link Component}, making it a Spring-managed singleton.
 * The optional configuration of saving behavior is controlled using {@code application.properties}.
 * </p>
 */

@Data
@Component
public class DataManager {
    @Value("${data.file.path}")
    private String resourceFilePath;

    @Value("${data.saved.path}")
    private String savedDirPath;

    /**
     * Flag that controls whether data saving is enabled.
     * Defaults to {@code true}; should be set to {@code false} in test profile.
     */
    @Value("${data.saving.enabled:true}")
    private boolean savingEnabled;

    private final Logger logger = LoggerFactory.getLogger(DataManager.class);
    private DataContainer dataContainer;

    /**
     * Loads data from the JSON file during bean initialization.
     */
    @PostConstruct
    public void init() {
        this.dataContainer = loadData();
        logger.info("DataContainer loaded");
    }

    /**
     * Loads the data from the appropriate source.
     * <p>
     * If data saving is enabled and a backup exists, the latest backup file is loaded.
     * Otherwise, the original resource file is loaded.
     *
     * @return the populated {@link DataContainer}
     */
    public DataContainer loadData() {
        if (!savingEnabled) {
            logger.info("Profil test active, original data will be used");
            return loadResourceFile(resourceFilePath);
        }
        Path savedDir = Paths.get(savedDirPath);
        if (Files.exists(savedDir) && Files.isDirectory(savedDir)) {
            Optional<Path> latest = getLatestBackupFile(savedDir);
            if (latest.isPresent()) {
                logger.info("Loading latest saved file: {}", latest.get());
                return loadFile(latest.get());
            }
        }

        return loadResourceFile(resourceFilePath);
    }

    /**
     * Saves the in-memory data to a timestamped JSON file before application shutdown.
     * <p>
     * The method is triggered automatically via {@link PreDestroy} when the Spring context is closed.
     */
    @PreDestroy
    public void saveDataOnShutdown() {
        if (!savingEnabled) {
            logger.info("Profil test active, no data will be saved");
            return;
        }

        try {
            if (dataContainer == null) {
                logger.warn("No data to save on shutdown.");
                return;
            }

            Path saveDir = Paths.get(savedDirPath);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }

            String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
            String fileName = "data-saved-" + date + "-" + time + ".json";
            Path savePath = saveDir.resolve(fileName);

            String json = JsonUtil.toJson(dataContainer);
            Files.writeString(savePath, json);

            logger.info("Data saved successfully to {}", savePath);
        } catch (IOException e) {
            logger.error("Failed to save data on shutdown", e);
        }
    }

    /**
     * Reads a JSON file from disk and deserializes it into a {@link DataContainer}.
     *
     * @param path the path of the file to load
     * @return the populated {@link DataContainer}
     * @throws UncheckedIOException if the file cannot be read
     */
    private DataContainer loadFile(Path path) {
        try {
            String json = Files.readString(path);
            return JsonUtil.fromJson(json, new TypeReference<>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load file: " + path, e);
        }
    }

    /**
     * Finds the most recently modified backup file in the specified directory.
     *
     * @param backupDir the directory containing backup files
     * @return an {@link Optional} containing the latest backup {@link Path}, if any
     */
    private Optional<Path> getLatestBackupFile(Path backupDir) {
        try (Stream<Path> files = Files.list(backupDir)) {
            return files
                    .filter(f -> f.getFileName().toString().startsWith("data-saved"))
                    .max(Comparator.comparingLong(f -> f.toFile().lastModified()));
        } catch (IOException e) {
            logger.warn("Unable to read backup files: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Loads the original resource file from the classpath.
     *
     * @param relativePath the relative path of the resource file
     * @return the deserialized {@link DataContainer}
     * @throws DataLoadingException if the file is not found or unreadable
     */
    private DataContainer loadResourceFile(String relativePath) {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(relativePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + relativePath);
            }
            return JsonUtil.fromJson(new String(inputStream.readAllBytes()), new TypeReference<>() {});
        }catch (IOException e){
            throw new DataLoadingException("Failed to load resource file: " + relativePath, e);
        }
    }
}
