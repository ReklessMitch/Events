package me.reklessmitch.csgo.games.tg;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.scoreboard.Line;
import me.neznamy.tab.api.scoreboard.Scoreboard;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;
import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class CSGO extends Game {

    private final Arena arena;
    private final BossBar bossBar;
    private int tScore = 0;
    private int ctScore = 0;
    private Map<UUID, Boolean> playersList = new HashMap<>();
    private Set<UUID> tTeam = new HashSet<>();
    private Set<UUID> ctTeam = new HashSet<>();
    private final Scoreboard sb;

    public CSGO(@NotNull Arena arena) {
        this.arena = arena;
        this.bossBar = Bukkit.createBossBar(ChatColor.RED + "TERRORIST - " + ChatColor.WHITE + tScore + ChatColor.GREEN + " vs " + ChatColor.BLUE + "COUNTER TERRORIST - " + ChatColor.WHITE + ctScore, BarColor.RED, BarStyle.SOLID);
        setDisplayItem(new DisplayItem(
                Material.FEATHER,
                "&c&lCSGO - " + arena.getName(),
                List.of("&716 Round Gun Game"),
                121
        ));
        setMaxPlayers(10);
        arena.setActive(true);
        arena.changed();
        String name = arena.getName();
        this.sb = TabAPI.getInstance().getScoreboardManager().getRegisteredScoreboards().
                get(name.toLowerCase());
    }


    private void updateTab(){
        List<Line> lines = sb.getLines();
        for(int i = 1; i < lines.size(); i++){
            lines.get(i).setText("%img_offset_-500%");
        }
        int start = 4;
        lines.get(3).setText("%img_offset_-500%" + ChatColor.RED + "Terrorist");
        for(UUID player: ctTeam){
            ChatColor colour = !playersList.get(player) ? ChatColor.GREEN : ChatColor.GRAY;
            lines.get(start).setText("%img_offset_-500% " + colour + Bukkit.getOfflinePlayer(player).getName());
            start++;
        }
        lines.get(start).setText("%img_offset_-500%" + ChatColor.RED + "Counter-Terrorist");
        start++;
        for(UUID player: tTeam){
            ChatColor colour = !playersList.get(player) ? ChatColor.GREEN : ChatColor.GRAY;
            lines.get(start).setText("%img_offset_-500% " + colour + Bukkit.getOfflinePlayer(player).getName());
            start++;
        }
    }


    @Override
    public void removePlayer(@NotNull UUID player){
        if(!isActive()) {
            super.removePlayer(player);
            updateTab();
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
                updateTab();
            }
        }
    }

    private boolean allTeamDead(@NotNull Set<UUID> team){
        for(UUID p : team){
            if(!playersList.get(p)) return false;
        }
        return true;
    }

    private void swapTeams(){
        Set<UUID> temp = tTeam;
        tTeam = ctTeam;
        ctTeam = temp;
        int tempS = tScore;
        tScore = ctScore;
        ctScore = tempS;
        resetPlayersCurrency();

    }

    private void newRound() {
        if(tScore + ctScore == 15) {swapTeams();}
        updateBossBar();
        bossBar.setVisible(true);
        if(tScore == 16 || ctScore == 16) {
            end();
            return;
        }
        setHasStarted(false);
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            playersList.replaceAll((player, status) -> false);
            updateTab();
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
            bossBar.getPlayers().forEach(bossBar::removePlayer);
        }
    }

    private void updateBossBar(){
        bossBar.setTitle(ChatColor.RED + "TERRORIST - " + ChatColor.WHITE + tScore + ChatColor.GREEN + " vs " +
                ChatColor.BLUE + "COUNTER TERRORIST - " + ChatColor.WHITE + ctScore);

        resetPlayersBossBar();
        idConvertList(getPlayers()).forEach(bossBar::addPlayer);
    }

    @Override
    public void startGame(){
        super.start();
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        getPlayers().forEach(player -> {
            resetPlayer(player);
            playersList.put(player, false);
        });

        putPlayersOnTeam();
        hideNameTags();
        resetPlayersCurrency();

        newRound();
    }

    private void resetPlayersCurrency() {
        getPlayers().forEach(player -> CPlayer.get(player).getCurrency(MConf.get().getCurrency()).set(player, 0));

    }

    private void hideNameTags(){
        List<PlayerDisguise> ctDisguises = List.of(new PlayerDisguise("BoltBag"), new PlayerDisguise("_Hunter_098"),  new PlayerDisguise("greenmachinejr"));
        List<PlayerDisguise> tDisguises = List.of(new PlayerDisguise("GuardrailHitter"), new PlayerDisguise("Technalite"), new PlayerDisguise("awildmikkel"));
        ctDisguises.forEach(disguise -> disguise.setNameVisible(false));
        tDisguises.forEach(disguise -> disguise.setNameVisible(false));
        ctTeam.forEach(p -> DisguiseAPI.disguiseToPlayers(Bukkit.getPlayer(p), MUtil.random(ctDisguises), idConvertList(getPlayers())));
        tTeam.forEach(p -> DisguiseAPI.disguiseToPlayers(Bukkit.getPlayer(p), MUtil.random(tDisguises), idConvertList(getPlayers())));
    }


    @Override
    public void end() {
        getPlayers().forEach(player -> DisguiseAPI.undisguiseToAll(idConvert(player)));
        arena.setActive(false);
        arena.changed();
        playersList = new HashMap<>();
        resetPlayersBossBar();
        String message = ctScore > tScore ? "CT wins" : "T wins";
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () ->
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 30, 0,
                message, "T:" + tScore + " - CT:" + ctScore)), 20L);
        super.end();
    }

    public void startRound(){
        new Countdown(15)
            .onTick(secondsLeft -> {
                if(secondsLeft <= 5 || secondsLeft % 5 == 0){
                    getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0,
                            "§7Round Begins in", "§c" + secondsLeft + "seconds"));
                }
            })
            .onComplete(() -> setHasStarted(true)).start(MiniGames.get());
    }

    private void openShop(){
        for(UUID player : getPlayers()) {
            new CSGOShop().open(player);
        }
    }

    private void newInventory(@NotNull UUID player){
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
            CPlayer.get(player).getCurrency(MConf.get().getCurrency()).add(player, 1500);
            newInventory(player);
        });

    }

    private void teleportPlayersToTeamSpawn(@NotNull Set<UUID> team, @NotNull List<SerLocation> spawns){
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

    private void checkTeamKill(UUID player, UUID killer){
        if(ctTeam.contains(player) && ctTeam.contains(killer) ||
            (tTeam.contains(player) && tTeam.contains(killer))){

            addCurrencyForKill(killer, -300);
        }else{
            addCurrencyForKill(killer, 300);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void deathEvent(PlayerDeathEvent event){
        UUID player = event.getEntity().getUniqueId();
        if(!getPlayers().contains(player)) return;
        if(event.getEntity().getKiller() != null) {
            checkTeamKill(event.getPlayer().getUniqueId(), event.getEntity().getKiller().getUniqueId());
        }
        playersList.put(player, true);
        event.setCancelled(true);
        playerDied(event.getEntity());
        if(allTeamDead(tTeam)){
            ctScore++;
            getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 30, 0,
                    "CT wins", "T:" + tScore + " - CT:" + ctScore));
            newRound();
        }
        if(allTeamDead(ctTeam)){
            tScore++;
            getPlayers().forEach(p -> MixinTitle.get().sendTitleMessage(p, 0, 30, 0,
                    "T wins", "T:" + tScore + " - CT:" + ctScore));
            newRound();
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player player){
            if(!getPlayers().contains(player.getUniqueId()) || isHasStarted()) return;
            if(event.getDamager() instanceof Player damager){
                event.setCancelled(true);
                damager.sendMessage(ChatColor.RED + "You can't damage players before the round starts");
            }
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

    private void addCurrencyForKill(UUID killer, int amount){
        CPlayer.get(killer).getCurrency(MConf.get().getCurrency()).add(killer, amount);
    }

    private void playerDied(@NotNull Player player){
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        updateTab();
    }
}
