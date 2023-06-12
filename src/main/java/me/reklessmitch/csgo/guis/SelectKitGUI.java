package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import lombok.Getter;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.colls.KitColl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectKitGUI extends ChestGui implements Listener {

    @Getter private Map<ItemStack, Integer> votes = new HashMap<>();

    public SelectKitGUI(Set<String> stringKits) {
        Set<Kit> kits = stringKits.stream()
                .map(Kit::get)
                .collect(Collectors.toSet());
        Inventory inventory = Bukkit.createInventory(null, 9, "Vote for a kit");
        kits.forEach(kit -> {
            ItemStack item = kit.getGuiItem();
            inventory.addItem(item);
            votes.put(item, 0);
        });
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.equals(this.getInventory())) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && votes.containsKey(clickedItem)) {
                event.setCancelled(true); // Prevent the item from being taken from the GUI
                addOneToVoteAndUpdateItem(clickedItem);
                player.sendMessage("You voted for " + clickedItem.getItemMeta().getDisplayName());
            }
        }
        //player.closeInventory();
    }

    private void addOneToVoteAndUpdateItem(ItemStack item) {
        int currentVotes = votes.getOrDefault(item, 0);
        votes.put(item, currentVotes + 1);

        // Update the item amount to show the new vote count
        item.setAmount(currentVotes + 1);
        this.getInventory().setItem(this.getInventory().first(item), item);
    }

    public Kit getHighestVotedKit(){
        int highestVotes = 0;
        ItemStack winningKit = null;
        for (Map.Entry<ItemStack, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > highestVotes) {
                highestVotes = entry.getValue();
                winningKit = entry.getKey();
            }
        }
        Collection<Kit> kits = KitColl.get().getAll();
        ItemStack finalWinningKit = winningKit;
        Kit wonKit = kits.stream().filter(kit -> kit.getGuiItem().getItemMeta().getDisplayName().equals(finalWinningKit.getItemMeta().getDisplayName())).findFirst().orElse(null);
        Bukkit.broadcastMessage(ChatColor.GREEN + "The winning kit is " + wonKit + " with " + highestVotes + " votes!");
        return wonKit;
    }
}
