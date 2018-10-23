package com.heroslender.herovender.service;

import com.heroslender.herovender.utils.CustomFileConfiguration;
import lombok.val;

import java.util.*;

public class MessageService implements Service<String> {
    private final CustomFileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();

    public MessageService(CustomFileConfiguration customFileConfiguration) {
        this.config = customFileConfiguration;
    }

    @Override
    public void init() {
        config.reload();
        loadOrSaveDefault("sell.sold", "&aYou sold &7:item-count: &afor &f:price-formated:&a!");
        loadOrSaveDefault("sell.no-items", "&cYou don't have any items that can be sold!");
        loadOrSaveDefault("sell.delay", "&cYou must wait :delay: to sell again!");
        loadOrSaveDefault("autosell.menu.title", "Sell menu!");
        loadOrSaveDefault("autosell.menu.sell", "DOUBLE_PLANT 1 name:&aSell lore:&7Click_here_to_sell_your_inventory!");
        loadOrSaveDefault("autosell.menu.autosell.on", "LEVER 1 name:&aAuto-Sell " +
                "lore:&7Sell_your_inventory_by_sneaking|" +
                "|" +
                "&7Current_state->_&aActive|" +
                "&7(Click_to_deactivate)");
        loadOrSaveDefault("autosell.menu.autosell.off", "LEVER 1 name:&cAuto-Sell " +
                "lore:&7Sell_your_inventory_by_sneaking|" +
                "|" +
                "&7Current_state->_&cInactive|" +
                "&7(Click_to_activate)");
    }

    private void loadOrSaveDefault(final String messageId, final String defaultMessage) {
        if (config == null) {
            messages.put(messageId, defaultMessage);
            return;
        }

        if (!config.contains(messageId)) {
            config.set(messageId, defaultMessage);
            config.save();
        }

        messages.put(messageId, config.getString(messageId, defaultMessage, true));
    }

    @Override
    public List<String> get() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public Optional<String> getById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        val message = messages.get(id);

        if (message == null || message.equalsIgnoreCase("none")) {
            return Optional.empty();
        }

        return Optional.of(message);
    }

    @Override
    public void stop() {
        messages.clear();
    }
}
