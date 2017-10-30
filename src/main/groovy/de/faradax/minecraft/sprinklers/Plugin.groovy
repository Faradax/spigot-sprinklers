package de.faradax.minecraft.sprinklers

import de.faradax.minecraft.sprinklers.event.SprinklerDetector
import de.faradax.minecraft.sprinklers.seeding.Fertilizer
import de.faradax.minecraft.sprinklers.seeding.Seeder
import de.faradax.minecraft.sprinklers.seeding.Sprinkler
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main file for Sprinkler Plugin.
 */
class Plugin extends JavaPlugin {
    private Map<Block, Sprinkler> sprinklers = [:]

    @Override
    void onEnable() {
        super.onEnable()
        def sprinklerDetector = new SprinklerDetector(this)
        server.pluginManager.registerEvents(sprinklerDetector, this)
    }

    @Override
    void onDisable() {
        super.onDisable()
    }

    void removeSprinkler(Block block) {
        sprinklers.get(block)?.deactivate()
        sprinklers.remove(block)
    }

    void initializeSprinkler(Block dispenserBlock) {
        if (!sprinklers.containsKey(dispenserBlock)) {
            def fertilizer = new Fertilizer(this, dispenserBlock)
            fertilizer.initialize(server.scheduler)
            sprinklers.put(dispenserBlock, fertilizer)
        }
    }


    void initializeSeeder(Block block) {
        if (!sprinklers.containsKey(block)) {
            def seeder = new Seeder(this, block, Material.CROPS)
            seeder.initialize(server.scheduler)
            sprinklers.put(block, seeder)
        }
    }
}
