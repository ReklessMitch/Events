package me.reklessmitch.csgo.utils;

import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class BDGen extends ChunkGenerator {

    private static final List<Biome> BIOMES = List.of(Biome.PLAINS, Biome.FOREST, Biome.DESERT, Biome.SWAMP);

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Set the block at (x, y, z) in the chunk
                biome.setBiome(x, z, MUtil.random(BIOMES));
            }
        }
        return chunkData;
    }
}