package com.lulippe.safetynetalerts.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static void logPersonNotFound(String firstName, String lastName) {
        log.error("Person with firstName {} and lastName {} does not exist", firstName, lastName);
    }
}
