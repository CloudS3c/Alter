package org.alter.game.message.handler

import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import org.alter.game.message.MessageHandler
import org.alter.game.model.attr.CLIENT_KEY_COMBINATION
import org.alter.game.model.move.MovementQueue
import org.alter.game.model.entity.Client
import org.alter.game.model.move.walkToInteract

class ClickMapHandler : MessageHandler<MoveGameClick> {
    override fun accept(
        client: Client,
        message: MoveGameClick
    ) {
        log(client, "Click map: x=%d, y=%d, type=%d", message.x, message.z, message.keyCombination)
        client.attr[CLIENT_KEY_COMBINATION] = message.keyCombination
        client.walkToInteract(message.x, message.z, stepType = MovementQueue.StepType.NORMAL)
    }
}
