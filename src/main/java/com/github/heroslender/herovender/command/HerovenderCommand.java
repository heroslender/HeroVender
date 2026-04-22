package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.Shop;
import com.github.heroslender.herovender.data.ShopItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HerovenderCommand extends Command {

    public HerovenderCommand() {
        super("herovender");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, getUsage());
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "item" -> handleItem(sender);
            case "shop" -> handleShop(sender, args);
            default -> sendMessage(sender, getUsage());
        }
    }

    @Override
    public @NonNull List<String> suggest(@NonNull CommandSourceStack commandSourceStack, String [] args) {
        if (args.length == 0) {
            return List.of("reload", "item", "additem", "shop");
        }

        if (args.length == 1) {
            return filter(List.of("reload", "item", "additem", "shop"), args[0]);
        }

        if (args[0].equalsIgnoreCase("shop")) {
            if (args.length == 2) {
                List<String> options = new ArrayList<>(List.of("list", "create", "remove"));
                HeroVender.getInstance().getShopController().getShops().forEach(shop -> options.add(shop.getName()));
                return filter(options, args[1]);
            }

            if (args.length == 3) {
                String sub = args[1].toLowerCase();
                if (sub.equals("list") || sub.equals("create") || sub.equals("remove")) {
                    return Collections.emptyList();
                }

                Shop shop = getShop(sub);
                if (shop != null) {
                    return filter(List.of("items", "priority"), args[2]);
                }
            }

            if (args.length == 4 && args[2].equalsIgnoreCase("items")) {
                return filter(List.of("add", "remove", "setprice", "setitem"), args[3]);
            }

            if (args.length == 5 && args[2].equalsIgnoreCase("items")) {
                String action = args[3].toLowerCase();
                if (action.equals("remove") || action.equals("setprice") || action.equals("setitem")) {
                    Shop shop = getShop(args[1]);
                    if (shop != null) {
                        List<String> indices = new ArrayList<>();
                        for (int i = 0; i < shop.getItems().size(); i++) {
                            indices.add(String.valueOf(i));
                        }
                        return filter(indices, args[4]);
                    }
                }
            }
        }

        if (args[0].equalsIgnoreCase("additem") && args.length == 2) {
            List<String> options = new ArrayList<>();
            HeroVender.getInstance().getShopController().getShops().forEach(shop -> options.add(shop.getName()));
            return filter(options, args[1]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> options, String filter) {
        val lowercaseFilter = filter.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lowercaseFilter))
                .toList();
    }

    private void handleReload(CommandSender sender) {
        HeroVender.getInstance().reload();
        sendMessage(sender, "<aqua>HeroVender <gray>- <green>Plugin reloaded!");
    }

    private void handleItem(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "<red>Only players can use this command!");
            return;
        }
        val item = player.getInventory().getItemInMainHand();
        sendMessage(sender, "<green>Item: <yellow>" + item.getType().name());
    }

    private void handleShop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /herovender shop <list|create|remove|[shop]> ...");
            return;
        }

        String sub = args[1].toLowerCase();
        switch (sub) {
            case "list" -> handleShopList(sender);
            case "create" -> handleShopCreate(sender, args);
            case "remove" -> handleShopRemove(sender, args);
            default -> handleSpecificShop(sender, sub, args);
        }
    }

    private void handleShopList(CommandSender sender) {
        sendMessage(sender, "<aqua>HeroVender <gray>- <dark_aqua>Shops:");
        for (Shop shop : HeroVender.getInstance().getShopController().getShops()) {
            sendMessage(sender, "<gray> - <green>" + shop.getName() + " <dark_gray>(Priority: " + shop.getPriority() + ")");
        }
    }

    private void handleShopCreate(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /herovender shop create <name>");
            return;
        }
        String name = args[2];
        if (getShop(name) != null) {
            sendMessage(sender, "<red>Shop already exists!");
            return;
        }
        HeroVender.getInstance().getConfig().set("shops." + name + ".priority", 1);
        saveAndReload();
        sendMessage(sender, "<green>Shop <dark_green>" + name + " <green>created!");
    }

    private void handleShopRemove(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /herovender shop remove <name>");
            return;
        }
        String name = args[2];
        if (getShop(name) == null) {
            sendMessage(sender, "<red>Shop not found!");
            return;
        }
        HeroVender.getInstance().getConfig().set("shops." + name, null);
        saveAndReload();
        sendMessage(sender, "<green>Shop <dark_green>" + name + " <green>removed!");
    }

    private void handleSpecificShop(CommandSender sender, String shopName, String[] args) {
        Shop shop = getShop(shopName);
        if (shop == null) {
            sendMessage(sender, "<red>Unknown shop or subcommand!");
            return;
        }

        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /herovender shop " + shopName + " <items|priority> ...");
            return;
        }

        switch (args[2].toLowerCase()) {
            case "priority" -> handleShopPriority(sender, shop, args);
            case "items" -> handleShopItems(sender, shop, args);
            default -> sendMessage(sender, "<red>Usage: /herovender shop " + shopName + " <items|priority> ...");
        }
    }

    private void handleShopPriority(CommandSender sender, Shop shop, String[] args) {
        if (args.length < 4) {
            sendMessage(sender, "<aqua>Shop <green>" + shop.getName() + " <aqua>priority: <yellow>" + shop.getPriority());
            return;
        }
        try {
            int priority = Integer.parseInt(args[3]);
            HeroVender.getInstance().getConfig().set("shops." + shop.getName() + ".priority", priority);
            saveAndReload();
            sendMessage(sender, "<green>Shop priority updated!");
        } catch (NumberFormatException e) {
            sendMessage(sender, "<red>Priority must be a number!");
        }
    }

    private void handleShopItems(CommandSender sender, Shop shop, String[] args) {
        ConfigurationSection itemsSection = HeroVender.getInstance().getConfig()
                .getConfigurationSection("shops." + shop.getName() + ".items");

        if (args.length == 3) {
            displayShopItems(sender, shop);
            return;
        }

        switch (args[3].toLowerCase()) {
            case "add" -> handleItemAdd(sender, shop, args);
            case "remove" -> handleItemRemove(sender, shop, itemsSection, args);
            case "setprice" -> handleItemSetPrice(sender, shop, itemsSection, args);
            case "setitem" -> handleItemSetItem(sender, shop, itemsSection, args);
            default -> sendMessage(sender, "<red>Unknown action! Use add, remove, setprice or setitem.");
        }
    }

    private void displayShopItems(CommandSender sender, Shop shop) {
        sendMessage(sender, "<aqua>Items in shop <green>" + shop.getName() + "<aqua>:");
        int i = 0;
        for (ShopItem item : shop.getItems()) {
            sendMessage(sender, "<gray>" + (i++) + ". <white>" + item.getItemStack().getType().name() + " <gray>- <gold>" + item.getPrice());
        }
    }

    private void handleItemAdd(CommandSender sender, Shop shop, String[] args) {
        if (args.length < 5) {
            sendMessage(sender, "<red>Usage: /herovender shop " + shop.getName() + " items add <price>");
            return;
        }
        handleAddItem(sender, new String[]{"additem", shop.getName(), args[4]});
    }

    private void handleItemRemove(CommandSender sender, Shop shop, ConfigurationSection section, String[] args) {
        if (args.length < 5) {
            sendMessage(sender, "<red>Usage: /herovender shop " + shop.getName() + " items remove <index>");
            return;
        }
        processItemIndex(sender, section, args[4], (key, idx) -> {
            section.set(key, null);
            saveAndReload();
            sendMessage(sender, "<green>Item removed from shop!");
        });
    }

    private void handleItemSetPrice(CommandSender sender, Shop shop, ConfigurationSection section, String[] args) {
        if (args.length < 6) {
            sendMessage(sender, "<red>Usage: /herovender shop " + shop.getName() + " items setprice <index> <price>");
            return;
        }
        try {
            double price = Double.parseDouble(args[5]);
            processItemIndex(sender, section, args[4], (key, idx) -> {
                section.set(key + ".price", price);
                saveAndReload();
                sendMessage(sender, "<green>Item price updated!");
            });
        } catch (NumberFormatException e) {
            sendMessage(sender, "<red>Price must be a number!");
        }
    }

    private void handleItemSetItem(CommandSender sender, Shop shop, ConfigurationSection section, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "<red>Only players can use this command!");
            return;
        }
        if (args.length < 5) {
            sendMessage(sender, "<red>Usage: /herovender shop " + shop.getName() + " items setitem <index>");
            return;
        }
        val handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()) {
            sendMessage(sender, "<red>You must be holding an item!");
            return;
        }

        processItemIndex(sender, section, args[4], (key, idx) -> {
            if (handItem.hasItemMeta()) {
                section.set(key + ".item", handItem);
            } else {
                section.set(key + ".item", handItem.getType().key().asString());
            }
            saveAndReload();
            sendMessage(sender, "<green>Item updated!");
        });
    }

    private void handleAddItem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "<red>Only players can use this command!");
            return;
        }
        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /herovender additem <shop> <price>");
            return;
        }

        val shop = getShop(args[1]);
        if (shop == null) {
            sendMessage(sender, "<red>Shop not found!");
            return;
        }

        try {
            double price = Double.parseDouble(args[2]);
            val handItem = player.getInventory().getItemInMainHand();
            if (handItem.getType().isAir()) {
                sendMessage(sender, "<red>You must be holding an item!");
                return;
            }

            String itemName = handItem.getType().name();
            if (handItem.getItemMeta().hasCustomName()) {
                Component customName = handItem.getItemMeta().customName();
                if (customName != null) {
                    itemName = PlainTextComponentSerializer.plainText().serialize(customName).replaceAll("[^a-zA-Z0-9]", "_");
                }
            }
            String key = itemName + "_" + System.currentTimeMillis();
            String cfgPath = "shops." + shop.getName() + ".items." + key;

            if (handItem.hasItemMeta()) {
                HeroVender.getInstance().getConfig().set(cfgPath + ".item", handItem);
            } else {
                HeroVender.getInstance().getConfig().set(cfgPath + ".item", handItem.getType().key().asString());
            }
            HeroVender.getInstance().getConfig().set(cfgPath + ".price", price);
            saveAndReload();

            sendMessage(sender, "<green>Item added to shop!");
        } catch (NumberFormatException e) {
            sendMessage(sender, "<red>Invalid price!");
        }
    }

    private void processItemIndex(CommandSender sender, ConfigurationSection section, String indexStr, IndexAction action) {
        if (section == null) {
            sendMessage(sender, "<red>Shop has no items!");
            return;
        }
        try {
            int index = Integer.parseInt(indexStr);
            List<String> keys = new ArrayList<>(section.getKeys(false));
            if (index < 0 || index >= keys.size()) {
                sendMessage(sender, "<red>Invalid index!");
                return;
            }
            action.run(keys.get(index), index);
        } catch (NumberFormatException e) {
            sendMessage(sender, "<red>Index must be a number!");
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    private Shop getShop(String shopName) {
        for (Shop shop : HeroVender.getInstance().getShopController().getShops()) {
            if (shop.getName().equalsIgnoreCase(shopName)) {
                return shop;
            }
        }
        return null;
    }

    private void saveAndReload() {
        HeroVender.getInstance().saveConfig();
        HeroVender.getInstance().reload();
    }

    @FunctionalInterface
    private interface IndexAction {
        void run(String key, int index);
    }
}
