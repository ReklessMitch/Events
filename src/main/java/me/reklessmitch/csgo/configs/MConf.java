package me.reklessmitch.csgo.configs;

import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.ShopItem;
import org.bukkit.Material;

import java.util.List;

@Getter
@EditorName("config")
public class MConf extends Entity<MConf> {
    public static MConf i;
    public static MConf get() { return i; }

    boolean spleefEnabled = true;
    boolean flowerPowerEnabled = true;
    boolean ffaEnabled = true;

    String eventWorld = "Events";
    String spawnWorld = "GambleVerse";

    String spleefShovelName = "&aSpleef Shovel";
    String spleefSnowballName = "&bSpleef Snowball";

    String currency = "money";
    int csgoShopRows = 6;
    List<ShopItem> csgoShopItems = List.of(new ShopItem(
            new DisplayItem(
                    Material.GOLDEN_SWORD,
                    "AK-47",
                    List.of("1x AK-47", "Cost: $1000"),
                    10000
            ),
            1,
            1000,
            List.of("mw give %player_name% AK-47")
    ));


}
