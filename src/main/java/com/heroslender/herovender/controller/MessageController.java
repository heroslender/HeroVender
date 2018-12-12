package com.heroslender.herovender.controller;

import com.heroslender.herovender.service.MessageService;

import java.util.Optional;

public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    public Optional<String> getMessage(final String messageId) {
        return messageService.getById(messageId);
    }
}
