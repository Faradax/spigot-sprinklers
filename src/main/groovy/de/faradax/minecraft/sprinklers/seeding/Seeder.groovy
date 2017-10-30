package de.faradax.minecraft.sprinklers.seeding

import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.material.MaterialData
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

class Seeder implements Sprinkler {

    private final Block block
    private final World world
    private final Random random = new Random()

    private BukkitTask effectTask
    private Plugin plugin
    private BukkitTask plantTask
    private final Material cropMaterial

    Seeder(Plugin plugin, Block block, Material cropMaterial) {
        this.cropMaterial = cropMaterial
        this.plugin = plugin
        this.block = block
        this.world = block.world
    }

    @Override
    void initialize(BukkitScheduler scheduler) {
        effectTask = scheduler.runTaskTimerAsynchronously(
                plugin,
                {
                    drawSeedParticles()
                },
                0,
                7
        )
        plantTask = scheduler.runTaskTimer(
                plugin,
                {
                    plantCrops()
                },
                0,
                5
        )
    }

    void deactivate() {
        this.effectTask.cancel()
        this.plantTask.cancel()
    }

    private void plantCrops() {
        def optionalFarmland = getRandomFarmland()
        optionalFarmland.ifPresent({ plantCropOnFarmland(it) })
    }

    private void plantCropOnFarmland(Block farmland) {
        def aboveFarmland = farmland.getRelative(BlockFace.UP)
        if (aboveFarmland.type == Material.AIR) {
            aboveFarmland.type = cropMaterial
            drawGrowthParticles(farmland)
        }
    }

    Optional<Block> getRandomFarmland() {
        def location = block.location
        def radius = 9
        def height = 2

        Location randomLocation = randomLocationInRadius(radius, height, location)
        def potentialBlock = world.getBlockAt(randomLocation)

        if (isBlockFarmland(potentialBlock)) {
            return Optional.of(potentialBlock)
        } else {
            return Optional.empty()
        }
    }

    private Location randomLocationInRadius(int radius, int height, Location location) {
        def xOffset = random.nextInt(radius)
        def yOffset = random.nextInt(radius)
        def zOffset = random.nextInt(height)

        def randomLocation =
                location.add(-4, 0, -4)
                        .add(xOffset, -zOffset, yOffset)
        randomLocation
    }

    private boolean isBlockFarmland(Block potentialBlock) {
        potentialBlock.type == Material.SOIL
    }

    private void drawSeedParticles() {

        def dx = random.nextDouble() - 0.5
        def dy = random.nextDouble() / 3 + 0.3
        def dz = random.nextDouble() - 0.5

        world.spawnParticle(
                Particle.BLOCK_DUST,
                block.location.add(0.5, 1, 0.5),
                0,
                dx, dy, dz,
                1.0,
                new MaterialData(Material.DIRT, (byte) 2)
        )
        world.spawnParticle(
                Particle.BLOCK_CRACK,
                block.location.add(0.5, 1, 0.5),
                0,
                0, 0, 0,
                1.0,
                new MaterialData(Material.DIRT, (byte) 2)
        )
    }

    private void drawGrowthParticles(Block cropBlock) {
        world.spawnParticle(
                Particle.VILLAGER_HAPPY,
                cropBlock.location.add(0.5, 0.1, 0.5),
                5,
                0, 0, 0,
                3
        )
        world.playSound(block.location, Sound.ENTITY_ARROW_HIT, 0.15f, random.nextFloat())
    }
}