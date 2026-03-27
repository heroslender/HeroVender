package com.github.heroslender.herovender;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    public static String SellAutoSellOn = "<green>You have activated the Auto-Sell!";
    public static String SellAutoSellOff = "<red>You have deactivated the Auto-Sell!";

    public static String SellShiftSellOn = "<green>You have activated the Shift-Sell!";
    public static String SellShiftSellOff = "<red>You have deactivated the Shift-Sell!";

    public static String SellCommandChatSold = "<green>You sold <gray><invoice-item-count> <green>for <white><invoice-total-formatted><green>!";
    public static String SellCommandChatNoItems = "<red>You don't have any items that can be sold!";
    public static String SellCommandChatDelay = "<red>You must wait <delay-formated> to sell again!";
    public static String SellCommandActionbarSold = "";
    public static String SellCommandActionbarNoItems = "";
    public static String SellCommandActionbarDelay = "";

    public static String SellAutoChatSold = "";
    public static String SellAutoChatNoItems = "";
    public static String SellAutoActionbarSold = "<green>You sold <gray><invoice-item-count> <green>for <white><invoice-total-formatted><green>!";
    public static String SellAutoActionbarNoItems = "<red>You don't have any items that can be sold!";

    public static String SellShiftChatSold = "<green>You sold <gray><invoice-item-count> <green>for <white><invoice-total-formatted><green>!";
    public static String SellShiftChatNoItems = "<red>You don't have any items that can be sold!";
    public static String SellShiftChatDelay = "<red>You must wait <delay-formated> to sell again!";
    public static String SellShiftActionbarSold = "";
    public static String SellShiftActionbarNoItems = "";
    public static String SellShiftActionbarDelay = "";
}
