package com.heroslender.herovender.data;

import lombok.Getter;
import lombok.Setter;

public enum SellReason {
    COMMAND,
    AUTO,
    SHIFT,
    CUSTOM;



    @Getter
    @Setter
    private boolean chat;

    @Getter
    @Setter
    private boolean actionbar;

    @Getter
    @Setter
    private boolean ignoreEmpty;

}
