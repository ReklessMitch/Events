package me.reklessmitch.csgo.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
public class ShopItem {

    Material material;
    String itemName;
    List<String> itemLore;
    int customModelData;
    int slot;
    int cost;
    List<String> commands;

    public ShopItem(Material material, String itemName, List<String> itemLore, int customModelData, int slot, int cost, List<String> commands) {
        this.material = material;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.customModelData = customModelData;
        this.slot = slot;
        this.cost = cost;
        this.commands = commands;
    }

    public ItemStack getGuiItem() {
        ItemStack guiItem = new ItemStack(material);
        ItemMeta meta = guiItem.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(itemLore);
        meta.setCustomModelData(customModelData);
        guiItem.setItemMeta(meta);
        return guiItem;
    }
}
