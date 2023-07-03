package me.reklessmitch.csgo.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDUtil {

    // TODO: 03/07/2023 Improve this
    public static @Nullable Player idConvert(@NotNull UUID uuid){
        return Bukkit.getPlayer(uuid);
    }

    // TODO: 03/07/2023 Remove this
    public static Set<Player> idConvertList(@NotNull Set<UUID> ids) {
        return ids.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
