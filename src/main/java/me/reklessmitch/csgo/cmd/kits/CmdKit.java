package me.reklessmitch.csgo.cmd.kits;

import me.reklessmitch.csgo.cmd.*;

public class CmdKit extends ArenaCommand {
    private static final CmdKit i = new CmdKit();
    public static CmdKit get() { return i; }

    public final CmdKitCreate createKit = new CmdKitCreate();

    public CmdKit() {
        this.addAliases("ckit", "csgokit");
        this.addChild(this.createKit);
    }
}
