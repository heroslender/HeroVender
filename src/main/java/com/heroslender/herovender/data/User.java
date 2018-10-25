package com.heroslender.herovender.data;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.command.exception.SellDelayException;
import com.heroslender.herovender.helpers.MessageBuilder;
import com.heroslender.herovender.utils.NumberUtil;
import lombok.Data;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

@Data
public class User {
    private final Player player;
    private boolean shiftSellActive;
    private long sellDelay;

    public User(Player player) {
        this.player = player;
        shiftSellActive = false;
        sellDelay = 0;
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

    public void checkDelay() throws SellDelayException {
        val delay = HeroVender.getInstance().getUserController().getDelay(getPlayer());

        val releaseTimestamp = sellDelay + delay;
        if (delay > 0 && releaseTimestamp > System.currentTimeMillis()) {
            val toWait = releaseTimestamp - System.currentTimeMillis();
            val messageBuilder = new MessageBuilder()
                    .withPlaceholder("delay", toWait)
                    .withPlaceholder("delay-formated", NumberUtil.format(toWait / 1000D))
                    .withPlaceholder(this);
            val message = HeroVender.getInstance().getMessageController().getMessage("sell.delay").orElse("&cYou must wait :delay: to sell again!");

            throw new SellDelayException(messageBuilder.build(message), toWait);
        }

        setSellDelay(System.currentTimeMillis());
    }
}
