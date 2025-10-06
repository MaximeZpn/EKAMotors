package com.gp9.game_MS.exception;

/**
 * Exception thrown when an entity is not found in the database.
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " with id " + id + " not found");
    }
    
    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " with identifier " + identifier + " not found");
    }
}
