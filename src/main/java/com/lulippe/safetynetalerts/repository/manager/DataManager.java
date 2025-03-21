package com.lulippe.safetynetalerts.repository.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lulippe.safetynetalerts.dto.DataContainer;
import com.lulippe.safetynetalerts.exception.DataLoadingException;
import com.lulippe.safetynetalerts.exception.InvalidFileException;
import com.lulippe.safetynetalerts.utils.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class responsible for loading and managing the application's data.
 * It stores a unique instance of {@link DataContainer} that is used by the repositories.
 * <p>
 * At application startup, data is loaded from the JSON file.
 * This class also provides access to in-memory data.
 *
 * <p>In the future, an auto-save method could be added to periodically
 * write modifications to a "dataSaved" file.</p>
 */

@Data
@Component
public class DataManager {
    private static String FILE_PATH = "src/main/resources/json/data.json";
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

    public DataContainer loadData() {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            logger.error("the file {} does not exist", FILE_PATH);
            throw new DataLoadingException("the file does not exist", new FileNotFoundException(FILE_PATH));
        }
        String json = getFile();
        return JsonUtil.fromJson(json, new TypeReference<>() {
        });
    }

    private String getFile() {
        try {
            return new String(Files.readAllBytes(Paths.get(FILE_PATH)));
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
    }
}
