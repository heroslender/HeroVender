package com.heroslender.herovender.event;

import com.heroslender.herovender.data.Invoice;
import com.heroslender.herovender.data.SellItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class PlayerSellEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    @Getter @Setter private Invoice invoice;

    public PlayerSellEvent(Player player, Invoice invoice) {
        super(player);
        this.invoice = invoice;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public List<SellItem> getItems() {
        return invoice.getItems();
    }

    public int getTotalItems() {
        return invoice.getItems().stream().mapToInt(SellItem::getAmount).sum();
    }

    public double getTotalPrice() {
        return invoice.getItems().stream()
                .mapToDouble(sellItem -> sellItem.getPrice() * sellItem.getAmount())
                .sum();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
