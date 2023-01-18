package com.heroslender.herovender.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NmsUtils {
    private static Constructor<?> chatMsgConst, actionPacketConst = null;

    static {
        try {
            Class<?> baseComponent = getNMSClass("IChatBaseComponent");
            Class<?> packetPlayOutChat = getNMSClass("PacketPlayOutChat");
            if (packetPlayOutChat != null) {
                actionPacketConst = packetPlayOutChat.getConstructor(baseComponent, Byte.TYPE);
            } else {
                actionPacketConst = null;
            }

            Class<?> chatMessage = getNMSClass("ChatMessage");
            if (chatMessage != null) {
                chatMsgConst = chatMessage.getDeclaredConstructor(String.class, Object[].class);
            } else {
                chatMsgConst = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Registar um Command no servidor sem precisar de registar na plugin.yml
     * <p>
     * WARINIG: Para usar este metodo o comando não pode tar na plugin.yml, senão
     * pode não funcionar.
     *
     * @param prefixo Nome do plugin, é o que aparece quando das /{prefixo}:comando
     * @param comando O comando que criaste
     */
    public static void registerCommand(String prefixo, Command comando) {
        try {
            Object craftServer = getOBCClass("CraftServer").cast(Bukkit.getServer());
            Object commandMap = craftServer.getClass().getMethod("getCommandMap").invoke(craftServer);

            commandMap.getClass().getMethod("register", String.class, Command.class).invoke(commandMap, prefixo, comando);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendActionBar(String message, Player... players) {
        if (chatMsgConst == null || actionPacketConst == null) {
            // Not initialized
            return;
        }

        try {
            Object ichatbc = chatMsgConst.newInstance(message, new Object[0]);
            Object packet = actionPacketConst.newInstance(ichatbc, (byte) 2);

            for (Player player : players) {
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getOBCClass(String name) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }
}
