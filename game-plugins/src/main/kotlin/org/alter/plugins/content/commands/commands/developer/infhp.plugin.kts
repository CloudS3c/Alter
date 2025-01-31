package org.alter.plugins.content.commands.commands.developer

import org.alter.game.model.bits.*
import org.alter.game.model.priv.Privilege

onCommand("infhp", Privilege.DEV_POWER, description = "Infinite HP") {
    player.toggleStorageBit(INFINITE_VARS_STORAGE, InfiniteVarsType.HP)
    player.message(
        "Infinite hp: ${if (!player.hasStorageBit(
                INFINITE_VARS_STORAGE,
                InfiniteVarsType.HP,
            )
        ) {
            "<col=801700>disabled</col>"
        } else {
            "<col=178000>enabled</col>"
        }}",
    )
}
