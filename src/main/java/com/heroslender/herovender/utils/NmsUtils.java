package com.heroslender.herovender.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;

public class NmsUtils {

/**
 * Registar um Command no servidor sem precisar de registar na plugin.yml
 *
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

private static Class<?> getOBCClass(String name) throws ClassNotFoundException {
    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
}
}
