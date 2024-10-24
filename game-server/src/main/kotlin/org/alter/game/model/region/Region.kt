package org.alter.game.model.region

import org.alter.game.model.Tile

class Region(val region: Int) {
    /**
     * bottom-left Tile in region
     */
    val baseCoord = Tile((region shr 8) shl 6, (region and 0xFF) shl 6, height = 0)

    val tiles =
        Array<Tile>(64 * 64) {
            Tile(baseCoord.x + it % 64, baseCoord.y + it / 64, baseCoord.height)
        }
}
