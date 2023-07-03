package me.reklessmitch.csgo.guis;

import com.cryptomorin.xseries.SkullUtils;
import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.todo.Element;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ElementGUI extends ChestGui implements Listener {

    private final @NotNull List<@NotNull Element> elementGames;

    public ElementGUI() {
        // TODO: 03/07/2023 elementGames variable
        elementGames = new LinkedList<>();

        Inventory inventory = Bukkit.createInventory(null, 18, Component.text("Games"));
        setUpGUI();
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    public void setUpGUI() {
        elementGames.forEach(game -> {
            // TODO: 03/07/2023 (URGENT) Check if the p is online
            Player p = Bukkit.getPlayer(game.getPlayers().stream().findAny().get());
            ItemStack itemStack = SkullUtils.getSkull(p.getUniqueId());
            this.getInventory().addItem(itemStack);
        });
    }

    public void open(@NotNull Player player) {
        player.openInventory(this.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvenClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(this.getInventory())) {
            event.setCancelled(true);
        }
    }

}
