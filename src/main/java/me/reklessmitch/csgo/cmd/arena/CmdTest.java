package me.reklessmitch.csgo.cmd.arena;

import dev.lone.itemsadder.api.CustomEntity;
import me.reklessmitch.csgo.cmd.ArenaCommand;

public class CmdTest extends ArenaCommand {

    private static final CmdTest i = new CmdTest();
    public static CmdTest get() { return i; }

    public CmdTest() {
        this.addAliases("test");
    }

    @Override
    public void perform() {
        CustomEntity.spawn("iavehicles:black_go_cart", me.getLocation());

    }
}
