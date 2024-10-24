package org.alter.game.model.region.update

import net.rsprot.protocol.game.outgoing.zone.payload.SoundArea
import net.rsprot.protocol.message.ZoneProt
import org.alter.game.model.entity.AreaSound

/**
 * Represents an update where a [AreaSound] is spawned.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class SoundAreaUpdate(
    override val type: EntityUpdateType,
    override val entity: AreaSound,
) : EntityUpdate<AreaSound>(type, entity) {
    override fun toMessage(): ZoneProt =
        SoundArea(entity.id, entity.delay, entity.volume, entity.radius, 1, (entity.tile.x and 0x7), (entity.tile.y and 0x7))
}
