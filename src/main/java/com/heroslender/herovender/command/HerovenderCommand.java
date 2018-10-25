package com.heroslender.herovender.command;

import com.heroslender.herovender.HeroVender;
import org.bukkit.command.CommandSender;

public class HerovenderCommand extends Command {

    public HerovenderCommand() {
        super("herovender");
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")){
                HeroVender.getInstance().reload();
                sender.sendMessage("§bHeroVender §7- §aPlugin reloaded!");
                return true;
            }
        }
        return false;
    }
}
