package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import lombok.Getter;
import me.reklessmitch.csgo.configs.Kit;
import me.reklessmitch.csgo.colls.KitColl;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class SelectKitGUI extends ChestGui {

    @Getter
    private final @NotNull Map<@NotNull String, @NotNull Integer> votes = new LinkedHashMap<>();

    public SelectKitGUI(@NotNull List<String> stringKits) {
        List<Kit> kits = new ArrayList<>();
        stringKits.forEach(s -> kits.add(KitColl.get().get(s)));
        setInventory(Bukkit.createInventory(null, 9, Component.text("Vote for a kit")));
        if(kits.isEmpty()) {
            // TODO: 03/07/2023 Change it to a thrown exception and create a cool error handler
            Bukkit.broadcast(Component.text("§c✘ §7There's no kits available for this Arena!"));
        }

        for(int i = 0; i < kits.size(); i++){
            getInventory().setItem(i, kits.get(i).getGuiItem());
            setAction(i, event -> {
                int slot = event.getSlot();
                addOneToVoteAndUpdateItem(kits.get(slot).getName());
                event.getWhoClicked().sendMessage(" §8• §7You voted for §f" + kits.get(slot).getName() + "§7.");
                event.getWhoClicked().closeInventory();
                return true;
            });
            votes.put(kits.get(i).getName(), 0);
        }
    }

    private void addOneToVoteAndUpdateItem(@NotNull String kitName) {
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
            // TODO: 03/07/2023 Improve this
            Bukkit.broadcastMessage(ChatColor.GREEN + "The kit " + keyOfMaxValue + " has won the vote with" + maxEntry.getValue() + " votes.");
            return KitColl.get().get(keyOfMaxValue);
        } else {
            // TODO: 03/07/2023 Improve this
            System.out.println("The map is empty.");
        }
        // TODO: 03/07/2023 Change it to a thrown exception and create a cool error handler
        Bukkit.broadcastMessage("NULL?");
        return null;
    }

    public void open(@NotNull Set<UUID> players) {
        idConvertList(players).forEach(p -> p.openInventory(this.getInventory()));
    }
}
