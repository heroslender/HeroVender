package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.data.User;

import java.util.concurrent.CompletableFuture;

public interface StorageService extends Service {
    CompletableFuture<Void> loadUser(User user);

    CompletableFuture<Void> saveUser(User user);
}
