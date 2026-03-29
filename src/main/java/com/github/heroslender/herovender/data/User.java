package com.github.heroslender.herovender.data;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.command.exception.SellDelayException;
import io.github.miniplaceholders.api.Expansion;
import lombok.Data;
import lombok.val;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class User {
    private final Player player;
    private boolean shiftSellActive;
    private boolean autoSellActive;
    private long sellDelay;

    private BukkitTask autoSellTask;


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

    public List<SellBonus> getSellBonuses() {
        return HeroVender.getInstance().getSellBonusController().getBonusesForPlayer(getPlayer());
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public void sendMiniMessage(String message) {
        getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(message, getPlayer()));
    }

    public void sendMiniMessage(String message, Expansion expansion) {
        sendMiniMessage(message, expansion.globalPlaceholders());
    }

    public void sendMiniMessage(String message, TagResolver tagResolver) {
        getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(message, getPlayer(), tagResolver));
    }

    public void toggleShiftSell() {
        setShiftSellActive(!isShiftSellActive());
    }

    public void toggleAutoSell() {
        setAutoSellActive(!isAutoSellActive());
    }

    public void checkDelay(@NotNull SellReason reason) throws SellDelayException {
        val delay = HeroVender.getInstance().getUserController().getDelay(getPlayer());
        if (delay <= 0) {
            return;
        }

        val releaseTimestamp = sellDelay + delay;
        if (releaseTimestamp > System.currentTimeMillis()) {
            throw new SellDelayException(this, releaseTimestamp - System.currentTimeMillis(), reason);
        }

        setSellDelay(System.currentTimeMillis());
    }
}
