package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class GamesGUI extends ChestGui {


    public GamesGUI() {
        setInventory(Bukkit.createInventory(null, 18, Component.text("Games")));
        refreshGUI();
        setAutoclosing(true);
        setAutoremoving(true);
    }

    public void refreshGUI(){
        int i = 0;
        for(Game game : MiniGames.get().getGames()){
            if(game.isActive() || game.getPlayers().size() == game.getMaxPlayers()) return;
            getInventory().setItem(i, game.getDisplay());
            setAction(i, event -> {
                if (game.isActive()) {
                    event.getWhoClicked().sendMessage("§c✘ §7That game already started!");
                } else if (game.getPlayers().size() == game.getMaxPlayers()) {
                    event.getWhoClicked().sendMessage("§c✘ §7That game is full!");
                } else {
                    game.addPlayer((Player) event.getWhoClicked(), game.getDisplayItem().getItemName());
                }
                return true;
            });
            i++;
        }
    }

    public void open(@NotNull Player player){
        player.openInventory(getInventory());
    }
}
