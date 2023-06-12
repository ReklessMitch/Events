package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import me.reklessmitch.csgo.colls.KitColl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class Kit extends Entity<Kit> {

    ItemStack guiItem = setGuiItem();
    List<String> commands = List.of("give %player_name% revolver 1", "give %player_name% revolver_ammo 64");
    ItemStack[] inventory = new ItemStack[0];

    public static Kit get(Object oid) {
        return KitColl.get().get(oid);
    }

    @Override
    public Kit load(@NotNull Kit that)
    {
        super.load(that);
        return this;
    }

    private ItemStack setGuiItem() {
        ItemStack i = new ItemStack(Material.STONE);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName("Revolver");
        meta.setLore(List.of("1x Revolver + 64 Ammo"));
        i.setItemMeta(meta);
        return i;
    }

    public void setInventory(ItemStack[] contents) {
        this.inventory = contents;
        changed();
    }
}
