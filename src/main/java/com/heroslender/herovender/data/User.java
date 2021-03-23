package com.heroslender.herovender.data;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import lombok.Data;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

@Data
public class User {
    private final Player player;
    private boolean shiftSellActive;
    private boolean autoSellActive;
    private long sellDelay;

    public User(Player player) {
        this.player = player;
        shiftSellActive = false;
        sellDelay = 0;
    }

    public Shop[] getShops() {
        return HeroVender.getInstance().getShopController().getShops(this);
    }

    public Inventory getInventory() {
        return getPlayer().getInventory();
    }

    public Optional<SellBonus> getSellBonus() {
        return HeroVender.getInstance().getSellBonusController().getBonusForPlayer(getPlayer());
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public void toggleShiftSell() {
        setShiftSellActive(!isShiftSellActive());
    }

    public void toggleAutoSell() {
        setAutoSellActive(!isAutoSellActive());
    }

    public void checkDelay() throws SellDelayException {
        val delay = HeroVender.getInstance().getUserController().getDelay(getPlayer());
        if (delay <= 0) {
            return;
        }

        val releaseTimestamp = sellDelay + delay;
        if (releaseTimestamp > System.currentTimeMillis()) {
            throw new SellDelayException(this, releaseTimestamp - System.currentTimeMillis());
        }

        setSellDelay(System.currentTimeMillis());
    }
}
