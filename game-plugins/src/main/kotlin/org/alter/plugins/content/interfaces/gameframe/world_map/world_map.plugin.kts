package org.alter.plugins.content.interfaces.worldmap

import org.alter.plugins.content.interfaces.worldmap.WorldMap.LAST_TILE
import org.alter.plugins.content.interfaces.worldmap.WorldMap.UPDATE_TIMER
import org.alter.plugins.content.interfaces.worldmap.WorldMap.WORLD_MAP_INTERFACE_ID

onButton(interfaceId = 160, component = 53) {
    if (!player.lock.canInterfaceInteract()) {
        return@onButton
    }

    if (!player.isInterfaceVisible(WORLD_MAP_INTERFACE_ID)) {
        /**
         * @TODO All options were now swapped from 2 to 1's
         */
        val opt = player.getInteractingOption()
        player.sendWorldMapTile()
        player.playSound(Sound.INTERFACE_SELECT1, 100)

        if (opt != 1) {
            player.openInterface(interfaceId = WORLD_MAP_INTERFACE_ID, dest = InterfaceDestination.WORLD_MAP, fullscreen = false)
            player.setInterfaceEvents(interfaceId = WORLD_MAP_INTERFACE_ID, component = 21, range = 0..4, setting = InterfaceEvent.ClickOp1)
        } else {
            //  160:53 -> opt 3 for FullScreen
            player.queue {
                player.animate(Animation.LOOK_AT_MINIMAP_WHEN_FULLSCREEN)
                wait(1)
                player.message("Fullscreen minimap was temporarily disabled.")
                player.animate(Animation.CLOSE_MINIMAP_FULLSCREEN)
            }
        }
        player.timers[UPDATE_TIMER] = 1
    } else {
        player.closeInterface(WORLD_MAP_INTERFACE_ID)
    }
}

/**
 * Esc key / 'x' closes.
 */
onButton(interfaceId = WORLD_MAP_INTERFACE_ID, 4, 38) {
    player.closeInterface(WORLD_MAP_INTERFACE_ID)
    player.openOverlayInterface(player.interfaces.displayMode)
    player.attr.remove(LAST_TILE)
    player.timers.remove(UPDATE_TIMER)
}

onTimer(UPDATE_TIMER) {
    if (player.isInterfaceVisible(WORLD_MAP_INTERFACE_ID)) {
        /*
         * Only send the world when the last tile recorded is not the same as
         * the current one being stood on, so we're not needlessly sending the
         * script every cycle.
         */
        val lastTile = player.attr[LAST_TILE]
        if (lastTile == null || !lastTile.sameAs(player.tile)) {
            player.sendWorldMapTile()
            player.attr[LAST_TILE] = player.tile
        }

        player.timers[UPDATE_TIMER] = 1
    }
}
