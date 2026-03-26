package com.github.heroslender.herovender.command.exception;

import com.github.heroslender.herovender.HeroVender;
import com.github.heroslender.herovender.data.User;
import com.github.heroslender.herovender.helpers.MessageBuilder;
import com.github.heroslender.herovender.utils.HeroException;
import com.github.heroslender.herovender.utils.NumberUtil;
import lombok.Getter;
import lombok.val;

public class SellDelayException extends HeroException {
    @Getter private final User user;
    /**
     * The delay the user has to wait to sell the inventory
     * again, in milliseconds.
     */
    @Getter private final long delay;

    public SellDelayException(User user, long delay) {
        this.user = user;
        this.delay = delay;
    }

    @Override
    public String getMessage() {
        val messageBuilder = new MessageBuilder()
                .withPlaceholder("delay", getDelay())
                .withPlaceholder("delay-formated", NumberUtil.format(getDelay() / 1000D))
                .withPlaceholder(getUser());
        val message = HeroVender.getInstance().getMessageController().getMessage("sell.delay").orElse("&cYou must wait :delay: to sell again!");

        return messageBuilder.build(message);
    }
}
