package me.reklessmitch.csgo.games.todo;

import com.massivecraft.massivecore.util.ItemBuilder;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

// Basically RPS
public class Element extends Game {

    Inventory gui;
    private final ItemStack rock;
    private final ItemStack paper;
    private final ItemStack scissors;
    Map<UUID, ItemStack> choice;

    public Element() {
        super();
        rock = new ItemBuilder(Material.COBBLESTONE, "&cRock").build();
        paper = new ItemBuilder(Material.PAPER, "&aPaper").build();
        scissors = new ItemBuilder(Material.COBBLESTONE, "&dScissors").build();
        gui = newGui();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());

    }

    private Inventory newGui() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Rock, Paper, Scissors");
        inventory.setItem(3, rock);
        inventory.setItem(5, paper);
        inventory.setItem(7, scissors);
        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != gui) return;
        switch(event.getSlot()){
            case 3 -> choice.put(event.getWhoClicked().getUniqueId(), rock);
            case 5 -> choice.put(event.getWhoClicked().getUniqueId(), paper);
            case 7 -> choice.put(event.getWhoClicked().getUniqueId(), scissors);
            default -> {
                return;
            }
        }
        event.getWhoClicked().closeInventory();
        if(choice.keySet().size() == 2){
            getWinner();
        }

    }

    private void getWinner() {

        UUID player1 = getPlayers().stream().toList().get(0);
        UUID player2 = getPlayers().stream().toList().get(1);

        ItemStack p1Choice = choice.get(player1);
        ItemStack p2Choice = choice.get(player2);

        if(p1Choice.equals(p2Choice)){
            this.getPlayers().forEach(player -> Bukkit.getPlayer(player).sendMessage("You both picked the same thing, try again!"));
            start();
        }else{
            if(p1Choice.equals(rock) && p2Choice.equals(scissors) ||
                (p1Choice.equals(scissors) && p2Choice.equals(paper)) ||
                (p1Choice.equals(paper) && p2Choice.equals(rock))){
                Bukkit.broadcastMessage(player1 + " won!");
            }else{
                Bukkit.broadcastMessage(player2 + " won!");
            }
        }


    }
}