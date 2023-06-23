package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.tpg.Element;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ElementGUI extends ChestGui implements Listener {

    List<Element> elementGames;

    public ElementGUI() {
        Inventory inventory = Bukkit.createInventory(null, 18, "Games");
        setUpGUI();
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());

    }

    private ItemStack getSkullOfPlayer(Player player){

        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName(player.getName());
        stack.setItemMeta(skullMeta);
        return stack;
    }

    public void setUpGUI() {
        elementGames.forEach(game -> {
            Player p = Bukkit.getPlayer(game.getPlayers().stream().findAny().get());
            ItemStack itemStack = getSkullOfPlayer(p);
            this.getInventory().addItem(itemStack);
        });
    }

    public void open(Player player) {
        player.openInventory(this.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvenClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(this.getInventory())) {
            event.setCancelled(true);
        }
    }

}
