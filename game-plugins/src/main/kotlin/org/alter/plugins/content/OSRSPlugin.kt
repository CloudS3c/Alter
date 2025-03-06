package org.alter.plugins.content

import org.alter.api.*
import org.alter.api.CommonClientScripts
import org.alter.api.InterfaceDestination
import org.alter.api.cfg.*
import org.alter.api.ext.*
import org.alter.game.*
import org.alter.game.model.*
import org.alter.game.model.entity.*
import org.alter.game.plugin.*
import org.alter.game.type.interfacedsl.InterfaceFlag

class OSRSPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        /**
         * Closing main modal for players.
         */
        setModalCloseLogic {
            val modal = player.interfaces.getModal()
            if (modal != -1) {
                player.closeInterface(modal)
                player.interfaces.setModal(-1)
            }
        }
        /**
         * Check if the player has a menu opened.
         */
        setMenuOpenCheck {
            player.getInterfaceAt(dest = InterfaceDestination.MAIN_SCREEN) != -1
        }
        onLogin {
            with(player) {
                // Skill-related logic.
                calculateAndSetCombatLevel()
                if (getSkills().getBaseLevel(Skills.HITPOINTS) < 10) {
                    getSkills().setBaseLevel(Skills.HITPOINTS, 10)
                }
                calculateAndSetCombatLevel()
                sendWeaponComponentInformation()
                sendCombatLevelText()
                setInterfaceEvents(
                    interfaceId = 149,
                    component = 0,
                    range = 0..27,
                    setting =
                        arrayOf(
                            InterfaceFlag.ClickOp2,
                            InterfaceFlag.ClickOp3,
                            InterfaceFlag.ClickOp4,
                            InterfaceFlag.ClickOp6,
                            InterfaceFlag.ClickOp7,
                            InterfaceFlag.ClickOp10,
                            InterfaceFlag.UseOnGroundItem,
                            InterfaceFlag.UseOnNpc,
                            InterfaceFlag.UseOnObject,
                            InterfaceFlag.UseOnPlayer,
                            InterfaceFlag.UseOnInventory,
                            InterfaceFlag.UseOnComponent,
                            InterfaceFlag.DRAG_DEPTH1,
                            InterfaceFlag.DragTargetable,
                            InterfaceFlag.ComponentTargetable,
                        ),
                )
                player.openDefaultInterfaces()
                setVarbit(Varbit.COMBAT_LEVEL_VARBIT, combatLevel)
                setVarbit(Varbit.CHATBOX_UNLOCKED, 1)
                runClientScript(CommonClientScripts.INTRO_MUSIC_RESTORE)
                if (getVarp(Varp.PLAYER_HAS_DISPLAY_NAME) == 0 && displayName.isNotBlank()) {
                    syncVarp(Varp.PLAYER_HAS_DISPLAY_NAME)
                }
                syncVarp(Varp.NPC_ATTACK_PRIORITY_VARP)
                syncVarp(Varp.PLAYER_ATTACK_PRIORITY_VARP)
                sendOption("Follow", 3)
                sendOption("Trade with", 4)
                sendOption("Report", 5)
                sendRunEnergy(player.runEnergy.toInt())
                message("Welcome to ${world.gameContext.name}.", ChatMessageType.GAME_MESSAGE)
                setVarbit(Varbit.ESC_CLOSES_CURRENT_INTERFACE, 1)
                /**
                 * @TODO
                 * As for now these varbit's disable Black bar on right side for Native client,
                 * The black bar is for loot tracker n whatnot
                 */
                setVarbit(13982, 1)
                setVarbit(13981, 1)
            }
        }



        // TODO Whats this for:?
        onButton(245, 20) {
            player.openInterface(interfaceId = 626, dest = InterfaceDestination.MAIN_SCREEN)
        }
    }

    fun Player.openDefaultInterfaces() {
        openOverlayInterface(interfaces.displayMode)
        openModals(this)
        setInterfaceEvents(interfaceId = 239, component = 3, range = 0..665, setting = 6) // enable music buttons
        initInterfaces(interfaces.displayMode)
    }

    fun openModals(
        player: Player,
        fullscreen: Boolean = false,
    ) {
        InterfaceDestination.getModals().forEach { pane ->
            if (pane == InterfaceDestination.XP_COUNTER && player.getVarbit(Varbit.XP_DROPS_VISIBLE_VARBIT) == 0) {
                return@forEach
            } else if (pane == InterfaceDestination.MINI_MAP && player.getVarbit(Varbit.HIDE_DATA_ORBS_VARBIT) == 1) {
                return@forEach
            }
            player.openInterface(pane.interfaceId, pane, fullscreen)
        }
    }

}
