package com.github.heroslender.herovender.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StorageType {
    H2("org.h2.Driver"),
    MYSQL("com.mysql.cj.jdbc.Driver");

    @Getter
    private final String driverClass;

    public static StorageType fromName(String name) {
        for (StorageType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return H2;
    }
}
