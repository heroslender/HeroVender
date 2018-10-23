package com.heroslender.herovender.utils.items;

import com.heroslender.herovender.HeroVender;
import com.heroslender.herovender.utils.NumberUtil;
import org.bukkit.*;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Item deserializer from EssentialsX with some tweaks to fit here
 * Source: https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/MetaItemStack.java
 */
public class MetaItemStack {
    private static final Map<String, DyeColor> colorMap = new HashMap<>();
    private static final Map<String, FireworkEffect.Type> fireworkShape = new HashMap<>();
    private static final Pattern itemSplitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
    private static final Pattern splitPattern = Pattern.compile("[:+',;.]");
    private static Method spigotMethod;
    private static Method setUnbreakableMethod;

    static {
        for (DyeColor color : DyeColor.values()) {
            colorMap.put(color.name(), color);
        }
        for (FireworkEffect.Type type : FireworkEffect.Type.values()) {
            fireworkShape.put(type.name(), type);
        }
    }

    private ItemStack stack;
    private FireworkEffect.Builder builder = FireworkEffect.builder();
    private PotionEffectType pEffectType;
    private PotionEffect pEffect;
    private boolean validFirework = false;
    private boolean validPotionEffect = false;
    private boolean validPotionDuration = false;
    private boolean validPotionPower = false;
    private boolean isSplashPotion = false;
    private boolean completePotion = false;
    private int power = 1;
    private int duration = 120;
    private double price = -1;

    public MetaItemStack(final ItemStack stack) {
        this.stack = stack.clone();
    }

    public static MetaItemStack getFromString(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return null;
        }

        final String[] parts = itemString.split(" +");
        final ItemStack parseStack = getItemStack(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);
        if (parseStack == null || parseStack.getType() == Material.AIR) {
            return null;
        }

        final MetaItemStack metaStack = new MetaItemStack(parseStack);

        if (parts.length > 2) {
            // We pass a null sender here because kits should not do perm checks
            metaStack.parseStringMeta(true, parts, 2);
        }

