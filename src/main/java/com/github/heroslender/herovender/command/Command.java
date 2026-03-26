package com.github.heroslender.herovender.command;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.helpers.MessageBuilder;
import com.github.heroslender.herovender.utils.HeroException;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public abstract class Command implements BasicCommand {
    private static final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("commands");
    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String permissionMessage;
    @Getter private final String usage;


    public Command(String defaultName) {
        String name = defaultName;
        List<String> aliases = Collections.emptyList();
        String permission = null;
        String permissionMessage = "§cYou do not have permission for that!";
        String usage = "§c/" + name;

        if (config != null) {
            name = config.getString(defaultName + ".name", defaultName);
            aliases = config.getStringList(defaultName + ".aliases");
            permission = config.getString(defaultName + ".permission");

            val messageBuilder = new MessageBuilder();
            permissionMessage = messageBuilder.build(config.getString(defaultName + ".permission-message", permissionMessage));
            usage = messageBuilder.build(config.getString(defaultName + ".usage", usage));
        }

        this.name = name;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.usage = usage;

        HeroVender.getInstance().registerCommand(name, aliases, this);
    }

    @Override
    public void execute(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        try {
            if (permission == null || commandSourceStack.getSender().hasPermission(permission)) {
                onCommand(commandSourceStack.getSender(), args);
                return;
            }
            commandSourceStack.getSender().sendMessage(permissionMessage);
        } catch (HeroException e) {
            commandSourceStack.getSender().sendMessage(e.getMessage());
        } catch (Exception e) {
            HeroVender.getInstance().getLogger().log(Level.SEVERE, "There was an error executing the command \"/" + getName() + "\"", e);
        }
    }

    public abstract void onCommand(CommandSender sender, String[] args) throws HeroException;

    protected Optional<User> getUser(CommandSender sender) {
        if (sender instanceof Player) {
            return Optional.of(HeroVender.getInstance().getUserController().getOrCreate((Player) sender));
        }

        return Optional.empty();
    }
}
