package me.reklessmitch.csgo.games.tg;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.neznamy.tab.shared.TAB;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.configs.TeamArena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.guis.CSGOShop;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.NameTagHider;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CSGO extends Game {

    TeamArena arena;
    BossBar bossBar;
    int maxScore = 16;
    int tScore = 0;
    int ctScore = 0;
    Map<UUID, Boolean> playersList = new HashMap<>();
    Set<UUID> tTeam = new HashSet<>();
    Set<UUID> ctTeam = new HashSet<>();

    public CSGO(TeamArena arena) {
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
            Player p = Bukkit.getPlayer(player);
            if(p != null && bossBar.getPlayers().contains(p)){
                bossBar.removePlayer(p);
            }
            tTeam.remove(player);
            ctTeam.remove(player);
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
        getPlayers().forEach(player -> bossBar.addPlayer(Bukkit.getPlayer(player)));
    }

    /**
     * 1. Get players onto split teams
     * 2. Teleport players to spawn locations
     * 3. Set players currencies
     * 4. Open CSGO GUI Shop for X seconds
     * 5. Start game, players can now move
     * 6. When player dies, teleport to lobby or smt
     * 7. If all players of team are dead, add score, new round
     */
    @Override
    public void start() {
        if (getPlayers().size() >= getMinPlayers() && !isStarting()){
            setStarting(true);
            new Countdown(30).onTick(tick -> {
                if(tick % 5 == 0 || tick <= 5){
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&7Game starting in...", "&c&l" + tick));
                }
            }).onComplete(() -> {
                if(getPlayers().size() >= getMinPlayers()) startGame();
                else {
                    setStarting(false);
                    getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 20, 0, "&c&lNot enough players!", "&7Game cancelled!"));
                }
            }).start(MiniGames.get());
        }
    }

    private void startGame(){
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

    private void hideNameTags() {
        NameTagHider hider = MiniGames.get().getNameTagHider();
        ctTeam.forEach(ctPlayer -> tTeam.forEach(tPlayer -> {
            hider.hideNametag(Bukkit.getPlayer(ctPlayer), Bukkit.getPlayer(tPlayer));
            hider.hideNametag(Bukkit.getPlayer(tPlayer), Bukkit.getPlayer(ctPlayer));
        }));
    }

    @Override
    public void end() {
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

    private void setPlayersCurrency(){
        getPlayers().forEach(player -> {
            CPlayer.get(player).getCurrency(MConf.get().getCurrency()).add(
                    player, 1500);
            Bukkit.getPlayer(player).setHealth(20);
            Bukkit.getPlayer(player).setFoodLevel(20);
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
