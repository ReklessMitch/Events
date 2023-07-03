package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GamesGUI extends ChestGui implements Listener {

    private final @NotNull Map<@NotNull Game, @NotNull Integer> games = new HashMap<>();
    private final @NotNull Inventory inventory;

    public GamesGUI() {
        inventory = Bukkit.createInventory(null, 18, Component.text("Games"));
        refreshGUI(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    public void refreshGUI(Inventory inventory){
        int i = 0;
        for(Game game : MiniGames.get().getGames()){
            if(game.isActive() || game.getPlayers().size() == game.getMaxPlayers()) return;
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

        player.closeInventory();

        if (game == null) {
            player.sendMessage("§c✘ §7That game no longer exists!");
        }else if (game.isActive()) {
            player.sendMessage("§c✘ §7That game already started!");
        }else if(game.getPlayers().size() == game.getMaxPlayers()){
            player.sendMessage("§c✘ §7That game is full!");
        }else {
            game.addPlayer(player, game.getDisplayItem().getItemName());
        }
    }

    public void open(@NotNull Player player){
        player.openInventory(inventory);
    }
}
