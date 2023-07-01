package me.reklessmitch.csgo.utils;

import lombok.Getter;
import java.util.List;

@Getter
public class ShopItem {

    // These Cannot be final because MCORE needs to be able to serialize them

    private DisplayItem displayItem;
    private int slot;
    private int cost;
    private List<String> commands;

    public ShopItem(DisplayItem item, int slot, int cost, List<String> commands) {
        this.displayItem = item;
        this.slot = slot;
        this.cost = cost;
        this.commands = commands;
    }

}
