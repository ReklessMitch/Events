package me.reklessmitch.csgo.utils;

import com.massivecraft.massivecore.util.MUtil;
import com.sk89q.worldedit.world.chunk.Chunk;
import me.reklessmitch.csgo.MiniGames;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class BDGen extends ChunkGenerator {

    private final String worldName;

    public BDGen(String worldName) {
        this.worldName = worldName;
    }

    public World createWorld() {
        World world = MiniGames.get().getServer().createWorld((new WorldCreator(this.worldName))

                .generator(this));
        world.setDifficulty(Difficulty.EASY);
        world.setTime(6000L);
        return world;
    }

    private static final List<Biome> BIOMES = List.of(Biome.PLAINS, Biome.FOREST, Biome.DESERT, Biome.SWAMP);

    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkGenerator.ChunkData chunkData = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Set the block at (x, y, z) in the chunk
                biome.setBiome(x, z, MUtil.random(BIOMES));
            }
        }
        return chunkData;
    }
}