package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import me.reklessmitch.csgo.colls.KitColl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Kit extends Entity<Kit> {

    String name = "default";
    ItemStack guiItem = new ItemStack(Material.STONE);
    List<String> commands = new ArrayList<>();
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

    public void setInventory(ItemStack[] contents) {
        this.inventory = contents;
        changed();
    }

    public void giveAllItems(Player player){
        player.getInventory().setContents(inventory);
        commands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player_name%", player.getName())));
    }
}
