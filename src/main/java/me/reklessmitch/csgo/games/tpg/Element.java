package me.reklessmitch.csgo.games.tpg;

import com.massivecraft.massivecore.util.ItemBuilder;
import me.reklessmitch.csgo.games.TwoPlayerGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

// Basically RPS
public class Element extends TwoPlayerGame {

    int prizePool;
    Inventory gui;
    private final ItemStack rock;
    private final ItemStack paper;
    private final ItemStack scissors;
    Map<Player, ItemStack> choice;

    public Element(int prizePool) {
        super();
        this.prizePool = prizePool;
        rock = new ItemBuilder(Material.COBBLESTONE, "&cRock").build();
        paper = new ItemBuilder(Material.PAPER, "&aPaper").build();
        scissors = new ItemBuilder(Material.COBBLESTONE, "&dScissors").build();
        gui = newGui();
    }

    private Inventory newGui() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Rock, Paper, Scissors");
        inventory.setItem(3, rock);
        inventory.setItem(5, paper);
        inventory.setItem(7, scissors);
        return inventory;
    }

    @Override
    public void start() {
        if(this.getPlayers().size() == 2) {
            this.getPlayers().forEach(player -> player.openInventory(gui));

        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != gui) return;
        switch(event.getSlot()){
            case 3 -> choice.put((Player) event.getWhoClicked(), rock);
            case 5 -> choice.put((Player) event.getWhoClicked(), paper);
            case 7 -> choice.put((Player) event.getWhoClicked(), scissors);
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
        Player player1 = getPlayers().stream().toList().get(0);
        Player player2 = getPlayers().stream().toList().get(1);

        ItemStack p1Choice = choice.get(player1);
        ItemStack p2Choice = choice.get(player2);

        if(p1Choice.equals(p2Choice)){
            this.getPlayers().forEach(player -> player.sendMessage("You both picked the same thing, try again!"));
            start();
        }else{
            if(p1Choice.equals(rock) && p2Choice.equals(scissors) ||
                (p1Choice.equals(scissors) && p2Choice.equals(paper)) ||
                (p1Choice.equals(paper) && p2Choice.equals(rock))){
                Bukkit.broadcastMessage(player1.name() + " won!");
            }else{
                Bukkit.broadcastMessage(player2.name() + " won!");
            }
        }


    }
}