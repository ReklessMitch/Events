package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GamesGUI extends ChestGui implements Listener {

    List<Game> games;

    public GamesGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 18, "Games");
        games = MiniGames.get().getGames();
        Bukkit.broadcastMessage("Games: " + games.size());
        games.forEach(game -> {
            if(game.isActive()) return;
            ItemStack itemStack = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("Game: " + game.getGameID());
            meta.setLore(List.of("" + game.getPlayers().size()));
            itemStack.setItemMeta(meta);
            inventory.addItem(itemStack);
        });
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
        this.setInventory(inventory);
        player.openInventory(this.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(this.getInventory())) {
            event.setCancelled(true);
            if(event.getCurrentItem() != null){
                int gameId = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split("Game: ")[1]);
                Game game = games.stream().filter(g -> g.getGameID() == gameId).findFirst().orElse(null);
                if(game == null){
                    return;
                }
                game.addPlayer(player);
                player.closeInventory();
            }
        }
    }
}
