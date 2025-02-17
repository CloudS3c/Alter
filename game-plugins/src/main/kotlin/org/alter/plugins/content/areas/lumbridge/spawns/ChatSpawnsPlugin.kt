package org.alter.plugins.content.areas.lumbridge.spawns

import org.alter.game.Server
import org.alter.game.model.Direction
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository


class ChatSpawnsPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        // Outside
        spawnNpc("npc.donie", 3228, 3222, 0, 10, Direction.SOUTH)
        spawnNpc("npc.father_aereck", 3243, 3206, 0, 3)
        spawnNpc("npc.hatius_cosaintus", 3233, 3215, 0, 1)
        spawnNpc("npc.ironman_tutor", 3229, 3228, 0, 1)
        spawnNpc("npc.lumbridge_guide", 3238, 3220, 0, 0, Direction.WEST)

        // Castle - outside
        spawnNpc("npc.hans", 3221, 3219, 0, 0, Direction.EAST)
        spawnNpc("npc.perdu", 3230, 3215, 0, 10, Direction.SOUTH)

        // Castle - inside
        spawnNpc("npc.banker_2897", 3209, 3222, 2, 0, Direction.SOUTH)
        spawnNpc("npc.duke_horacio", 3212, 3220, 1, 4, Direction.SOUTH)

        // Stores
        spawnNpc("npc.bob_10619", 3230, 3203, 0, 2, Direction.EAST)
        spawnNpc("npc.shop_keeper", 3211, 3246, 0, 3, Direction.EAST)
        spawnNpc("npc.shop_assistant", 3211, 3247, 0, 3, Direction.EAST)

        // Tutors
        spawnNpc("npc.melee_combat_tutor", 3220, 3238, 0, 1)
        spawnNpc("npc.ranged_combat_tutor", 3218, 3238, 0, 1)
        spawnNpc("npc.magic_combat_tutor", 3216, 3238, 0, 1)
        spawnNpc("npc.woodsman_tutor", 3228, 3246, 0, 1)
        spawnNpc("npc.smithing_apprentice", 3228, 3254, 0, 1)
        spawnNpc("npc.cooking_tutor", 3234, 3196, 0, 1)
        spawnNpc("npc.prayer_tutor", 3243, 3211, 0, 3)

        spawnNpc("npc.count_check", 3238, 3199, 0, 0, Direction.EAST)
        spawnNpc("npc.nigel_8391", 3243, 3201, 0, 3, Direction.WEST)
    }
}
