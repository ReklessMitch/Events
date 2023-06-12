package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import me.reklessmitch.csgo.utils.ShopItem;
import org.bukkit.Material;

import java.util.List;

@Getter
@EditorName("config")
public class MConf extends Entity<MConf> {
    protected static MConf i;
    public static MConf get() { return i; }

    String spleefShovelName = "&aSpleef Shovel";
    String spleefSnowballName = "&bSpleef Snowball";

    String currency = "money";
    int csgoShopRows = 6;
    List<ShopItem> csgoShopItems = List.of(new ShopItem(
            Material.GOLDEN_SWORD,
            "Revolver",
            List.of("1x Revolver + 64 Ammo"),
            10000,
            1,
            1000,
            List.of("iagive %player_name% iaweapons:revolver 1 silent", "iagive %player_name% iaweapons:projectile 64 silent")
    ));


}
