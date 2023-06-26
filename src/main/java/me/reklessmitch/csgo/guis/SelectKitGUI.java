package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import lombok.Getter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.colls.KitColl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class SelectKitGUI extends ChestGui implements Listener {

    @Getter private Map<String, Integer> votes = new HashMap<>();
    private Map<Integer, String> kitSlots = new HashMap<>();

    public SelectKitGUI(Set<String> stringKits) {
        List<Kit> kits = new ArrayList<>();
        stringKits.forEach(s -> kits.add(KitColl.get().get(s)));
        Inventory inventory = Bukkit.createInventory(null, 9, "Vote for a kit");
        if(kits.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + "No kits available for this Arena");
        }

        for(int i = 0; i < kits.size(); i++){
            kitSlots.put(i, kits.get(i).getName());
            inventory.setItem(i, kits.get(i).getGuiItem());
            votes.put(kits.get(i).getName(), 0);
        }
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.equals(this.getInventory())) {
            event.setCancelled(true);
            int clickedItem = event.getSlot();
            if (kitSlots.get(clickedItem) != null) {
                addOneToVoteAndUpdateItem(kitSlots.get(clickedItem));
                event.getWhoClicked().sendMessage("You voted for " + kitSlots.get(clickedItem));
                event.getWhoClicked().closeInventory();
            }
        }
    }

    private void addOneToVoteAndUpdateItem(String kitName) {
        int currentVotes = votes.getOrDefault(kitName, 0);
        votes.put(kitName, currentVotes + 1);
        // Update the item amount to show the new vote count
    }

    public Kit getHighestVotedKit(){
        Optional<Map.Entry<String, Integer>> maxEntryOptional = votes.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntryOptional.isPresent()) {
            Map.Entry<String, Integer> maxEntry = maxEntryOptional.get();
            String keyOfMaxValue = maxEntry.getKey();
            Bukkit.broadcastMessage(ChatColor.GREEN + "The kit " + keyOfMaxValue + " has won the vote with" + maxEntry.getValue() + " votes.");
            return KitColl.get().get(keyOfMaxValue);
        } else {
            System.out.println("The map is empty.");
        }
        Bukkit.broadcastMessage("NULL?");
        return null;
    }

    public void open(Set<UUID> players) {
        players.forEach(p -> Bukkit.getPlayer(p).openInventory(this.getInventory()));
    }
}
