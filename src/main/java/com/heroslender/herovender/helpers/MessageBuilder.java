package com.heroslender.herovender.helpers;

import com.google.common.collect.Lists;
import com.heroslender.herovender.data.Invoice;
import com.heroslender.herovender.data.User;
import com.heroslender.herovender.utils.NumberUtil;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MessageBuilder {
    private final List<String> placeholders;
    private final List<String> replaces;

    public MessageBuilder() {
        placeholders = Lists.newArrayList();
        replaces = Lists.newArrayList();
    }

    public MessageBuilder withPlaceholder(User user) {
        return withPlaceholder(user.getPlayer());
    }

    public MessageBuilder withPlaceholder(Player player) {
        return withPlaceholder((CommandSender) player);
    }

    public MessageBuilder withPlaceholder(CommandSender sender) {
        withPlaceholder("player", sender.getName());
        return this;
    }

    public MessageBuilder withPlaceholder(Invoice invoice) {
        withPlaceholder("invoice-item-count", invoice.getItemCount());

        val invoiceTotal = invoice.getTotal();
        withPlaceholder("invoice-total", NumberUtil.format(invoiceTotal));
        withPlaceholder("invoice-total-formatted", NumberUtil.formatShort(invoiceTotal));

        return this;
    }

    public MessageBuilder withPlaceholder(String replacer, double placeholder) {
        return withPlaceholder(replacer, Double.toString(placeholder));
    }

    public MessageBuilder withPlaceholder(String replacer, int placeholder) {
        return withPlaceholder(replacer, Integer.toString(placeholder));
    }

    public MessageBuilder withPlaceholder(String replacer, String placeholder) {
        if (replacer == null || replacer.isEmpty() || placeholder == null)
            return this;

        StringBuilder builder = new StringBuilder();
        if (replacer.charAt(0) != ':') {
            builder.append(':');
        }
        builder.append(replacer);
        if (replacer.charAt(replacer.length() - 1) != ':') {
            builder.append(':');
        }

        placeholders.add(builder.toString());
        replaces.add(placeholder);

        return this;
    }

    public String build(String text) {
        if (text == null || text.length() == 0 || placeholders.isEmpty()) {
            return text;
        }


        val buff = new StringBuilder(text.length());
        boolean[] toIgnore = new boolean[placeholders.size()];
        int start = 0;
        int tempIndex;
        int textIndex;
        int replaceIndex = -1;
        int replaceLength = -1;

        while(true) {
            textIndex = -1;

            for (int i = 0; i < placeholders.size(); i++) {
                if (toIgnore[i]) {
                    continue;
                }

                val placeholder = placeholders.get(i);
                tempIndex = text.indexOf(placeholder, start);
                if (tempIndex == -1) {
                    toIgnore[i] = true;
                } else if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                    replaceLength = placeholder.length();
                }
            }

            if (textIndex == -1) {
                break;
            }

            for (int i = start; i < textIndex; i++) {
                buff.append(text.charAt(i));
            }

            val replacement = replaces.get(replaceIndex);
            buff.append(replacement);

            start = textIndex + replaceLength;
        }

        for (int i = start; i < text.length(); i++) {
            buff.append(text.charAt(i));
        }

        return ChatColor.translateAlternateColorCodes('&', buff.toString());
    }
}
