package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.utils.ShopItem;
import me.reklessmitch.mitchcurrency.configs.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CSGOShop extends ChestGui implements Listener {

    public CSGOShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, MConf.get().getCsgoShopRows() * 9, "CSGO Shop");
        MConf.get().getCsgoShopItems().forEach(shopItem -> inventory.setItem(shopItem.getSlot(), shopItem.getGuiItem()));
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
        player.openInventory(this.getInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getClickedInventory() != this.getInventory()) return;
        event.setCancelled(true);
        ShopItem item = MConf.get().getCsgoShopItems().stream().filter(shopItem -> shopItem.getSlot() == event.getSlot()).findFirst().orElse(null);
        if(item == null) return;
        if(doCost((Player) event.getWhoClicked(), item.getCost()) && item.getCommands() != null) {
            item.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", event.getWhoClicked().getName())));
        }
    }

    public boolean doCost(Player player, int cost){
        CPlayer pConfig = CPlayer.get(player.getUniqueId());
        long pMoney = pConfig.getCurrency(MConf.get().getCurrency()).getAmount();
        if(cost > pMoney) {
            player.sendMessage(ChatColor.RED + "You can't afford this item!");
            return false;
        }
        pConfig.getCurrency(MConf.get().getCurrency()).take(cost);
        pConfig.changed();
        return true;
    }
}