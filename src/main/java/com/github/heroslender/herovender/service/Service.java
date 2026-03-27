package com.github.heroslender.herovender.service;

public interface Service {
    /**
     * Called when the plugin is initialized
     */
    void init();

    /**
     * Called when the plugin is disabled
     */
    void stop();
}
