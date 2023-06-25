package me.reklessmitch.csgo.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NameTagHider {

    private final Plugin plugin;
    private final ProtocolManager protocolManager;

    public NameTagHider(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void hideNametag(Player targetPlayer, Player viewerPlayer) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPlayer().equals(viewerPlayer)) {
                    int entityId = event.getPacket().getIntegers().read(0);
                    if (entityId == targetPlayer.getEntityId()) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    public void showNametag(Player targetPlayer, Player viewerPlayer) {
        protocolManager.removePacketListeners(plugin);
    }
}