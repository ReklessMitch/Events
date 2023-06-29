package me.reklessmitch.csgo.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDUtil {

    public static Player idConvert(UUID uuid){
        return Bukkit.getPlayer(uuid);
    }
}
