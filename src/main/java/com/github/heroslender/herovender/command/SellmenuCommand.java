//package com.github.heroslender.herovender.command;
//
//import com.github.heroslender.herovender.HeroVender;
//import com.github.heroslender.herovender.command.exception.CommandAllowsPlayersOnlyException;
//import com.github.heroslender.herovender.menu.impl.AutosellMenu;
//import com.github.heroslender.herovender.utils.HeroException;
//import lombok.val;
//import org.bukkit.command.CommandSender;
//
//public class SellmenuCommand extends Command {
//    public SellmenuCommand() {
//        super("sellmenu");
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, String label, String[] args) throws HeroException {
//        val user = getUser(sender).orElseThrow(CommandAllowsPlayersOnlyException::new);
//
//        new AutosellMenu(HeroVender.getInstance().getMessageController(), user);
//        return true;
//    }
//}
