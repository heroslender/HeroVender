package com.heroslender.herovender.data;

import lombok.Getter;

public class PermissionSellBonus extends SellBonus {
    @Getter private final String permission;

    public PermissionSellBonus(String name, double bonus, String permission) {
        super(name, bonus);
        this.permission = permission;
    }
}
