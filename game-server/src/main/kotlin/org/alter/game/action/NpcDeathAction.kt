package org.alter.game.action

import dev.openrune.cache.CacheManager.getAnim
import org.alter.game.model.LockState
import org.alter.game.model.attr.KILLER_ATTR
import org.alter.game.model.entity.AreaSound
import org.alter.game.model.entity.Npc
import org.alter.game.model.entity.Pawn
import org.alter.game.model.entity.Player
import org.alter.game.model.move.stopMovement
import org.alter.game.model.queue.QueueTask
import org.alter.game.model.queue.TaskPriority
import org.alter.game.model.weightedTableBuilder.roll
import org.alter.game.plugin.Plugin
import org.alter.game.service.log.LoggerService
import java.lang.ref.WeakReference

/**
 * This class is responsible for handling npc death events.
 *
 * @author Tom <rspsmods@gmail.com>
 */
object NpcDeathAction {
    var deathPlugin: Plugin.() -> Unit = {
        val npc = ctx as Npc
        if (!npc.world.plugins.executeNpcFullDeath(npc)) {
            npc.interruptQueues()
            npc.stopMovement()
            npc.lock()
            npc.queue(TaskPriority.STRONG) {
                death(npc)
            }
        }
    }

    suspend fun QueueTask.death(npc: Npc) {
        val world = npc.world
        val deathAnimation = npc.combatDef.deathAnimation
        val deathSound = npc.combatDef.defaultDeathSound
        val respawnDelay = npc.combatDef.respawnDelay
        var killer: Pawn? = null
        npc.damageMap.getMostDamage()?.let {
            if (it is Player) {
                killer = it
                world.getService(LoggerService::class.java, searchSubclasses = true)?.logNpcKill(it, npc)
            }
            npc.attr[KILLER_ATTR] = WeakReference(it)
        }
        world.plugins.executeNpcPreDeath(npc)
        npc.resetFacePawn()

        if (npc.combatDef.defaultDeathSoundArea) {
            world.spawn(AreaSound(npc.tile, deathSound, npc.combatDef.defaultDeathSoundRadius, npc.combatDef.defaultDeathSoundVolume))
        } else {
            (killer as? Player)?.playSound(deathSound, npc.combatDef.defaultDeathSoundVolume)
        }
        deathAnimation.forEach { anim ->
            val def = getAnim(anim)
            npc.animate(def.id, def.cycleLength)
            wait(def.cycleLength + 1)
        }
        world.plugins.executeNpcDeath(npc)
        world.plugins.anyNpcDeath.forEach {
            npc.executePlugin(it)
        }
        /**
         * 10 took 0ms.
         */
        npc.damageMap.getMostDamage()?.let {    pawn ->
            val player = pawn as Player
            val lootTables = npc.combatDef.LootTables ?: return@let
            roll(player, lootTables).forEach {
                it.ownerUID = player.uid
                it.tile = npc.getCentreTile()
                it.timeUntilPublic = world.gameContext.gItemPublicDelay
                it.timeUntilDespawn = world.gameContext.gItemDespawnDelay
                world.spawn(it)
            }
        }
        if (npc.respawns) {
            npc.reset()
            wait(respawnDelay)
            npc.avatar.setInaccessible(false)
            world.plugins.executeNpcSpawn(npc)
        } else {
            world.remove(npc)
        }
    }

    private fun Npc.reset() {
        lock = LockState.NONE
        tile = spawnTile
        avatar.setInaccessible(true)
        attr.clear()
        timers.clear()
        world.setNpcDefaults(this)
    }
}
