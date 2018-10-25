package com.heroslender.herovender.event;

import com.heroslender.herovender.data.Invoice;
import com.heroslender.herovender.data.SellItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class PlayerSellEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    @Getter @Setter private Invoice invoice;
    private Boolean cancel;

    public PlayerSellEvent(Player player, Invoice invoice) {
        super(player);
        this.invoice = invoice;
        this.cancel = false;
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
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
