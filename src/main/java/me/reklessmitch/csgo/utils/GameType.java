package me.reklessmitch.csgo.utils;

public enum GameType {
    FLOWER_POWER("FP"),
    CSGO("CSGO"),
    OIAC("OIAC"),
    SPLEEF("SPLEEF");

    private final String className;

    GameType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
