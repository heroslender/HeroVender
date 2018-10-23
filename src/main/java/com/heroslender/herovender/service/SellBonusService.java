package com.heroslender.herovender.service;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.data.PermissionSellBonus;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellBonusService implements Service<PermissionSellBonus> {
    private final List<PermissionSellBonus> bonuses = new ArrayList<>();

    @Override
    public void init() {
        final ConfigurationSection config = HeroVender.getInstance().getConfig().getConfigurationSection("bonus");
        if (config != null) {
            for (String id : config.getKeys(false)) {
                double bonus = config.getDouble(id + ".bonus", -1);
                if (bonus > 0) {
                    val permission = config.getString(id + ".permission", null);
                    val bonusName = config.getString(id + ".name", "Bonus " + permission);
                    bonuses.add(new PermissionSellBonus(bonusName, bonus, permission));
                }
            }
        }
    }

    @Override
    public List<PermissionSellBonus> get() {
        return bonuses;
    }

    @Override
    public Optional<PermissionSellBonus> getById(String id) {
        return bonuses.stream()
                .filter(bonus -> bonus.getPermission() != null && bonus.getPermission().equalsIgnoreCase(id))
                .findAny();
    }

    @Override
    public void stop() {
        bonuses.clear();
    }
}
