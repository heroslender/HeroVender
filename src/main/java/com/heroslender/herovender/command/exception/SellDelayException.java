package com.heroslender.herovender.command.exception;

import com.heroslender.herovender.utils.HeroException;
import lombok.Getter;

public class SellDelayException extends HeroException {
    /**
     * The delay the user has to wait to sell the inventory
     * again, in miliseconds.
     */
    @Getter private final long delay;

    public SellDelayException(String message, long delay) {
        super(message);
        this.delay = delay;
    }
}
