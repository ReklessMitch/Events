package me.reklessmitch.csgo.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public record DisplayItem(Material material, String itemName, List<String> itemLore, int customModelData) {

    public ItemStack getGuiItem() {
        ItemStack i = new ItemStack(material);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
        meta.setLore(itemLore.stream().map(s -> {
            if (s == null) return "";
            return ChatColor.translateAlternateColorCodes('&', s);
        }).toList());

        meta.setCustomModelData(customModelData);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(meta);
        return i;
    }


}
