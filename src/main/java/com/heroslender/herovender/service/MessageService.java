package com.heroslender.herovender.service;

import com.heroslender.herovender.helpers.CustomFileConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;

@RequiredArgsConstructor
public class MessageService implements Service<String> {
    private final CustomFileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void init() {
        config.reload();
        loadOrSaveDefault("number-formatting", "K;M;B;T;Q");
        loadOrSaveDefault("sell.sold", "&aYou sold &7:invoice-item-count: &afor &f:invoice-total-formatted:&a!");
        loadOrSaveDefault("sell.no-items", "&cYou don't have any items that can be sold!");
        loadOrSaveDefault("sell.delay", "&cYou must wait :delay-formated: to sell again!");
        loadOrSaveDefault("sell.autosell.on", "&aYou have activated the Auto-Sell!");
        loadOrSaveDefault("sell.autosell.off", "&cYou have deactivated the Auto-Sell!");
        loadOrSaveDefault("sell.shiftsell.on", "&aYou have activated the Shift-Sell!");
        loadOrSaveDefault("sell.shiftsell.off", "&cYou have deactivated the Shift-Sell!");
        loadOrSaveDefault("sell.menu.title", "Sell menu!");
        loadOrSaveDefault("sell.menu.sell", "DOUBLE_PLANT 1 name:&aSell lore:&7Click_here_to_sell_your_inventory!");
        loadOrSaveDefault("sell.menu.shiftsell.no-permission", "LEVER 1 name:&aShift-Sell " +
                "lore:&7Sell_your_inventory_by_sneaking|" +
                "|" +
                "&7(Insufficient_permissions)");
        loadOrSaveDefault("sell.menu.shiftsell.on", "LEVER 1 name:&aShift-Sell " +
                "lore:&7Sell_your_inventory_by_sneaking|" +
                "|" +
                "&7Current_state->_&aActive|" +
                "&7(Click_to_deactivate)");
        loadOrSaveDefault("sell.menu.shiftsell.off", "LEVER 1 name:&cShift-Sell " +
                "lore:&7Sell_your_inventory_by_sneaking|" +
                "|" +
                "&7Current_state->_&cInactive|" +
                "&7(Click_to_activate)");
        loadOrSaveDefault("sell.menu.autosell.no-permission", "LEVER 1 name:&aAuto-Sell " +
                "lore:&7Automatically_sell_your_inventory_when_full.|" +
                "|" +
                "&7(Insufficient_permissions)");
        loadOrSaveDefault("sell.menu.autosell.on", "LEVER 1 name:&aAuto-Sell " +
                "lore:&7Automatically_sell_your_inventory_when_full.|" +
                "|" +
                "&7Current_state->_&aActive|" +
                "&7(Click_to_deactivate)");
        loadOrSaveDefault("sell.menu.autosell.off", "LEVER 1 name:&cAuto-Sell " +
                "lore:&7Automatically_sell_your_inventory_when_full.|" +
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

        final String message = config.getString(messageId, defaultMessage, true);
        if (!message.equals("none")){
            messages.put(messageId, message);
        }
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
