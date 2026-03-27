package com.github.heroslender.herovender.service;

import com.github.heroslender.herovender.Message;
import com.github.heroslender.herovender.helpers.CustomFileConfiguration;
import com.github.heroslender.herovender.utils.NumberUtil;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RequiredArgsConstructor
public class MessageService implements Service {
    private final CustomFileConfiguration config;

    @Override
    public void init() {
        config.reload();

        NumberUtil.NumberFormatShortSuffix = getOrSaveDefault("number-formatting", "K;M;B;T;Q").split(";");
        Message.SellAutoSellOn = getOrSaveDefault("sell.autosell.on", Message.SellAutoSellOn);
        Message.SellAutoSellOff = getOrSaveDefault("sell.autosell.off", Message.SellAutoSellOff);
        Message.SellShiftSellOn = getOrSaveDefault("sell.shiftsell.on", Message.SellShiftSellOn);
        Message.SellShiftSellOff = getOrSaveDefault("sell.shiftsell.off", Message.SellShiftSellOff);

        Message.SellCommandChatSold = getOrSaveDefault("sell.command.chat.sold", Message.SellCommandChatSold);
        Message.SellCommandChatNoItems = getOrSaveDefault("sell.command.chat.no-items", Message.SellCommandChatNoItems);
        Message.SellCommandChatDelay = getOrSaveDefault("sell.command.chat.delay", Message.SellCommandChatDelay);
        Message.SellCommandActionbarSold = getOrSaveDefault("sell.command.actionbar.sold", Message.SellCommandActionbarSold);
        Message.SellCommandActionbarNoItems = getOrSaveDefault("sell.command.actionbar.no-items", Message.SellCommandActionbarNoItems);
        Message.SellCommandActionbarDelay = getOrSaveDefault("sell.command.actionbar.delay", Message.SellCommandActionbarDelay);

        Message.SellAutoChatSold = getOrSaveDefault("sell.autosell.chat.sold", Message.SellAutoChatSold);
        Message.SellAutoChatNoItems = getOrSaveDefault("sell.autosell.chat.no-items", Message.SellAutoChatNoItems);
        Message.SellAutoActionbarSold = getOrSaveDefault("sell.autosell.actionbar.sold", Message.SellAutoActionbarSold);
        Message.SellAutoActionbarNoItems = getOrSaveDefault("sell.autosell.actionbar.no-items", Message.SellAutoActionbarNoItems);

        Message.SellShiftChatSold = getOrSaveDefault("sell.shiftsell.chat.sold", Message.SellShiftChatSold);
        Message.SellShiftChatNoItems = getOrSaveDefault("sell.shiftsell.chat.no-items", Message.SellShiftChatNoItems);
        Message.SellShiftChatDelay = getOrSaveDefault("sell.shiftsell.chat.delay", Message.SellShiftChatDelay);
        Message.SellShiftActionbarSold = getOrSaveDefault("sell.shiftsell.actionbar.sold", Message.SellShiftActionbarSold);
        Message.SellShiftActionbarNoItems = getOrSaveDefault("sell.shiftsell.actionbar.no-items", Message.SellShiftActionbarNoItems);
        Message.SellShiftActionbarDelay = getOrSaveDefault("sell.shiftsell.actionbar.delay", Message.SellShiftActionbarDelay);
    }

    @Nullable
    private String getOrSaveDefault(@Nonnull final String messageId, @Nonnull final String defaultMessage) {
        if (config == null) {
            return defaultMessage;
        }

        if (!config.contains(messageId)) {
            config.set(messageId, defaultMessage);
            config.save();
        }

        final String message = config.getString(messageId, defaultMessage, true);
        if (message.isEmpty() || message.equals("none")) {
            return null;
        }

        return message;
    }

    @Override
    public void stop() {
    }
}
