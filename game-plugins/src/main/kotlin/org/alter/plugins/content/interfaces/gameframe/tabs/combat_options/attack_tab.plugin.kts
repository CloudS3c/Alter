package org.alter.plugins.content.interfaces.attack

import org.alter.game.model.attr.NEW_ACCOUNT_ATTR
import org.alter.plugins.content.combat.specialattack.SpecialAttacks
import org.alter.plugins.content.interfaces.attack.AttackTab.ATTACK_STYLE_VARP
import org.alter.plugins.content.interfaces.attack.AttackTab.ATTACK_TAB_INTERFACE_ID
import org.alter.plugins.content.interfaces.attack.AttackTab.DISABLE_AUTO_RETALIATE_VARP
import org.alter.plugins.content.interfaces.attack.AttackTab.SPECIAL_ATTACK_VARP
import org.alter.plugins.content.interfaces.attack.AttackTab.setEnergy

/**
 * First log-in logic (when accounts have just been made).
 */
onLogin {
    if (player.attr.getOrDefault(NEW_ACCOUNT_ATTR, false)) {
        setEnergy(player, 100)
    }
    AttackTab.resetRestorationTimer(player)
}

onTimer(AttackTab.SPEC_RESTORE) {
    AttackTab.restoreEnergy(player)
    AttackTab.resetRestorationTimer(player)
}

/**
 * Attack style buttons
 */
onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 5) {
    player.setVarp(ATTACK_STYLE_VARP, 0)
}

onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 9) {
    player.setVarp(ATTACK_STYLE_VARP, 1)
}

onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 13) {
    player.setVarp(ATTACK_STYLE_VARP, 2)
}

onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 17) {
    player.setVarp(ATTACK_STYLE_VARP, 3)
}

/**
 * Toggle auto-retaliate button.
 */
onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 31) {
    player.toggleVarp(DISABLE_AUTO_RETALIATE_VARP)
}

onButton(interfaceId = 160, component = 35) {
    val weaponId = player.equipment[EquipmentType.WEAPON.id]!!.id
    if (SpecialAttacks.executeOnEnable(weaponId)) {
        if (!SpecialAttacks.execute(player, null, world)) {
            player.message("You don't have enough power left.")
        }
    } else {
        player.toggleVarp(SPECIAL_ATTACK_VARP)
    }
}

/**
 * Toggle special attack.
 */
onButton(interfaceId = ATTACK_TAB_INTERFACE_ID, component = 36) {
    val weaponId = player.equipment[EquipmentType.WEAPON.id]!!.id
    if (SpecialAttacks.executeOnEnable(weaponId)) {
        if (!SpecialAttacks.execute(player, null, world)) {
            player.message("You don't have enough power left.")
        }
    } else {
        player.toggleVarp(SPECIAL_ATTACK_VARP)
    }
}

/**
 * Disable special attack when switching weapons.
 */
onEquipToSlot(EquipmentType.WEAPON.id) {
    player.setVarp(SPECIAL_ATTACK_VARP, 0)
}

/**
 * Disable special attack on log-out.
 */
onLogout {
    player.setVarp(SPECIAL_ATTACK_VARP, 0)
}
