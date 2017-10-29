package de.faradax.minecraft.sprinklers.event

import de.faradax.minecraft.sprinklers.Plugin
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Dispenser
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * EventListener that is responsible for detecting the construction and destruction of sprinklers.
 */
class SprinklerDetector implements Listener {

    private final Plugin sprinklerPlugin
    ItemStack boneMealItemStack = new ItemStack(Material.INK_SACK, 1, (short) 15)
    ItemStack seedItemStack = new ItemStack(Material.SEEDS, 1)
    ItemStack carrotItemStack = new ItemStack(Material.CARROT_ITEM, 1)
    ItemStack potatoStack = new ItemStack(Material.POTATO_ITEM, 1)
    ItemStack beetrootStack = new ItemStack(Material.BEETROOT, 1)

    SprinklerDetector(Plugin sprinklerPlugin) {
        this.sprinklerPlugin = sprinklerPlugin
    }

    /**
     * Responsible for preventing the ejection of bonemeal
     */
    @EventHandler
    void onDispense(final BlockDispenseEvent event) {
        Block block = event.block
        def contains5BoneMeal = dispenserContainsEnoughBoneMeal(block)
        def containsSeeds = dispenserContainsSeeds(block)
        if (isDispenserFacingUp(block) && (contains5BoneMeal || containsSeeds)) {
            event.cancelled = true
        }
    }

    /**
     * Responsible for turning on well-configured sprinklers when powered.
     * @param event
     */
    @EventHandler
    void onPowerChange(final BlockPhysicsEvent event) {
        Block block = event.block
        def powered = block.blockPowered
        def contains5BoneMeal = dispenserContainsEnoughBoneMeal(block)
        def containsSeeds = dispenserContainsSeeds(block)
        def facingUp = isDispenserFacingUp(block)
        if (powered && facingUp) {
            if (contains5BoneMeal) {
                sprinklerPlugin.initializeSprinkler(block)
            } else if (containsSeeds) {
                sprinklerPlugin.initializeSeeder(block)
            }
        } else {
            sprinklerPlugin.removeSprinkler(block)
        }
    }

    boolean isDispenser(Block block) {
        block.type == Material.DISPENSER
    }

    boolean isDispenserFacingUp(Block block) {
        if (isDispenser(block)) {
            def dispenserMaterialData = block.state.data as org.bukkit.material.Dispenser
            return dispenserMaterialData.facing == BlockFace.UP
        } else {
            return false
        }
    }

    private boolean dispenserContainsSeeds(Block block) {
        if (!isDispenser(block)) {
            return false
        }
        Dispenser dispenser = ((Dispenser) block.state)
        def inventory = dispenser.inventory
        return inventoryContainsEnoughSeeds(inventory)
    }

    private boolean dispenserContainsEnoughBoneMeal(Block block) {
        if (!isDispenser(block)) {
            return false
        }
        Dispenser dispenser = ((Dispenser) block.state)
        def inventory = dispenser.inventory
        return inventoryContainsEnoughBoneMeal(inventory)
    }

    private boolean inventoryContainsEnoughSeeds(Inventory inventory) {
        inventory.containsAtLeast(seedItemStack, 5) ||
        inventory.containsAtLeast(carrotItemStack, 5) ||
        inventory.containsAtLeast(potatoStack, 5) ||
        inventory.containsAtLeast(beetrootStack, 5)
    }

    private boolean inventoryContainsEnoughBoneMeal(Inventory inventory) {
        inventory.containsAtLeast(boneMealItemStack, 5)
    }

}
