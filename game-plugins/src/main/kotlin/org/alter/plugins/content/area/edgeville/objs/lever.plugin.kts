package org.alter.plugins.content.area.edgeville.objs

import org.alter.game.model.move.moveTo

on_obj_option(Objs.LEVER_26761, "pull") {
    val obj = player.getInteractingGameObj()

    player.queue {
        player.lock()
        wait(1)
        player.animate(2140)
        player.message("You pull the lever...")
        wait(1)

        world.queue {
            val pulled = DynamicObject(obj, 88)
            world.remove(obj)
            world.spawn(pulled)

            world.remove(pulled)
            world.spawn(DynamicObject(obj))
        }

        wait(2)

        player.animate(714)
        player.graphic(111, 110)
        wait(4)

        player.animate(-1)
        player.moveTo(3154, 3924)
        player.message("... and teleport into the Wilderness.")
        player.unlock()
    }
}
