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

    private final SellReason reason;

    public Invoice(List<SellItem> items, SellReason reason) {
        this.items = items;
        this.bonuses = new ArrayList<>();
        this.reason = reason;
    }

    /**
     * Get the amout of items in the invoice
     *
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
        double total = 0;

        for (SellItem item : getItems()) {
            total += item.getPrice() * item.getAmount();
        }

        return total;
    }

    /**
     * Get the total bonus percentage to add to the total value.
     *
     * @return bonus percentage
     */
    private double getBonusesTotal() {
        double total = 0;

        for (SellBonus bonus : getBonuses()) {
            total += bonus.getBonus();
        }

        return total;
    }

    /**
     * Get the total value including the bonuses
     *
     * @return Total value
     */
    public double getTotal() {
        return getTotalClean() * (1 + getBonusesTotal());
    }
}