        return metaStack;
    }

    private static ItemStack getItemStack(final String itemId, final int amount) {
        final String itemname;
        final short metaData;

        Matcher parts = itemSplitPattern.matcher(itemId);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemname = itemId;
            metaData = 0;
        }

        try {
            if (NumberUtil.isInt(itemname)) {
                return new ItemStack(Material.getMaterial(Integer.parseInt(itemname)), amount, metaData);
            } else if (NumberUtil.isInt(itemId)) {
                return new ItemStack(Material.getMaterial(Integer.parseInt(itemId)), amount, metaData);
            } else {
                return new ItemStack(Material.getMaterial(itemname), amount, metaData);
            }
        } catch (Exception e) {
            HeroVender.getInstance().getLogger().log(Level.WARNING, "Failed to load the item \"" + itemId + "\" from the config. Material not found!");
            return null;
        }
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public boolean isValidFirework() {
        return validFirework;
    }

    public boolean isValidPotion() {
        return validPotionEffect && validPotionDuration && validPotionPower;
    }

    public FireworkEffect.Builder getFireworkBuilder() {
        return builder;
    }

    public PotionEffect getPotionEffect() {
        return pEffect;
    }

    public double getPrice() {
        return price;
    }

    public boolean completePotion() {
        return completePotion;
    }

    private void resetPotionMeta() {
        pEffect = null;
        pEffectType = null;
        validPotionEffect = false;
        validPotionDuration = false;
        validPotionPower = false;
        isSplashPotion = false;
        completePotion = true;
    }

    private boolean isPotion(Material type) {
        return type.name().endsWith("POTION");
    }

    public void parseStringMeta(final boolean allowUnsafe, String[] string, int fromArg) {
        for (int i = fromArg; i < string.length; i++) {
            addStringMeta(allowUnsafe, string[i]);
        }
        if (validFirework) {
            FireworkEffect effect = builder.build();
            FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
            fmeta.addEffect(effect);
            stack.setItemMeta(fmeta);
        }
    }

    public void addStringMeta(final boolean allowUnsafe, final String string) {
        final String[] split = splitPattern.split(string, 2);
        if (split.length < 1) {
            return;
        }

        Material banner = null;
        Material shield = null;

        try {
            // 1.8
            banner = Material.valueOf("BANNER");

            // 1.9
            shield = Material.valueOf("SHIELD");
        } catch (IllegalArgumentException ignored) {
        }

        if (split.length > 1 && split[0].equalsIgnoreCase("price")) {
            price = NumberUtil.getValidMoney(split[1]);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("name")) {
            final String displayName = ChatColor.translateAlternateColorCodes('&', split[1].replace('_', ' '));
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc"))) {
            final List<String> lore = new ArrayList<>();
            for (String line : split[1].split("\\|")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line.replace('_', ' ')));
            }
            final ItemMeta meta = stack.getItemMeta();
            meta.setLore(lore);
            stack.setItemMeta(meta);
        } else if (split[0].equalsIgnoreCase("unbreakable")) {
            boolean value = split.length > 1 ? Boolean.valueOf(split[1]) : true;
            setUnbreakable(stack, value);
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("player") || split[0].equalsIgnoreCase("owner")) && stack.getType() == Material.SKULL_ITEM) {
            stack.setDurability((short) 3);
            final String owner = split[1];
            final SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(owner);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("author") && stack.getType() == Material.WRITTEN_BOOK) {
            final String author = ChatColor.translateAlternateColorCodes('&', split[1]);
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            meta.setAuthor(author);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("title") && stack.getType() == Material.WRITTEN_BOOK) {
            final String title = ChatColor.translateAlternateColorCodes('&', split[1].replace('_', ' '));
            final BookMeta meta = (BookMeta) stack.getItemMeta();
            meta.setTitle(title);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("power") && stack.getType() == Material.FIREWORK) {
            final int power = NumberUtil.isInt(split[1]) ? Integer.parseInt(split[1]) : 0;
            final FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
            meta.setPower(power > 3 ? 4 : power);
            stack.setItemMeta(meta);
        } else if (split.length > 1 && split[0].equalsIgnoreCase("itemflags")) {
            addItemFlags(string);
        } else if (stack.getType() == Material.FIREWORK) {//WARNING - Meta for fireworks will be ignored after this point.
            addFireworkMeta(false, string);
        } else if (isPotion(stack.getType())) { //WARNING - Meta for potions will be ignored after this point.
            addPotionMeta(false, string);
        } else if (banner != null && stack.getType() == banner) { //WARNING - Meta for banners will be ignored after this point.
            addBannerMeta(string);
        } else if (shield != null && stack.getType() == shield) { //WARNING - Meta for shields will be ignored after this point.
            addBannerMeta(string);
        } else if (split.length > 1 && (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour")) && (stack.getType() == Material.LEATHER_BOOTS || stack.getType() == Material.LEATHER_CHESTPLATE || stack.getType() == Material.LEATHER_HELMET || stack.getType() == Material.LEATHER_LEGGINGS)) {
            final String[] color = split[1].split("(\\||,)");
            if (color.length == 1 && (NumberUtil.isInt(color[0]) || color[0].startsWith("#"))) { // int rgb and hex
                final LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
                String input = color[0];
                if (input.startsWith("#")) {
                    meta.setColor(Color.fromRGB(
                            Integer.valueOf(input.substring(1, 3), 16),
                            Integer.valueOf(input.substring(3, 5), 16),
                            Integer.valueOf(input.substring(5, 7), 16)));
                } else {
                    meta.setColor(Color.fromRGB(Integer.parseInt(input)));
                }
                stack.setItemMeta(meta);
            } else if (color.length == 3) { // r,g,b
                final int red = NumberUtil.isInt(color[0]) ? Integer.parseInt(color[0]) : 0;
                final int green = NumberUtil.isInt(color[1]) ? Integer.parseInt(color[1]) : 0;
                final int blue = NumberUtil.isInt(color[2]) ? Integer.parseInt(color[2]) : 0;
                final LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
                meta.setColor(Color.fromRGB(red, green, blue));
                stack.setItemMeta(meta);
            }
        } else {
            parseEnchantmentStrings(allowUnsafe, split);
        }
    }

    public void addItemFlags(final String string) {
        String[] separate = splitPattern.split(string, 2);
        if (separate.length != 2) {
            return;
        }

        String[] split = separate[1].split(",");
        ItemMeta meta = stack.getItemMeta();

        for (String s : split) {
            for (ItemFlag flag : ItemFlag.values()) {
                if (s.equalsIgnoreCase(flag.name())) {
                    meta.addItemFlags(flag);
                }
            }
        }

        stack.setItemMeta(meta);
    }

    public void addFireworkMeta(final boolean allowShortName, final String string) {
        if (stack.getType() == Material.FIREWORK) {
            final String[] split = splitPattern.split(string, 2);
            if (split.length < 2) {
                return;
            }

            if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || (allowShortName && split[0].equalsIgnoreCase("c"))) {
                if (validFirework) {
                    FireworkEffect effect = builder.build();
                    FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                    fmeta.addEffect(effect);
                    stack.setItemMeta(fmeta);
                    builder = FireworkEffect.builder();
                }

                List<Color> primaryColors = new ArrayList<Color>();
                String[] colors = split[1].split(",");
                for (String color : colors) {
                    if (colorMap.containsKey(color.toUpperCase())) {
                        validFirework = true;
                        primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                    }
                }
                builder.withColor(primaryColors);
            } else if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("type") || (allowShortName && (split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("t")))) {
                FireworkEffect.Type finalEffect = null;
                split[1] = (split[1].equalsIgnoreCase("large") ? "BALL_LARGE" : split[1]);
                if (fireworkShape.containsKey(split[1].toUpperCase())) {
                    finalEffect = fireworkShape.get(split[1].toUpperCase());
                }
                if (finalEffect != null) {
                    builder.with(finalEffect);
                }
            } else if (split[0].equalsIgnoreCase("fade") || (allowShortName && split[0].equalsIgnoreCase("f"))) {
                List<Color> fadeColors = new ArrayList<>();
                String[] colors = split[1].split(",");
                for (String color : colors) {
                    if (colorMap.containsKey(color.toUpperCase())) {
                        fadeColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
                    }
                }
                if (!fadeColors.isEmpty()) {
                    builder.withFade(fadeColors);
                }
            } else if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                String[] effects = split[1].split(",");
                for (String effect : effects) {
                    if (effect.equalsIgnoreCase("twinkle")) {
                        builder.flicker(true);
                    } else if (effect.equalsIgnoreCase("trail")) {
                        builder.trail(true);
                    }
                }
            }
        }
    }

    public void addPotionMeta(final boolean allowShortName, final String string) {
        if (isPotion(stack.getType())) {
            final String[] split = splitPattern.split(string, 2);

            if (split.length < 2) {
                return;
            }

            if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e"))) {
                pEffectType = Potions.getByName(split[1]);
                if (pEffectType != null && pEffectType.getName() != null) {
                    validPotionEffect = true;
                }
            } else if (split[0].equalsIgnoreCase("power") || (allowShortName && split[0].equalsIgnoreCase("p"))) {
                if (NumberUtil.isInt(split[1])) {
                    validPotionPower = true;
                    power = Integer.parseInt(split[1]);
                    if (power > 0 && power < 4) {
                        power -= 1;
                    }
                }
            } else if (split[0].equalsIgnoreCase("duration") || (allowShortName && split[0].equalsIgnoreCase("d"))) {
                if (NumberUtil.isInt(split[1])) {
                    validPotionDuration = true;
                    duration = Integer.parseInt(split[1]) * 20; //Duration is in ticks by default, converted to seconds
                }
            } else if (split[0].equalsIgnoreCase("splash") || (allowShortName && split[0].equalsIgnoreCase("s"))) {
                isSplashPotion = Boolean.valueOf(split[1]);
            }

            if (isValidPotion()) {
                PotionMeta pmeta = (PotionMeta) stack.getItemMeta();
                pEffect = pEffectType.createEffect(duration, power);
                pmeta.addCustomEffect(pEffect, true);
                stack.setItemMeta(pmeta);

                Potion potion = Potion.fromItemStack(stack);
                potion.setSplash(isSplashPotion);
                potion.apply(stack);

                resetPotionMeta();
            }
        }
    }

    private void parseEnchantmentStrings(final boolean allowUnsafe, final String[] split) {
        final Enchantment enchantment = Enchantments.getByName(split[0]);
        if (enchantment == null) {
            return;
        }

        int level = -1;
        if (split.length > 1) {
            try {
                level = Integer.parseInt(split[1]);
            } catch (NumberFormatException ex) {
                level = -1;
            }
        }

        if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
            level = enchantment.getMaxLevel();
        }
        addEnchantment(allowUnsafe, enchantment, level);
    }

    public void addEnchantment(final boolean allowUnsafe, final Enchantment enchantment, final int level) {
        if (enchantment == null) {
            return;
        }
        try {
            if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                if (level == 0) {
                    meta.removeStoredEnchant(enchantment);
                } else {
                    meta.addStoredEnchant(enchantment, level, allowUnsafe);
                }
                stack.setItemMeta(meta);
            } else // all other material types besides ENCHANTED_BOOK
            {
                if (level == 0) {
                    stack.removeEnchantment(enchantment);
                } else {
                    if (allowUnsafe) {
                        stack.addUnsafeEnchantment(enchantment, level);
                    } else {
                        stack.addEnchantment(enchantment, level);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Enchantment getEnchantment(final String name) {
        return Enchantments.getByName(name);
    }

    public void addBannerMeta(final String string) {
        if (stack.getType() == Material.BANNER && string != null) {
            final String[] split = splitPattern.split(string, 2);

            if (split.length < 2) {
                return;
            }

            PatternType patternType = null;
            try {
                patternType = PatternType.valueOf(split[0]);
            } catch (Exception ignored) {
            }

            final BannerMeta meta = (BannerMeta) stack.getItemMeta();
            if (split[0].equalsIgnoreCase("basecolor")) {
                Color color = Color.fromRGB(Integer.valueOf(split[1]));
                meta.setBaseColor(DyeColor.getByColor(color));
            } else if (patternType != null) {
                PatternType type = PatternType.valueOf(split[0]);
                DyeColor color = DyeColor.getByColor(Color.fromRGB(Integer.valueOf(split[1])));
                org.bukkit.block.banner.Pattern pattern = new org.bukkit.block.banner.Pattern(color, type);
                meta.addPattern(pattern);
            }

            stack.setItemMeta(meta);
        }
    }

    private void setUnbreakable(ItemStack is, boolean unbreakable) {
        ItemMeta meta = is.getItemMeta();
        try {
            if (spigotMethod == null) {
                spigotMethod = meta.getClass().getDeclaredMethod("spigot");
                spigotMethod.setAccessible(true);
            }
            Object itemStackSpigot = spigotMethod.invoke(meta);
            if (setUnbreakableMethod == null) {
                setUnbreakableMethod = itemStackSpigot.getClass().getDeclaredMethod("setUnbreakable", Boolean.TYPE);
                setUnbreakableMethod.setAccessible(true);
            }
            setUnbreakableMethod.invoke(itemStackSpigot, unbreakable);

            is.setItemMeta(meta);
        } catch (Exception t) {
            t.printStackTrace();
        }
    }
}