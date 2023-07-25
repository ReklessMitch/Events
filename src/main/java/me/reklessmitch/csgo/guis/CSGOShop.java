package me.reklessmitch.csgo.guis;

import com.massivecraft.massivecore.chestgui.ChestGui;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.mitchcurrency.configs.CPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;

public class CSGOShop extends ChestGui {

    public CSGOShop() {
        setInventory(Bukkit.createInventory(null, MConf.get().getCsgoShopRows() * 9, Component.text("CSGO Shop")));
        setAutoclosing(false);
        MConf.get().getCsgoShopItems().forEach(shopItem -> {
            getInventory().setItem(shopItem.getSlot(), shopItem.getDisplayItem().getGuiItem());
            setAction(shopItem.getSlot(), event -> {
                if(doCost((Player) event.getWhoClicked(), shopItem.getCost()) && shopItem.getCommands() != null) {
                    shopItem.getCommands().forEach(command -> {
                        switch (command) {
                            case "ah" -> event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                            case "ac" -> event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                            default -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", event.getWhoClicked().getName()));
                                event.getWhoClicked().sendMessage("§a✔ §7You have purchased §f" + shopItem.getDisplayItem().getItemName() + "§7!");
                            }
                        }
                    });
                }
                return true;
            });
        });
        add();
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
        p.openInventory(getInventory());
    }
}