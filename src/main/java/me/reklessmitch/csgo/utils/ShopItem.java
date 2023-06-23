package me.reklessmitch.csgo.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public class ShopItem {

    DisplayItem displayItem;
    int slot;
    int cost;
    List<String> commands;

    public ShopItem(DisplayItem item, int slot, int cost, List<String> commands) {
        this.displayItem = item;
        this.slot = slot;
        this.cost = cost;
        this.commands = commands;
    }

}
