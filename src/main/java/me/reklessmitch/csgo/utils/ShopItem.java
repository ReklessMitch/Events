package me.reklessmitch.csgo.utils;

import lombok.Getter;
import java.util.List;

@Getter
public class ShopItem {

    private final DisplayItem displayItem;
    private final int slot;
    private final int cost;
    private final List<String> commands;

    public ShopItem(DisplayItem item, int slot, int cost, List<String> commands) {
        this.displayItem = item;
        this.slot = slot;
        this.cost = cost;
        this.commands = commands;
    }

}
