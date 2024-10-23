package org.alter.game.model.move

import org.alter.game.model.Tile
import org.alter.game.model.attr.*
import org.alter.game.model.entity.*
import org.alter.game.model.queue.QueueTask
import org.alter.game.model.queue.TaskPriority
import org.alter.game.model.timer.RESET_PAWN_FACING_TIMER
import org.alter.game.plugin.Plugin
import java.lang.ref.WeakReference

object PawnPathAction {
    private const val ITEM_USE_OPCODE = -1

    val walkPlugin: Plugin.() -> Unit = {
        val pawn = ctx as Pawn
        val world = pawn.world
        val other = pawn.attr[INTERACTING_NPC_ATTR]?.get() ?: pawn.attr[INTERACTING_PLAYER_ATTR]?.get()!!
        val opt = pawn.attr[INTERACTING_OPT_ATTR]!!

        /*
         * Some interactions only require line-of-sight range, such as npcs
         * behind cells or booths. This allows for diagonal interaction.
         *
         * Set to null for default interaction range.
         */
        val lineOfSightRange = if (other is Npc) world.plugins.getNpcInteractionDistance(other.id) else null
        pawn.queue(TaskPriority.STANDARD) {
            terminateAction = {
                pawn.stopMovement()
                if (pawn is Player) {
                    pawn.setMapFlag()
                }
            }
            walk(this, pawn, other, opt, lineOfSightRange)
        }
    }

    val itemUsePlugin: Plugin.() -> Unit = s@{
        val pawn = ctx as Pawn
        val world = pawn.world
        val other = pawn.attr[INTERACTING_NPC_ATTR]?.get() ?: pawn.attr[INTERACTING_PLAYER_ATTR]?.get()!!
        /*
         * Some interactions only require line-of-sight range, such as npcs
         * behind cells or booths. This allows for diagonal interaction.
         *
         * Set to null for default interaction range.
         */
        val lineOfSightRange = if (other is Npc) world.plugins.getNpcInteractionDistance(other.id) else null
        pawn.queue(TaskPriority.STANDARD) {
            terminateAction = {
                pawn.stopMovement()
                if (pawn is Player) {
                    pawn.setMapFlag()
                }
            }
            TODO("walk(this, pawn, other, ITEM_USE_OPCODE, lineOfSightRange) || Wip")
        }
    }

    private suspend fun walk(it: QueueTask, pawn: Pawn, other: Pawn, opt: Int, lineOfSightRange: Int?) {
        val world = pawn.world
        val initialTile = Tile(other.tile)
        val lineOfSight = lineOfSightRange ?: 1
        pawn.facePawn(other)
        if (!pawn.tile.isWithinRadius(other.tile, lineOfSight)) {
            val route = pawn.walkToInteract(other.tile)
            val result = it.awaitArrivalRanged(route, lineOfSight)
            println("From here 70")
            if (!result) {
                return
            }
        }
        pawn.stopMovement()
        if (pawn is Player) {
            if (pawn.attr[FACING_PAWN_ATTR]?.get() != other) {
                return
            }
            /*
             * If the npc has moved from the time this queue was added to
             * when it was actually invoked, we need to walk towards it again.
             */
            if (!other.tile.sameAs(initialTile)) {
                walk(it, pawn, other, opt, lineOfSightRange)
                return
            }

            if (other is Npc) {

                /*
                 * On 07, only one npc can be facing the player at a time,
                 * so if the last pawn that faced the player is still facing
                 * them, then we reset their face target.
                 */
                pawn.attr[NPC_FACING_US_ATTR]?.get()?.let {
                    if (it.attr[FACING_PAWN_ATTR]?.get() == pawn) {
                        it.resetFacePawn()
                        it.timers.remove(RESET_PAWN_FACING_TIMER)
                    }
                }
                pawn.attr[NPC_FACING_US_ATTR] = WeakReference(other)

                /*
                 * Stop the npc from walking while the player talks to it
                 * for [Npc.RESET_PAWN_FACE_DELAY] cycles.
                 */
                other.stopMovement()
                if (other.attr[FACING_PAWN_ATTR]?.get() != pawn) {
                    other.facePawn(pawn)
                    other.timers[RESET_PAWN_FACING_TIMER] = Npc.RESET_PAWN_FACE_DELAY
                }

                val npcId = other.getTransform(pawn)
                val handled = if (opt != ITEM_USE_OPCODE) {
                    world.plugins.executeNpc(pawn, npcId, opt)
                } else {
                    val item = pawn.attr[INTERACTING_ITEM]?.get() ?: return
                    world.plugins.executeItemOnNpc(pawn, npcId, item.id)
                }

                if (!handled) {
                    pawn.writeMessage(Entity.NOTHING_INTERESTING_HAPPENS)
                }
            }

            if (other is Player) {
                val option = pawn.options[opt - 1]
                if (option != null) {
                    val handled = world.plugins.executePlayerOption(pawn, option)
                    if (!handled) {
                        pawn.writeMessage(Entity.NOTHING_INTERESTING_HAPPENS)
                    }
                }
            }
            pawn.resetFacePawn()
            pawn.faceTile(other.tile)
        }
    }
}
