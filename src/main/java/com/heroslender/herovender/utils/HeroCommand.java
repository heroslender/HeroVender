package com.heroslender.herovender.utils;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.exception.HeroException;
import com.heroslender.herovender.helpers.MessageBuilder;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;

public abstract class HeroCommand extends Command {
    private static final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("commands");

    public HeroCommand(String defaultName) {
        super(config.getString(defaultName + ".name", defaultName));

        val aliases = config.getStringList(defaultName + ".aliases");
        setAliases(aliases != null ? aliases : Collections.emptyList());

        val messageBuilder = new MessageBuilder();
        setUsage(messageBuilder.build(config.getString(defaultName + ".usage", "§c/" + getName())));

        setPermission(messageBuilder.build(config.getString(defaultName + ".permisson")));
        setPermissionMessage(messageBuilder.build(config.getString(defaultName + ".permisson-message")));

        NmsUtils.registerCommand(HeroVender.getInstance().getDescription().getName(), this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            if (testPermission(sender)) {
                return onCommand(sender, label, args);
            }
            return true;
        } catch (HeroException e) {
            sender.sendMessage(e.getMessage());
            return true;
        } catch (Exception e) {
            HeroVender.getInstance().getLogger().log(Level.SEVERE, "There was an error executing the command \"/" + getName() + "\"", e);
            return false;
        }
    }

    public abstract boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException;

    protected Optional<User> getUser(CommandSender sender) {
        if (sender instanceof Player) {
            return Optional.of(HeroVender.getInstance().getUserController().getOrCreate((Player) sender));
        }

        return Optional.empty();
    }
}
