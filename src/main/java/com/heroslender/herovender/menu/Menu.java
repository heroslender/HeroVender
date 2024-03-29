package com.heroslender.herovender.menu;

import com.heroslender.herovender.utils.HeroException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Menu {
    @Getter private final String nome;
    @Getter private MenuItem[] items;

    public Menu(String nome, int tamanho) {
        this.nome = nome;
        items = new MenuItem[tamanho];
    }

    public Menu(String nome, MenuSize tamanho) {
        this(nome, tamanho.getSlots());
    }

    public static void registar(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onInvClick(InventoryClickEvent e) {
                if (!e.isCancelled() && e.getInventory().getHolder() instanceof MenuHolder)
                    ((MenuHolder) e.getInventory().getHolder()).getMenu().inventoryClick(e);
            }
        }, plugin);
    }

    public void setItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack, null);
    }

    public void setItem(int slot, ItemStack itemStack, MenuItemClick itemClick) {
        setItem(slot, new MenuItem(itemStack, itemClick));
    }

    public void setItem(int slot, MenuItem menuItem) {
        items[slot] = menuItem;
    }

    public void setItemUpdate(int slot, HumanEntity holder, ItemStack itemStack, MenuItemClick itemClick) {
        setItemUpdate(slot, holder, new MenuItem(itemStack, itemClick));
    }

    public void setItemUpdate(int slot, HumanEntity holder, MenuItem menuItem) {
        setItem(slot, menuItem);

        Inventory inv = holder.getOpenInventory().getTopInventory();
        if (inv.getHolder() instanceof MenuHolder) {
            inv.setItem(slot, menuItem.getIcon());
        }
    }

    public int getSize() {
        return items.length;
    }

    public void open(HumanEntity humanEntity) {
        MenuHolder holder = new MenuHolder(this);
        Inventory inventory = Bukkit.createInventory(holder, items.length, nome);
        holder.setInventory(inventory);

        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null)
                inventory.setItem(i, this.items[i].getIcon());
            else
                inventory.setItem(i, new ItemStack(Material.AIR));
        }

        humanEntity.openInventory(inventory);
    }

    private void inventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
        int slot = e.getRawSlot();
        if (slot >= 0 && slot < items.length && items[slot] != null)
            items[slot].onClick(e);
    }

    public interface MenuItemClick {
        void onClick(InventoryClickEvent e) throws HeroException;
    }

    public enum MenuSize {
        ONE_LINE(9),
        TWO_LINES(18),
        THREE_LINES(27),
        FOUR_LINES(36),
        FIVE_LINES(45),
        SIX_LINES(54);

        private final int slots;

        MenuSize(int slots) {
            this.slots = slots;
        }

        public int getSlots() {
            return slots;
        }

        public static MenuSize of(int size) {
            MenuSize curr = SIX_LINES;

            for (MenuSize menuSize : values()) {
                if (size < menuSize.getSlots() && curr.getSlots() > menuSize.getSlots()) {
                    curr = menuSize;
                }
            }

            return curr;
        }
    }

    @RequiredArgsConstructor
    public class MenuItem {
        private final ItemStack icon;
        private final MenuItemClick itemClick;

        public MenuItem(ItemStack icon) {
            this.icon = icon;
            this.itemClick = null;
        }

        void onClick(InventoryClickEvent e) {
            if (itemClick != null) {
                try {
                    itemClick.onClick(e);
                } catch (HeroException ex) {
                    e.getWhoClicked().sendMessage(ex.getMessage());
                }
            }
        }

        ItemStack getIcon() {
            return icon;
        }
    }

    private class MenuHolder implements InventoryHolder {
        private final Menu menu;
        private Inventory inventory;

        MenuHolder(Menu menu) {
            this.menu = menu;
        }

        Menu getMenu() {
            return menu;
        }

        public Inventory getInventory() {
            return this.inventory;
        }

        void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }
}