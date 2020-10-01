package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class HerovenderCommand extends Command {

    public HerovenderCommand() {
        super("herovender");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                HeroVender.getInstance().reload();
                sender.sendMessage("§bHeroVender §7- §aPlugin reloaded!");
                return true;
            } else if (args[0].equalsIgnoreCase("item") && sender instanceof Player) {
                val item = ((Player) sender).getInventory().getItemInHand();
                MaterialData data = item.getData();
                val sb = new StringBuilder(ChatColor.GREEN.toString());
                sb.append("Item: ");
                sb.append(item.getType().name());
                sb.append(ChatColor.YELLOW);
                if (item.getDurability() != 0) {
                    sb.append(":").append(item.getData());
                }

                sender.sendMessage(sb.toString());
                return true;
            }
        }
        return false;
    }
}
