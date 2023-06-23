package me.reklessmitch.csgo.cmd.other;

import me.reklessmitch.csgo.cmd.ArenaCommand;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdGetCustomData extends ArenaCommand {

    private static final CmdGetCustomData i = new CmdGetCustomData();
    public static CmdGetCustomData get() { return i; }

    public CmdGetCustomData(){
        this.addAliases("getcustomdata", "gcd");
    }

    @Override
    public void perform() {
        ItemMeta meta = me.getInventory().getItemInMainHand().getItemMeta();
        if(meta.hasCustomModelData()) {
            msg("Custom model data: " + meta.getCustomModelData());
        } else {
            msg("No custom model data");
        }
    }


}
