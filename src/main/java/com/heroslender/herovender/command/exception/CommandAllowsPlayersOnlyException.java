package com.heroslender.herovender.command.exception;

import com.heroslender.herovender.utils.HeroException;
import org.bukkit.ChatColor;

/**
 * Exception thrown when the {@link org.bukkit.command.Command} only allows players to execute it.
 */
public class CommandAllowsPlayersOnlyException extends HeroException {
    public CommandAllowsPlayersOnlyException() {
        super(ChatColor.RED + "Only players are allowed to sell!");
    }
}
