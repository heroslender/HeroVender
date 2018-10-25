package com.heroslender.herovender.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Invoice {
    private final List<SellItem> items;
    private final List<SellBonus> bonuses;

    public Invoice(List<SellItem> items) {
        this.items = items;
        bonuses = new ArrayList<>();
    }

    /**
     * Get the amout of items in the invoice
     * @return Amount of items
     */
    public int getItemCount() {
        return getItems().stream().mapToInt(SellItem::getAmount).sum();
    }

    /**
     * Get the total value, not including the bonuses
     *
     * @return Total value
     */
    public double getTotalClean() {
        return getItems().stream()
                .mapToDouble(sellItem -> sellItem.getPrice() * sellItem.getAmount())
                .sum();
    }

    /**
     * Get the total value including the bonuses
     *
     * @return Total value
     */
    public double getTotal() {
        return getTotalClean() * (1 + getBonuses().stream().mapToDouble(SellBonus::getBonus).sum());
    }
}
