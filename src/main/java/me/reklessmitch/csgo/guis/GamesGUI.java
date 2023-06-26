package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GamesGUI extends ChestGui implements Listener {

    Map<Game, Integer> games = new HashMap<>();
    Inventory inventory;

    public GamesGUI() {
        inventory = Bukkit.createInventory(null, 18, "Games");
        refreshGUI(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    public void refreshGUI(Inventory inventory){
        int i = 0;
        for(Game game : MiniGames.get().getGames()){
            if(game.isHasStarted() || game.getPlayers().size() == game.getMaxPlayers()) return;
            games.put(game, i);
            inventory.setItem(i, game.getDisplay());
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
        Game game = games.keySet().stream().filter(g -> games.get(g) == event.getSlot()).findFirst().orElse(null);
        if (game == null || game.isActive()) {
            player.closeInventory();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cGame no longer exists"));
            return;
        }else if(game.getPlayers().size() == game.getMaxPlayers()){
                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cGame is full"));
                return;
        }
        game.addPlayer(player, game.getDisplayItem().getItemName());
        player.closeInventory();
    }

    public void open(Player player){
        player.openInventory(inventory);
    }
}
