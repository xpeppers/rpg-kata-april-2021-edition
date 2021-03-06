package com.xpeppers.kata

import kotlin.math.min

class Character(private val level: Int = 1, private val maxAttackRange: Int = 0) {
    val isDead
        get() = health == 0

    var health = INITIAL_HEALTH
        private set

    private var factions = mutableSetOf<Faction>()

    fun dealDamageTo(target: Thing, damage: Int) {
        target.receiveDamage(damage)
    }

    fun dealDamageTo(target: Character, damage: Int, distanceFromTarget: Int = 0) {
        if (target == this || isAllyOf(target) || !isTargetInRange(distanceFromTarget)) {
            return
        }

        val actualDamage = computeActualDamage(damage, target)
        target.receiveDamage(actualDamage)
    }

    private fun isAllyOf(target: Character) = factions.intersect(target.factions).isNotEmpty()

    private fun isTargetInRange(distanceFromTarget: Int) = distanceFromTarget <= maxAttackRange

    fun heal(healingAmount: Int) {
        if (!isDead)
            restoreHealth(healingAmount)
    }

    fun heal(target: Character, healingAmount: Int) {
        if (isAllyOf(target))
            target.heal(healingAmount)
    }

    private fun computeActualDamage(damage: Int, target: Character): Int {
        return when {
            target.level - level >= 5 -> damage / 2
            level - target.level >= 5 -> damage + damage / 2
            else -> damage
        }
    }

    private fun receiveDamage(damage: Int) {
        health -= min(damage, health)
    }

    private fun restoreHealth(healingAmount: Int) {
        health = min(health + healingAmount, INITIAL_HEALTH)
    }

    fun joinFaction(faction: Faction) {
        factions.add(faction)
    }

    companion object {
        fun melee(level: Int = 1): Character = Character(level, maxAttackRange = 2)
        fun ranged(level: Int = 1): Character = Character(level, maxAttackRange = 20)

        private const val INITIAL_HEALTH = 1000
    }
}