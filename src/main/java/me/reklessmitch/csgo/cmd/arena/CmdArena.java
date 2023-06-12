package me.reklessmitch.csgo.cmd.arena;

import me.reklessmitch.csgo.cmd.ArenaCommand;

public class CmdArena extends ArenaCommand {
    private static final CmdArena i = new CmdArena();
    public static CmdArena get() { return i; }

    public CmdArenaCreate arenaCreate = new CmdArenaCreate();
    public CmdArenaAddSpawn cmdArenaAddSpawn = new CmdArenaAddSpawn();
    public CmdArenaJoin cmdArenaJoin = new CmdArenaJoin();
    public CmdSpleefAddFloor cmdSpleefAddFloor = new CmdSpleefAddFloor();

    public CmdArena() {
        this.addAliases("arena");
        this.addChild(this.arenaCreate);
        this.addChild(this.cmdArenaAddSpawn);
        this.addChild(this.cmdArenaJoin);
        this.addChild(this.cmdSpleefAddFloor);
    }
}
