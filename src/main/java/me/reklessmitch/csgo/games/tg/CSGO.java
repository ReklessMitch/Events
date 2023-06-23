package me.reklessmitch.csgo.games.tg;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.configs.TeamArena;
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
import org.bukkit.potion.PotionEffectType;
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
    boolean roundStarted = false;

    public CSGO(TeamArena arena) {
        this.arena = arena;
        this.bossBar = Bukkit.createBossBar(ChatColor.RED + "TERRORIST - " + ChatColor.WHITE + tScore + ChatColor.GREEN + " vs " + ChatColor.BLUE + "COUNTER TERRORIST - " + ChatColor.WHITE + ctScore, BarColor.RED, BarStyle.SOLID);
        setDisplayItem(new DisplayItem(
                Material.GOLDEN_SWORD,
                "&c&lCSGO - " + arena.getName(),
                List.of("&716 Round Gun Game"),
                10000
        ));
        setMaxPlayers(2);
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
        roundStarted = false;
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            playersList.replaceAll((player, status) -> false);
            teleportPlayersToTeamSpawn(tTeam, arena.getTeam1Spawns());
            teleportPlayersToTeamSpawn(ctTeam, arena.getTeam2Spawns());
            setPlayersCurrency();
        }, 20L);
        if(tScore + ctScore == 0) {
            MixinTitle.get().sendTitleMessage(getPlayers(), 0, 60, 0,
                    ChatColor.RED + "CSGO", ChatColor.GRAY + "First to 16 wins");
        }
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), this::openShop, 20L);


    }

    private void resetPlayersBossBar(){
        if(!bossBar.getPlayers().isEmpty()){
            bossBar.getPlayers().forEach(player -> bossBar.removePlayer(player));
        }
    }
    private void updateBossBar(){
        bossBar.setTitle("T " + tScore + " : CT" + ctScore);
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
        if(getPlayers().size() < getMaxPlayers()) return;
        super.start();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        getPlayers().forEach(player -> playersList.put(player, false));

        putPlayersOnTeam();
        setPlayersCurrency();
        newRound();
    }

    @EventHandler(ignoreCancelled = true)
    public void jumpEvent(PlayerJumpEvent event){
        if(getPlayers().contains(event.getPlayer().getUniqueId()) && !roundStarted) event.setCancelled(true);
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
        new Countdown(5).onTick(secondsLeft -> {
            if(!isActive()) return;
            getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0,
                            "Starting in", String.valueOf(secondsLeft)));
        }).onComplete(() -> roundStarted = true).start(MiniGames.get());
    }

    private void openShop(){
        setAllPlayersToSurvival();

        for(UUID player : playersList.keySet()) {
            Bukkit.getPlayer(player).addPotionEffect(PotionEffectType.SLOW.createEffect(20 * 20, 255));
            new CSGOShop().open(player);
        }
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), this::startRound, 20L * 15);
    }

    private void setPlayersCurrency(){
        getPlayers().forEach(player -> CPlayer.get(player).getCurrency(MConf.get().getCurrency()).add(Bukkit.getPlayer(player), 1500));
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

    private void addCurrencyForKill(Player killer){
        CPlayer.get(killer).getCurrency(MConf.get().getCurrency()).add(killer, 300);
    }
    private void playerDied(Player player){
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
    }
}
