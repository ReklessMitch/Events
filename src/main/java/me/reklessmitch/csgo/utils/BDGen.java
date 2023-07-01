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
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        ChunkData chunkData = createChunkData(world);

        // Iterate over each block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomeGrid.setBiome(x, z, MUtil.random(BIOMES));
            }
        }

        return chunkData;
    }


}