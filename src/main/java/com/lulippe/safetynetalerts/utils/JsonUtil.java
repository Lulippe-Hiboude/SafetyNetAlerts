package com.lulippe.safetynetalerts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.json_exception.DataLoadingException;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON serialization and deserialization.
 * This class allows converting JSON to Java objects
 * using the Jackson library.
 *
 * <p>It is used by {@link DataManager} to load data from the JSON file.</p>
 */
@UtilityClass
public class JsonUtil {
    private final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Converts a JSON string into a Java object of the specified type.
     *
     * @param json the JSON string to convert
     * @param typeReference the target object type
     * @param <T> the generic type of the object
     * @return the deserialized object
     * @throws DataLoadingException if a parsing error occurs
     */
    public <T> T fromJson(final String json, final TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("The file is malformed: {} - Error: {}", json, e.getMessage(), e);
            throw new DataLoadingException("The file is malformed: " + json + " - Error: " + e.getMessage(), e);
        }
    }
}
