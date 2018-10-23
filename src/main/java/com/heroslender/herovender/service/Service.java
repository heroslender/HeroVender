package com.heroslender.herovender.service;

import java.util.List;
import java.util.Optional;

public interface Service<T> {
    /**
     * Called when the plugin is initialized
     */
    void init();

    List<T> get();

    /**
     * Get a {@link T} by its ID
     * @param id The object ID
     * @return An {@link java.util.Optional} containing the Oject if found or else Empty
     */
    Optional<T> getById(String id);

    /**
     * Called when the plugin is disabled
     */
    void stop();
}
