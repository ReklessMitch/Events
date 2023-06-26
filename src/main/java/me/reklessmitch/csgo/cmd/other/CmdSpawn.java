package me.reklessmitch.csgo.cmd.other;

import me.reklessmitch.csgo.cmd.ArenaCommand;
import me.reklessmitch.csgo.configs.MConf;
import org.bukkit.Bukkit;

public class CmdSpawn extends ArenaCommand {

    private static final CmdSpawn i = new CmdSpawn();
    public static CmdSpawn get() { return i; }

    public CmdSpawn(){
        this.addAliases("spawn");
    }

    @Override
    public void perform() {
        me.teleport(Bukkit.getWorld(MConf.get().getSpawnWorld()).getSpawnLocation());
    }
}
