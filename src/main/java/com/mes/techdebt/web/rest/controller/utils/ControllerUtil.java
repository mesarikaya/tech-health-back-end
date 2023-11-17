package com.mes.techdebt.web.rest.controller.utils;

import com.mes.techdebt.web.rest.errors.BadRequestAlertException;

/**
 * Utility class for testing REST controllers.
 */
public final class ControllerUtil {

    public static void prepareNotUniqueException(String ENTITY_NAME, String description) {
        throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, description);
    }

    public static void prepareNotFoundEntity(String ENTITY_NAME, String description) {
        throw new BadRequestAlertException("Entity not found", ENTITY_NAME, description);
    }

    public static void prepareInvalidId(String ENTITY_NAME, String description) {
        throw new BadRequestAlertException("Entity not found", ENTITY_NAME, description);
    }

    private ControllerUtil() {}
}
