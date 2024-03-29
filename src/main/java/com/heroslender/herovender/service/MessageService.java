package com.heroslender.herovender.service;

import com.heroslender.herovender.data.SellReason;
import com.heroslender.herovender.helpers.CustomFileConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageService implements Service<String> {
    private final CustomFileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();
    private final Map<String, List<String>> messagesLists = new HashMap<>();

    @Override
    public void init() {
        config.reload();

        setDefault("sell.command.actionbar", false);
        setDefault("sell.command.chat", true);
        setDefault("sell.command.ignore-empty", false);

        setDefault("sell.autosell.actionbar", true);
        setDefault("sell.autosell.chat", false);
        setDefault("sell.autosell.ignore-empty", true);

        setDefault("sell.shiftsell.actionbar", true);
        setDefault("sell.shiftsell.chat", false);
        setDefault("sell.shiftsell.ignore-empty", false);

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
        loadOrSaveDefault("sell.menu.sell-prices", "ANVIL 1 name:&aPrices lore:&7Click_here_to_see_your_sell_prices!");
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

        loadOrSaveDefault("prices.menu.title", "Sell menu!");
        loadOrSaveDefault("prices.menu.prev-page", "ARROW 1 name:&aPrev_Page lore:&7||&bClick_to_move");
        loadOrSaveDefault("prices.menu.next-page", "ARROW 1 name:&aNext_page lore:&7||&bClick_to_move");
        loadOrSaveDefault("prices.menu.item.lore", "", "&7Price per unit: &a$&f:price-formatted:", "", ":lore:");


        SellReason.COMMAND.setChat(getOrSaveDefault("sell.command.chat", true));
        SellReason.COMMAND.setActionbar(getOrSaveDefault("sell.command.actionbar", true));
        SellReason.COMMAND.setIgnoreEmpty(getOrSaveDefault("sell.command.ignore-empty", true));
        SellReason.AUTO.setChat(getOrSaveDefault("sell.autosell.chat", true));
        SellReason.AUTO.setActionbar(getOrSaveDefault("sell.autosell.actionbar", true));
        SellReason.AUTO.setIgnoreEmpty(getOrSaveDefault("sell.autosell.ignore-empty", true));
        SellReason.SHIFT.setChat(getOrSaveDefault("sell.shiftsell.chat", true));
        SellReason.SHIFT.setActionbar(getOrSaveDefault("sell.shiftsell.actionbar", true));
        SellReason.SHIFT.setIgnoreEmpty(getOrSaveDefault("sell.shiftsell.ignore-empty", true));
        SellReason.CUSTOM.setChat(getOrSaveDefault("sell.custom.chat", true));
        SellReason.CUSTOM.setActionbar(getOrSaveDefault("sell.custom.actionbar", true));
        SellReason.CUSTOM.setIgnoreEmpty(getOrSaveDefault("sell.custom.ignore-empty", true));
    }

    private boolean getOrSaveDefault(String path, boolean defaultValue) {
        if (config == null) {
            return defaultValue;
        }

        if (!config.contains(path)) {
            config.set(path, defaultValue);
            config.save();
        }

        return config.getBoolean(path);
    }

    private void setDefault(final String key, final Object value) {
        if (config == null) {
            return;
        }

        if (!config.contains(key)) {
            config.set(key, value);
            config.save();
        }
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
        if (!message.equals("none")) {
            messages.put(messageId, message);
        }
    }

    private void loadOrSaveDefault(final String messageId, final String... defaultMessage) {
        List<String> messages = Arrays.asList(defaultMessage);
        if (config == null) {
            messagesLists.put(messageId, messages);
            return;
        }

        if (!config.contains(messageId)) {
            config.set(messageId, messages);
            config.save();
        }

        final List<String> message = config.getStringList(messageId);
        if (!message.isEmpty()) {
            messagesLists.put(
                    messageId,
                    message.stream()
                            .map(it -> ChatColor.translateAlternateColorCodes('&', it))
                            .collect(Collectors.toList())
            );
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

    public Optional<List<String>> getListById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        val message = messagesLists.get(id);

        if (message == null || message.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(message);
    }

    @Override
    public void stop() {
        messages.clear();
    }
}
