package de.faradax.minecraft.sprinklers.seeding

import org.bukkit.CropState
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.material.Crops
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

/**
 * Created by dockworker on 30.10.17.
 */
class Fertilizer implements Sprinkler {

    private final Plugin plugin
    private final Block block
    private BukkitTask effectTask
    private BukkitTask cropTask
    private final World world
    private Random random = new Random()

    Fertilizer(Plugin plugin, Block block) {

        this.block = block
        this.world = block.world
        this.plugin = plugin
    }

    @Override
    void initialize(BukkitScheduler scheduler) {
        effectTask = scheduler.runTaskTimer(
                plugin,
                {
                    drawSprinklerParticles()
                },
                0,
                7
        )
        cropTask = scheduler.runTaskTimer(
                plugin,
                {
                    checkCrops()
                },
                0,
                3
        )

    }

    @Override
    void deactivate() {
        effectTask.cancel()
        cropTask.cancel()
    }

    private void checkCrops() {
        def optionalCrop = getRandomCropAroundLocation(block.location)
        optionalCrop.ifPresent({ Block crop ->
            def increasedBlockState = (crop.state.data as Crops).getState().data + 1 + random.nextInt(3)
            byte newBlockState = Math.min(0x7, increasedBlockState)
            crop.data = new Crops(crop.type, CropState.getByData(newBlockState)).data
            drawGrowthParticles(crop)
        })
    }

    private void drawGrowthParticles(Block cropBlock) {
        world.spawnParticle(
                Particle.VILLAGER_HAPPY,
                cropBlock.location.add(0.5, 0.1, 0.5),
                5,
                0, 0, 0,
                3
        )
    }

    private void drawSprinklerParticles() {
        double angle = world.getFullTime() / 100.0
        drawWaterCannon(angle)
        drawWaterCannon(angle + (2 * Math.PI / 3.0))
        drawWaterCannon(angle - (2 * Math.PI / 3.0))
    }

    private void drawWaterCannon(double angle) {
        for (int i = 0; i < 40; ++i) {
            def location =
                    block.location
                            .add(0.5, 1, 0.5)
                            .add(
                            i * 0.10 * Math.sin(angle),
                            0.05 * i - 0.0004 * (i**2),
                            i * 0.10 * Math.cos(angle))



            world.spawnParticle(
                    Particle.WATER_DROP,
                    location,
                    2,
                    0, 0, 0,
                    0
            )
        }
    }

    Optional<Block> getRandomCropAroundLocation(final Location location) {
        def xOffset = random.nextInt(9)
        def yOffset = random.nextInt(9)
        def zOffset = random.nextInt(2)
        def randomLocation = location.add(-4, 0, -4).add(xOffset, zOffset, yOffset)
        def potentialBlock = world.getBlockAt(randomLocation)

        def materialData = potentialBlock.type.data
        if (materialData == Crops) {
            return Optional.of(potentialBlock)
        } else {
            return Optional.empty()
        }
    }

}
