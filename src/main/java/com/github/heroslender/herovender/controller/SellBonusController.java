package com.github.heroslender.herovender.controller;

import com.github.heroslender.herovender.data.PermissionSellBonus;
import com.github.heroslender.herovender.data.SellBonus;
import com.github.heroslender.herovender.service.SellBonusService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SellBonusController {
    private final SellBonusService sellBonusService;

    public SellBonusController(SellBonusService sellBonusService) {
        this.sellBonusService = sellBonusService;
    }

    public List<SellBonus> getBonusesForPlayer(Player player) {
        List<SellBonus> collector = new ArrayList<>();
        for (PermissionSellBonus bonus : sellBonusService.get()) {
            if (bonus.getPermission() == null || player.hasPermission(bonus.getPermission())) {
                collector.add(bonus);
            }
        }

        return collector;
    }
}
