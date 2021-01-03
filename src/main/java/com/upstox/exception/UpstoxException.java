package com.upstox.exception;

public class UpstoxException extends IllegalStateException {
    private String entityType;

    public UpstoxException(String message, Throwable cause) {
        super(message, cause);
    }
    public UpstoxException(String message, Class<?> entityClass, Throwable cause) {
        super(message, cause);
        entityType = extractEntityTypeFromClass(entityClass);
    }

    protected static String extractEntityTypeFromClass(Class<?> entityClass) {
        if (entityClass == null) {
            return "null";
        }
        return entityClass.getSimpleName();
    }
}
