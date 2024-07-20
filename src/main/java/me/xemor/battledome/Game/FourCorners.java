package me.xemor.battledome.Game;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FourCorners extends BiomeProvider {

    private final static List<Biome> trees = List.of(Biome.TAIGA, Biome.SPARSE_JUNGLE, Biome.FLOWER_FOREST, Biome.FOREST, Biome.SAVANNA);
    private final static List<Biome> general = List.of(Biome.PLAINS, Biome.SUNFLOWER_PLAINS, Biome.DESERT, Biome.TAIGA, Biome.SPARSE_JUNGLE, Biome.FLOWER_FOREST, Biome.FOREST, Biome.SAVANNA);
    private final List<Biome> generated = new ArrayList<>(4);

    public FourCorners() {
        generated.add(trees.get(ThreadLocalRandom.current().nextInt(4)));
        generated.add(general.get(ThreadLocalRandom.current().nextInt(4)));
        generated.add(general.get(ThreadLocalRandom.current().nextInt(4)));
        generated.add(general.get(ThreadLocalRandom.current().nextInt(4)));
        Collections.shuffle(generated);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        if (x < 0 && z < 0) return generated.get(0);
        else if (x > 0 && z < 0) return generated.get(1);
        else if (x < 0 && z > 0) return generated.get(2);
        else return generated.get(3);
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return generated;
    }

}
