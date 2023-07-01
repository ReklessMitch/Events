package me.reklessmitch.csgo.games.todo;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.scoreboard.Line;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.configs.MConf;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.Countdown;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.TeleportUtils;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvert;
import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class BattleRoyale extends Game {

    private final Scoreboard scoreboard;
    private final Map<UUID, Integer> kills;
    private final Map<Integer, UUID> finishedPosition;
    private final BossBar bossBar;
    private final Arena arena;
    private WorldBorder border;
    private static final int START_BORDER_SIZE = 250;
    private static final int REDUCE_BORDER_EVERY_X_MINS = 10;
    private static final int REDUCE_BORDER_AMOUNT = 50;
    private static final int GRACE_PERIOD = 10;
    private boolean gracePeriodActive = true;

    public BattleRoyale(Arena arena){
        super();
        this.arena = arena;
        this.scoreboard = TabAPI.getInstance().getScoreboardManager().getRegisteredScoreboards().get("br");
        this.bossBar = Bukkit.createBossBar(ChatColor.RED + "BATTLEDOME", BarColor.RED, BarStyle.SOLID);
        kills = new HashMap<>();
        finishedPosition = new HashMap<>();
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.DIAMOND_SWORD,
                "&c&lBattle Arena",
                List.of("&7FFA Battle Arena"),
                0
        ));
        setMaxPlayers(50);
    }


    @Override
    public void removePlayer(UUID player){
        Player p = idConvert(player);
        if(p != null && bossBar.getPlayers().contains(p)){
            bossBar.removePlayer(p);
        }
        super.removePlayer(player);
    }
    private void showWinners(){
        StringBuilder message = new StringBuilder("&b&l -- BattleDome RESULTS -- \n");
        message.append("\n&6&lPlacements\n");
        for(int i = 0; i < Math.min(5, finishedPosition.size()); i++) {
            message.append("&3&l").append(i + 1).append(". &b&l").
                    append(Bukkit.getOfflinePlayer(finishedPosition.get(finishedPosition.size() -1 - i)).getName()).append("\n");
        }
        message.append("\n&6&lKill Leaders\n");
        MUtil.entriesSortedByValues(kills).stream().limit(5).forEach(entry -> message.append("&3&l")
                .append(Bukkit.getOfflinePlayer(entry.getKey()).getName())
                .append(" &7with &b&l").append(entry.getValue()).append(" &7kills\n"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.toString()));
    }

    @Override
    public void end(){
        showWinners();
        border.reset();
        arena.setActive(false);
        arena.changed();
        super.end();
    }

    @Override
    public void startGame() {
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
        setupWorld();
        sortPlayersInventories();
        teleportToSpawns();
        updateTab();
    }

    private void setupWorld(){
        World world = Bukkit.getWorld(MConf.get().getBrWorld());
        border = world.getWorldBorder();
        border.setCenter(arena.getSpawnPoint().getLocation());
        border.setSize(START_BORDER_SIZE * 2.0);
        world.setTime(1000);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setDifficulty(Difficulty.NORMAL);
    }
    private void updateTab(){
        String prefix = "%img_offset_-500% ";
        List<Line> lines = scoreboard.getLines();
        int size = getPlayers().size();
        lines.get(4).setText(prefix + "&fPlayers Left: " + size);
    }

    private void sortPlayersInventories() {
        idConvertList(getPlayers()).forEach(player -> {
            resetPlayer(player.getUniqueId());
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16), new ItemStack(Material.TORCH, 32));
        });
    }

    private void teleportToSpawns() {
        idConvertList(getPlayers()).forEach(player -> player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(999999, 10)));
        int radius = (START_BORDER_SIZE) - 20;
        TeleportUtils.spawnPlayersInRadius(arena.getSpawnPoint().getLocation(), radius, idConvertList(getPlayers()));
        doStartCountDown();
    }

    private void doStartCountDown() {
        new Countdown(10).onTick(tick -> {
            if(tick % 5 == 0 || tick <= 5){
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                    player, 0, 20, 0, "&c&lGame begins in", tick + " seconds"));
            }}).onComplete(() -> {
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(
                    player, 0, 20, 0, "&c&lGame has begun", ""));
            setHasStarted(true);
            gracePeriodCountDown();
        }).start(MiniGames.get());
    }


    private void resetPlayersBossBar(){
        if(!bossBar.getPlayers().isEmpty()){
            bossBar.getPlayers().forEach(bossBar::removePlayer);
        }
    }

    private void updateBossBar(){
        resetPlayersBossBar();
        idConvertList(getPlayers()).forEach(bossBar::addPlayer);
    }

    private void gracePeriodCountDown() {
        updateBossBar();
        bossBar.setTitle(ChatColor.RED + "GRACE PERIOD");
        bossBar.setVisible(true);
        new Countdown(GRACE_PERIOD * 60).onTick(tick -> {
            if(!isActive()) return;
            bossBar.setProgress((double) tick / (GRACE_PERIOD * 60));
            if(tick % 60 == 0){
                idConvertList(getPlayers()).forEach(player ->
                        player.sendMessage(ChatColor.RED + "GRACE PERIOD ENDS IN " + tick / 60 + " MINS"));
            }}).onComplete(() -> idConvertList(getPlayers()).forEach(player -> {
                player.sendMessage(ChatColor.RED + "GRACE PERIOD HAS ENDED");
                gracePeriodActive = false;
                borderShrinkCountDown();
            })).start(MiniGames.get());
    }

    private void borderShrinkCountDown(){
        if(border.getSize() <= 50 || !isActive()){return;}
        bossBar.setTitle(ChatColor.RED + "BORDER SHRINKING " + ChatColor.LIGHT_PURPLE + border.getSize() + " -> " + (border.getSize() - REDUCE_BORDER_AMOUNT));
        new Countdown(REDUCE_BORDER_EVERY_X_MINS * 60).onTick(tick -> {
            if(!isActive()) return;
            bossBar.setProgress((double) tick / (REDUCE_BORDER_EVERY_X_MINS * 60));
            if(tick % 60 == 0){
                idConvertList(getPlayers()).forEach(player ->
                        player.sendMessage(ChatColor.RED + "BORDER WILL SHRINK IN " + tick / 60 + " MINS"));
            }
        }).onComplete(() -> {
            border.setSize(border.getSize() - REDUCE_BORDER_AMOUNT, 60);
            borderShrinkCountDown();
        }).start(MiniGames.get());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        removePlayer(event.getPlayer().getUniqueId());
        finishedPosition.put(finishedPosition.size(), event.getPlayer().getUniqueId());
        if(event.getEntity().getKiller() != null){
            UUID killerID = event.getEntity().getKiller().getUniqueId();
            kills.put(killerID, kills.getOrDefault(killerID, 0) + 1);
            getPlayers().forEach(p -> idConvert(p).sendMessage(ChatColor.RED + event.getEntity().getName().toUpperCase()
                    + ChatColor.LIGHT_PURPLE + " has been eliminated by " + ChatColor.RED +
                    event.getEntity().getKiller().getName().toUpperCase() + "\n" + ChatColor.BLUE + getPlayers().size() + " players remaining!"));
        }
        if(getPlayers().size() == 1){
            UUID player = getPlayers().stream().toList().get(0);
            finishedPosition.put(finishedPosition.size(), player);
            end();
        }else{
            updateTab();
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

    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player
                && getPlayers().contains(event.getEntity().getUniqueId()) && gracePeriodActive){
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "You cannot attack players yet!");
        }
    }

}
