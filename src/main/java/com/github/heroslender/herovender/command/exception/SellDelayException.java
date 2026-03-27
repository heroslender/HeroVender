package com.github.heroslender.herovender.command.exception;

import com.github.heroslender.herovender.Message;
import com.github.heroslender.herovender.data.SellReason;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.utils.HeroException;
import com.github.heroslender.herovender.utils.NumberUtil;
import io.github.miniplaceholders.api.Expansion;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;

public class SellDelayException extends HeroException {
    @Getter private final User user;
    /**
     * The delay the user has to wait to sell the inventory
     * again, in milliseconds.
     */
    @Getter private final long delay;

    @Getter private final SellReason reason;

    public SellDelayException(User user, long delay, SellReason reason) {
        this.user = user;
        this.delay = delay;
        this.reason = reason;
    }

    public void sendUserMessage() {
        String msg = switch (reason) {
            case COMMAND -> Message.SellCommandChatDelay;
            case SHIFT -> Message.SellShiftChatDelay;
            default -> null;
        };

        if (msg == null) {
            return;
        }

        Expansion expansion = Expansion.builder("internal")
                .globalPlaceholder("delay", (queue, ctx) -> Tag.selfClosingInserting(Component.text(getDelay())))
                .globalPlaceholder("delay-formated", (queue, ctx) -> Tag.selfClosingInserting(Component.text(NumberUtil.format(getDelay()))))
                .build();

        user.sendMiniMessage(msg, expansion);
    }
}
