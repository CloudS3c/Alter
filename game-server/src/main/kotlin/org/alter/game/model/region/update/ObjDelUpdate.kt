package org.alter.game.model.region.update

import net.rsprot.protocol.game.outgoing.zone.payload.ObjDel
import net.rsprot.protocol.message.ZoneProt
import org.alter.game.model.entity.GroundItem

/**
 * Represents an update where a [GroundItem] is removed.
 *
 * @author Tom <rspsmods@gmail.com>
 */
class ObjDelUpdate(
    override val type: EntityUpdateType,
    override val entity: GroundItem,
) : EntityUpdate<GroundItem>(type, entity) {
    override fun toMessage(): ZoneProt = ObjDel(entity.item, entity.amount, (entity.tile.x and 0x7), (entity.tile.y and 0x7))
}
