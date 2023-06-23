package me.reklessmitch.csgo.cmd.arena;

import me.reklessmitch.csgo.cmd.ArenaCommand;

public class CmdArena extends ArenaCommand {
    private static final CmdArena i = new CmdArena();
    public static CmdArena get() { return i; }

    public final CmdArenaCreate arenaCreate = new CmdArenaCreate();
    public final CmdArenaAddSpawn cmdArenaAddSpawn = new CmdArenaAddSpawn();
    public final CmdSpleefAddFloor cmdSpleefAddFloor = new CmdSpleefAddFloor();
    public final CmdArenaTGAddSpawn cmdAddTGSpawn = new CmdArenaTGAddSpawn();
    public final CmdArenaParkourAddCheckPoint cmdArenaParkourAddCheckPoint = new CmdArenaParkourAddCheckPoint();

    public CmdArena() {
        this.addAliases("arena");
        this.addChild(this.arenaCreate);
        this.addChild(this.cmdArenaAddSpawn);
        this.addChild(this.cmdSpleefAddFloor);
        this.addChild(this.cmdAddTGSpawn);
        this.addChild(this.cmdArenaParkourAddCheckPoint);
    }
}
