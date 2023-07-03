package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.utils.ShopItem;
import me.reklessmitch.mitchcurrency.configs.CPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

public class CSGOShop extends ChestGui implements Listener {

    public CSGOShop() {
        Inventory inventory = Bukkit.createInventory(null, MConf.get().getCsgoShopRows() * 9, Component.text("CSGO Shop"));
        MConf.get().getCsgoShopItems().forEach(shopItem -> inventory.setItem(shopItem.getSlot(), shopItem.getDisplayItem().getGuiItem()));
        this.setInventory(inventory);
        MiniGames.get().getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    // TODO: 03/07/2023 Improve this
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getClickedInventory() != this.getInventory()) return;
        event.setCancelled(true);
        ShopItem item = MConf.get().getCsgoShopItems().stream().filter(shopItem -> shopItem.getSlot() == event.getSlot()).findFirst().orElse(null);
        if(item == null) return;
        if(doCost((Player) event.getWhoClicked(), item.getCost()) && item.getCommands() != null) {
            item.getCommands().forEach(command -> {
                switch (command) {
                    case "ah" -> event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    case "ac" -> event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    default -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", event.getWhoClicked().getName()));
                        event.getWhoClicked().sendMessage("§a✔ §7You have purchased §f" + item.getDisplayItem().getItemName() + "§7!");
                    }
                }
            });
        }
    }

    public boolean doCost(@NotNull Player player, int cost){
        CPlayer pConfig = CPlayer.get(player.getUniqueId());
        long pMoney = pConfig.getCurrency(MConf.get().getCurrency()).getAmount();

        if(cost > pMoney) {
            player.sendMessage("§c✘ §7You cannot afford this item!");
            return false;
        }

        pConfig.getCurrency(MConf.get().getCurrency()).take(player.getUniqueId(), cost);
        pConfig.changed();
        return true;
    }

    public void open(@NotNull UUID player){
        Player p = idConvert(player);
        if(p == null) return;
        p.openInventory(this.getInventory());
    }

    public void close(@NotNull Player player){
        player.closeInventory();
    }
}