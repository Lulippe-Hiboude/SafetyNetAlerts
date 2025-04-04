package com.lulippe.safetynetalerts.exception.json_exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) {
        super("impossible to load json file : " + message);
    }
}
