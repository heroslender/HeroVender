package com.heroslender.herovender.autosell;

public enum AutoSellStategy {
    /**
     * Sell when the player picks up an item.
     */
    PICKUP_ITEM,
    /**
     * Sell every x ticks, where x is given in the configuration.
     */
    TIMER
}
