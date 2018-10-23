package com.heroslender.herovender.controller;

import com.heroslender.herovender.data.SellBonus;
import com.heroslender.herovender.service.SellBonusService;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Optional;

public class SellBonusController implements Controller {
    private final SellBonusService sellBonusService;

    public SellBonusController(SellBonusService sellBonusService) {
        this.sellBonusService = sellBonusService;
    }

    public Optional<SellBonus> getBonusForPlayer(Player player) {
        return sellBonusService.get().stream()
                .filter(bonus -> bonus.getPermission() == null || player.hasPermission(bonus.getPermission()))
                .map(bonus -> (SellBonus) bonus)
                // Deve ter acesso apenas ao bonus mais alto que tem permissao ou a todos que tem permissao?!?
                .max(Comparator.comparingDouble(SellBonus::getBonus));
    }
}
