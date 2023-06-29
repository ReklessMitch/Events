package me.reklessmitch.csgo.games.tg;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.colls.KitColl;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.guis.CSGOShop;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.SerLocation;
import me.reklessmitch.mitchcurrency.configs.CPlayer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;
import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class CSGO extends Game {

    Arena arena;
    BossBar bossBar;
    int maxScore = 16;
    int tScore = 0;
    int ctScore = 0;
    Map<UUID, Boolean> playersList = new HashMap<>();
    Set<UUID> tTeam = new HashSet<>();
    Set<UUID> ctTeam = new HashSet<>();

    public CSGO(Arena arena) {
        this.arena = arena;
        this.bossBar = Bukkit.createBossBar(ChatColor.RED + "TERRORIST - " + ChatColor.WHITE + tScore + ChatColor.GREEN + " vs " + ChatColor.BLUE + "COUNTER TERRORIST - " + ChatColor.WHITE + ctScore, BarColor.RED, BarStyle.SOLID);
        setDisplayItem(new DisplayItem(
                Material.GOLDEN_SWORD,
                "&c&lCSGO - " + arena.getName(),
                List.of("&716 Round Gun Game"),
                10000
        ));
        setMaxPlayers(10);
        arena.setActive(true);
        arena.changed();
    }

    @Override
    public void removePlayer(UUID player){
        if(!isActive()) {
            super.removePlayer(player);
        }else {
            Player p = idConvert(player);
            if(p != null && bossBar.getPlayers().contains(p)){
                bossBar.removePlayer(p);
            }
            tTeam.remove(player);
            ctTeam.remove(player);
            DisguiseAPI.undisguiseToAll(idConvert(player));
            if (tTeam.isEmpty() || ctTeam.isEmpty()) {
                end();
            } else {
                super.removePlayer(player);
            }
        }
    }

    private boolean allTeamDead(Set<UUID> team){
        for(UUID p : team){
            if(!playersList.get(p)) return false;
        }
        return true;
    }

    private void newRound() {
        updateBossBar();
        bossBar.setVisible(true);
        if(tScore == maxScore || ctScore == maxScore) {
            end();
            return;
        }
        setHasStarted(false);
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            playersList.replaceAll((player, status) -> false);
            teleportPlayersToTeamSpawn(tTeam, arena.getTeam1Spawns());
            teleportPlayersToTeamSpawn(ctTeam, arena.getTeam2Spawns());
            setPlayersCurrency();
        }, 20L);

        if(tScore + ctScore == 0) {
            getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 60, 0,
                    ChatColor.RED + "CSGO", ChatColor.GRAY + "First to 16 wins"));
        }
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            openShop();
            setAllPlayersToSurvival();
            startRound();
        }, 20L);
    }

    private void resetPlayersBossBar(){
        if(!bossBar.getPlayers().isEmpty()){
            bossBar.getPlayers().forEach(player -> bossBar.removePlayer(player));
        }
    }

    private void updateBossBar(){
        bossBar.setTitle(ChatColor.RED + "TERRORIST - " + ChatColor.WHITE + tScore + ChatColor.GREEN + " vs " +
                ChatColor.BLUE + "COUNTER TERRORIST - " + ChatColor.WHITE + ctScore);

        resetPlayersBossBar();
        idConvertList(getPlayers()).forEach(player -> bossBar.addPlayer(player));
    }

    @Override
    public void startGame(){
        super.start();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        getPlayers().forEach(player -> playersList.put(player, false));

        putPlayersOnTeam();
        hideNameTags();
        resetPlayersCurrency();
        newRound();
    }

    private void resetPlayersCurrency() {
        getPlayers().forEach(player -> CPlayer.get(player).getCurrency(MConf.get().getCurrency()).set(player, 0));

    }

    private void hideNameTags(){
        List<PlayerDisguise> ctDisguises = List.of(new PlayerDisguise("_Hunter_098"), new PlayerDisguise(""));
        List<PlayerDisguise> tDisguises = List.of(new PlayerDisguise(""), new PlayerDisguise(""));
        ctDisguises.forEach(disguise -> disguise.setNameVisible(false));
        tDisguises.forEach(disguise -> disguise.setNameVisible(false));
        ctTeam.forEach(p -> DisguiseAPI.disguiseToPlayers(Bukkit.getPlayer(p), MUtil.random(ctDisguises), idConvertList(getPlayers())));
        tTeam.forEach(p -> DisguiseAPI.disguiseToPlayers(Bukkit.getPlayer(p), MUtil.random(tDisguises), idConvertList(getPlayers())));
    }


    @Override
    public void end() {
        getPlayers().forEach(player -> DisguiseAPI.undisguiseToAll(idConvert(player)));
        super.end();

        arena.setActive(false);
        arena.changed();
        playersList = new HashMap<>();
        resetPlayersBossBar();
        String message = ctScore > tScore ? "CT wins" : "T wins";
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () ->
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 30, 0,
                message, "T:" + tScore + " - CT:" + ctScore)), 20L);
    }

    public void startRound(){
        new Countdown(15)
            .onTick(secondsLeft -> {
                if(secondsLeft <= 5 || secondsLeft % 5 == 0){
                    getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0,
                            ChatColor.GRAY + "Round Begins in", ChatColor.RED + String.valueOf(secondsLeft) + "seconds"));
                }
            })
            .onComplete(() -> setHasStarted(true)).start(MiniGames.get());
    }

    private void openShop(){
        for(UUID player : playersList.keySet()) {
            new CSGOShop().open(player);
        }
    }

    private void newInventory(UUID player){
        Player p = idConvert(player);
        p.setHealth(20);
        p.setFoodLevel(20);
        Inventory inventory = p.getInventory();
        if(inventory.isEmpty()){
            KitColl.get().get("Pistol").giveAllItems(p);
        }
    }
    private void setPlayersCurrency(){
        getPlayers().forEach(player -> {
            CPlayer.get(player).getCurrency(MConf.get().getCurrency()).add(
                    player, 1500);
            newInventory(player);
        });

    }

    private void teleportPlayersToTeamSpawn(Set<UUID> team, @NotNull List<SerLocation> spawns){
        SerLocation spawn = MUtil.random(spawns);
        if(spawn == null) return;
        team.forEach(spawn::teleport);
    }


    private void putPlayersOnTeam(){
        int midWayPoint = getPlayers().size() / 2;
        List<UUID> p = getPlayers().stream().toList();
        for(int i = 0; i < getPlayers().size(); i++){
            if(i < midWayPoint) {
                tTeam.add(p.get(i));}
            else{
                ctTeam.add(p.get(i));}
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void deathEvent(PlayerDeathEvent event){
        UUID player = event.getEntity().getUniqueId();
        if(!playersList.containsKey(player)) return;
        addCurrencyForKill(event.getEntity().getKiller());
        playersList.put(player, true);
        event.setCancelled(true);
        playerDied(event.getEntity());
        if(allTeamDead(tTeam)){
            ctScore++;
            playersList.keySet().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 30, 0,
                    "CT wins", "T:" + tScore + " - CT:" + ctScore));
            newRound();
        }
        if(allTeamDead(ctTeam)){
            tScore++;
            playersList.keySet().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 30, 0,
                    "T wins", "T:" + tScore + " - CT:" + ctScore));
            newRound();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerID = event.getPlayer().getUniqueId();
        if (!getPlayers().contains(playerID)) return;
        if (!isHasStarted()) {
            event.setCancelled(true);
        }
    }

    private void addCurrencyForKill(Player killer){
        CPlayer.get(killer).getCurrency(MConf.get().getCurrency()).add(killer.getUniqueId(), 300);
    }

    private void playerDied(Player player){
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
    }
}
