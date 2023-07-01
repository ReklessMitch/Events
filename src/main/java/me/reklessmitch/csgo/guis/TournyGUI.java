package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.torny.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class TournyGUI extends ChestGui implements Listener {

    private final Map<Tournament, Integer> games = new HashMap<>();
    private final Inventory inventory;

    public TournyGUI() {
        inventory = Bukkit.createInventory(null, 18, "Tournaments");
        refreshGUI(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    public void refreshGUI(Inventory inventory){
        int i = 0;
        for(Tournament game : MiniGames.get().getTournaments()){
            if(game.isStarted()) return;
            games.put(game, i);
            inventory.setItem(i, game.getDisplayItem().getGuiItem());
            i++;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !clickedInventory.equals(inventory)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        Tournament game = games.keySet().stream().filter(g -> games.get(g) == event.getSlot()).findFirst().orElse(null);
        if (game == null || game.isStarted()) {
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cGame no longer exists"));
            return;
        }
        game.addPlayer(player.getUniqueId());
        player.closeInventory();
    }

    public void open(Player player){
        player.openInventory(inventory);
    }
}
