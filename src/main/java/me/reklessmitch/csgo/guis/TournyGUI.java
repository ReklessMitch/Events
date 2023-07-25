package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.torny.Tournament;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TournyGUI extends ChestGui{

    public TournyGUI() {
        setInventory(Bukkit.createInventory(null, 18, Component.text("Tournaments")));
        refreshGUI();
    }

    public void refreshGUI(){
        int i = 0;
        for(Tournament game : MiniGames.get().getTournaments()){
            if(game.isStarted()) continue;
            getInventory().setItem(i, game.getDisplayItem().getGuiItem());
            setAction(i, event -> {
                if (game.isStarted()) {
                    event.getWhoClicked().sendMessage("§c✘ §7That game already started!");
                } else {
                    game.addPlayer(event.getWhoClicked().getUniqueId());
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
