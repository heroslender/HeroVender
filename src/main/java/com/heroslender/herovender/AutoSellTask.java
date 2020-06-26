package com.heroslender.herovender;

import com.heroslender.herovender.listener.autosell.AutoSellLogic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AutoSellTask extends AutoSellLogic implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            autoSell(player);
        }
    }
}
