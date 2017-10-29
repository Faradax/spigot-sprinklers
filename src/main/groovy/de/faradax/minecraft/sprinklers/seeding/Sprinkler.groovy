package de.faradax.minecraft.sprinklers.seeding

import org.bukkit.scheduler.BukkitScheduler

interface Sprinkler {
    void initialize(BukkitScheduler scheduler)
    void deactivate()
}