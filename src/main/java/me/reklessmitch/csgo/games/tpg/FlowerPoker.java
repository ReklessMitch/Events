package me.reklessmitch.csgo.games.tpg;

import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.util.MUtil;
import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.Arena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.utils.DisplayItem;
import me.reklessmitch.csgo.utils.SerLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.reklessmitch.csgo.utils.UUIDUtil.idConvertList;

public class FlowerPoker extends Game {

    Arena arena;
    private static final Material seed = Material.WHEAT_SEEDS;
    Map<UUID, List<Material>> playersFlowers;

    List<Block> blocksToBeRemoved = new ArrayList<>();
    List<Material> flowerPowerValues = List.of(
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.DANDELION,
            Material.ORANGE_TULIP,
            Material.WITHER_ROSE,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.ROSE_BUSH,
            Material.CORNFLOWER);

    public FlowerPoker(Arena arena) {
        this.arena = arena;
        arena.setActive(true);
        arena.changed();
        setDisplayItem(new DisplayItem(
                Material.ROSE_BUSH,
                "&c&lFLOWER POKER - " + arena.getName(),
                List.of("&71v1 Flower Poker"),
                0
        ));
        setMaxPlayers(2);
        // shouldnt be needed but is?
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGames.get());
    }

    public Map<UUID, Map<Material, Integer>> countFlowers(Map<UUID, List<Material>> playersFlowers) {
        Map<UUID, Map<Material, Integer>> flowerCounts = new HashMap<>();

        for (Map.Entry<UUID, List<Material>> entry : playersFlowers.entrySet()) {
            UUID player = entry.getKey();
            List<Material> flowers = entry.getValue();
            Map<Material, Integer> countMap = new HashMap<>();
            for (Material flower : flowers) {
                if (flowerPowerValues.contains(flower)) {
                    int count = countMap.getOrDefault(flower, 0);
                    countMap.put(flower, count + 1);
                }
            }

            if (!countMap.isEmpty()) {
                flowerCounts.put(player, countMap);
            }
        }

        return flowerCounts;
    }

    public boolean someoneHasBlackFlower() {
        return this.playersFlowers.entrySet().stream().anyMatch(entry -> entry.getValue().contains(Material.WITHER_ROSE));
    }

    public void resetFlowers() {
        this.playersFlowers = new HashMap<>();
        this.getPlayers().forEach(player -> this.playersFlowers.put(player, new ArrayList<>()));
        // @todo add other player
    }

    @Override
    public void end() {
        super.end();
        arena.setActive(false);
        arena.changed();
        resetFlowers();
        resetLocations();
    }


    public void resetLocations(){
        blocksToBeRemoved.forEach(block -> block.setType(Material.AIR));
    }

    public void resetInventories(){
        idConvertList(getPlayers()).forEach(p ->
                p.getInventory().setContents(new ItemStack[]{new ItemStack(seed, 5)}));
    }

    @Override
    public void startGame() {
        resetFlowers();
        resetLocations();
        resetInventories();
        teleportPlayers();
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0,
                ChatColor.GREEN + "NEW ROUND", ChatColor.GRAY + "Place 5 flowers!")), 15L);
    }

    private void teleportPlayers() {
        List<UUID> players = getPlayers().stream().toList();
        int iterator = 0;
        for(SerLocation spawn : arena.getSpawnPoints()){
            UUID player = players.get(iterator);
            spawn.teleport(player);
            iterator++;
        }
    }

    public boolean checkIfAllPlaced() {
        for (Map.Entry<UUID, List<Material>> entry : playersFlowers.entrySet()) {
            List<Material> flowers = entry.getValue();
            if (flowers.size() < 5) {
                return false; // At least one player hasn't placed enough flowers
            }
        }
        return true; // All players have placed enough flowers
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlantEvent(BlockPlaceEvent event) {
        if (!getPlayers().contains(event.getPlayer().getUniqueId())) return;
        if (event.getItemInHand().getType() == seed) {
            Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
                Material flower = MUtil.random(flowerPowerValues);
                event.getBlock().setType(flower);
                blocksToBeRemoved.add(event.getBlock());
                playersFlowers.get(event.getPlayer().getUniqueId()).add(flower);
                if (checkIfAllPlaced()){
                    newRoundOrEnd();
                }
            }, 100L);
        }
    }

    private void newRoundOrEnd() {
        Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
            if (someoneHasBlackFlower()) {
                getPlayers().forEach(player -> MixinTitle.get().sendTitleMessage(player, 0, 20, 0,
                                ChatColor.RED + "BLACK FLOWER", ChatColor.GRAY + "Forcing round restart!"));
                Bukkit.getScheduler().runTaskLater(MiniGames.get(), this::startGame, 20L);
            } else {
                checkWhoWon();
            }
        }, 30L);
    }

    private void checkWhoWon() {
        Map<UUID, Map<Material, Integer>> flowerCounts = countFlowers(playersFlowers);
        List<UUID> players = getPlayers().stream().toList();
        int player1 = getHandValue(flowerCounts.get(players.get(0)));
        int player2 = getHandValue(flowerCounts.get(players.get(1)));

        if (player1 == player2) {
            MixinTitle.get().sendTitleMessage(getPlayers(), 0, 20, 0,
                    ChatColor.GREEN + "DRAW", ChatColor.GRAY + "Restarting round!");
            startGame();
        } else {
            String winnerName = player1 > player2 ? Bukkit.getOfflinePlayer(players.get(0)).getName() : Bukkit.getOfflinePlayer(players.get(1)).getName();
            idConvertList(getPlayers()).forEach(p -> p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&a&l" + winnerName + " &7won with &a&l" + getHandName(Math.max(player1, player2)))));
            end();
        }

    }

    private int getHandValue(Map<Material, Integer> frequencyMap){
        int maxFrequency = Collections.max(frequencyMap.values());
        switch (maxFrequency) {
            case 5 -> {return 5;}
            case 4 -> {return 4;}
            case 3 -> {return frequencyMap.size() == 2 ? 3 : 2;}
            case 2 -> {return 1;}
            default -> {return 0;}
        }
    }

    private String getHandName(int handValue){
        switch (handValue) {
            case 5 -> {return "Five of a kind";}
            case 4 -> {return "Four of a kind";}
            case 3 -> {return "Full house";}
            case 2 -> {return "Three of a kind";}
            case 1 -> {return "Two of a kind";}
            default -> {return "Nothing";}
        }
    }
}
