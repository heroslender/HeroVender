package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.Shop;
import com.github.heroslender.herovender.data.ShopItem;
import lombok.val;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HerovenderCommand extends Command {

    public HerovenderCommand() {
        super("herovender");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                HeroVender.getInstance().reload();
                sender.sendMessage("§bHeroVender §7- §aPlugin reloaded!");
                return;
            } else if (args[0].equalsIgnoreCase("item") && sender instanceof Player) {
                val item = ((Player) sender).getInventory().getItemInHand();

                val sb = new StringBuilder(ChatColor.GREEN.toString());
                sb.append("Item: ");
                sb.append(item.getType().name());
                sb.append(ChatColor.YELLOW);
                if (item.getDurability() != 0) {
                    sb.append(":").append(item.getDurability());
                }

                sender.sendMessage(sb.toString());
                return;
            } else if (args[0].equalsIgnoreCase("additem")) {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /herovender additem <shop> <price>");
                    return;
                }

                val shop = getShop(args[1]);
                if (shop == null) {
                    sender.sendMessage("§cShop not found!");
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid price!");
                    return;
                }
                val handItem = ((Player) sender).getInventory().getItemInMainHand();
                if (handItem.getType().isAir()) {
                    sender.sendMessage("§cYou must be holding an item!");
                    return;
                }

                val shopItem = new ShopItem(handItem, price);
                shop.getItems().add(shopItem);

                String itemName = handItem.getType().name();
                if (handItem.getItemMeta().hasCustomName()) {
                    // Simple sanitization for config key
                    itemName = ((TextComponent) handItem.getItemMeta().customName()).content().replaceAll("[^a-zA-Z0-9]", "_");
                }
                // Ensure unique key
                String key = itemName + "_" + System.currentTimeMillis();

                String cfgPath = "shops." + shop.getName() + ".items." + key;
                if (handItem.hasItemMeta()) {
                    HeroVender.getInstance().getConfig().set(cfgPath + ".item", handItem);
                } else {
                    HeroVender.getInstance().getConfig().set(cfgPath + ".item", handItem.getType().key().asString());

                }
                HeroVender.getInstance().getConfig().set(cfgPath + ".price", price);
                HeroVender.getInstance().saveConfig();

                sender.sendMessage("§aItem added to shop!");
                return;
            }
        }

        sender.sendMessage(getUsage());
    }

    private Shop getShop(String shopName) {
        for (Shop shop : HeroVender.getInstance().getShopController().getShops()) {
            if (shop.getName().equalsIgnoreCase(shopName)) {
                return shop;
            }
        }

        return null;
    }
}
