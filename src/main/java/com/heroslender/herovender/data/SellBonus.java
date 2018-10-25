package com.heroslender.herovender.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellBonus {
    private final String name;
    /**
     * Bonus in percentage, between 0 and 1
     */
    private final double bonus;
}
