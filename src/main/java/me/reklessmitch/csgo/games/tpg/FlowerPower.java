package me.reklessmitch.csgo.games.tpg;

import me.reklessmitch.csgo.MiniGames;
import me.reklessmitch.csgo.configs.FlowerPowerArena;
import me.reklessmitch.csgo.games.Game;
import me.reklessmitch.csgo.games.TwoPlayerGame;
import me.reklessmitch.csgo.utils.SpawnLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


import java.util.*;

public class FlowerPower extends TwoPlayerGame {

    FlowerPowerArena arena;
    private final Random random = new Random();
    private static final Material seed = Material.WHEAT_SEEDS;
    Map<Player, List<Material>> playersFlowers;
    List<Material> flowerPowerValues = List.of(Material.POPPY, Material.BLUE_ORCHID, Material.DANDELION, Material.ORANGE_TULIP, Material.WITHER_ROSE, Material.WHITE_TULIP, Material.PINK_TULIP, Material.ROSE_BUSH);

    public FlowerPower(FlowerPowerArena arena) {
        this.arena = arena;
        arena.setActive(true);
        // shouldnt be needed but is?
        Bukkit.getServer().getPluginManager().registerEvents(
                this, MiniGames.get());
    }

    public Map<Player, Map<Material, Integer>> countFlowers(Map<Player, List<Material>> playersFlowers) {
        Map<Player, Map<Material, Integer>> flowerCounts = new HashMap<>();

        for (Map.Entry<Player, List<Material>> entry : playersFlowers.entrySet()) {
            Player player = entry.getKey();
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
        return this.playersFlowers.entrySet().stream().anyMatch(entry -> entry.getValue().contains(Material.WITHER_ROSE));//((entry.getValue()).contains(Material.WHITE_TULIP) || (entry.getValue()).contains(Material.WITHER_ROSE)));
    }

    public void resetFlowers() {
        this.playersFlowers = Map.of(
                getPlayers().get(0), new ArrayList<>());
        // @todo add other player
    }

    @Override
    public void start() {
        Bukkit.broadcastMessage("" + getPlayers().size() + "SIZE");
        if (getPlayers().size() == 2) {
            setActive(true);
            Bukkit.broadcastMessage("Starting Flower Power");
            newRound();
        }
    }

    public void resetLocations(List<SpawnLocation> locations){
        locations.forEach(location -> {
            Block block = location.getLocation().getBlock();
            block.setType(Material.FARMLAND);
            // get block above and set to air
            block.getRelative(0, 1, 0).setType(Material.AIR);
        });
    }

    public void resetInventories(){
        getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.getInventory().addItem(new ItemStack(seed, 5));
        });
    }

    public void newRound() {
        Bukkit.broadcastMessage("New Round");
        resetFlowers();
        resetLocations(arena.getRedSpawnPoints());
        resetLocations(arena.getBlueSpawnPoints());
        resetInventories();
    }

    public boolean checkIfAllPlaced() {
        for (Map.Entry<Player, List<Material>> entry : playersFlowers.entrySet()) {
            List<Material> flowers = entry.getValue();

            if (flowers.size() < 5) {
                Bukkit.broadcastMessage("Not all placed: " + flowers.size());
                return false; // At least one player hasn't placed enough flowers
            } else {
                Bukkit.broadcastMessage("All placed: " + flowers.size());
            }
        }

        return true; // All players have placed enough flowers
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlantEvent(BlockPlaceEvent event) {
        if (!getPlayers().contains(event.getPlayer())) return;
        if (event.getItemInHand().getType() == seed) {
            Bukkit.getScheduler().runTaskLater(MiniGames.get(), () -> {
                Material flower = flowerPowerValues.get(random.nextInt(flowerPowerValues.size()));
                event.getBlock().setType(flower);
                playersFlowers.get(event.getPlayer()).add(flower);
                if (checkIfAllPlaced()){
                    if (someoneHasBlackFlower()) {newRound();
                    } else {checkWhoWon();}
                }
            }, 100L);
        }
    }

    private void checkWhoWon() {
        Map<Player, Map<Material, Integer>> flowerCounts = countFlowers(playersFlowers);
        for (Map.Entry<Player, Map<Material, Integer>> entry : flowerCounts.entrySet()) {
            Map<Material, Integer> countMap = entry.getValue();
            for (Map.Entry<Material, Integer> countEntry : countMap.entrySet()) {
                Bukkit.broadcastMessage(countEntry.getKey().name() + " " + countEntry.getValue());
            }
        }
        Bukkit.broadcastMessage("Someone won!");
    }
}
