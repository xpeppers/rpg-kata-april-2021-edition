package com.xpeppers.kata

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CharacterTest {
    private val attacker = Character()
    private val target = Character()

    @Test
    fun `Characters can deal damage to Characters`() {
        action { attacker.dealDamageTo(target, 100) }.decreasesBy(100) { target.health }
    }

    @Test
    fun `when damage received by a character exceeds current Health, Health becomes 0 and the character dies`() {
        attacker.dealDamageTo(target, target.health + 1)

        assertThat(target.health).isEqualTo(0)
        assertThat(target.isDead).isTrue()
    }

    @Test
    fun `a Character can heal itself`() {
        attacker.dealDamageTo(target, 100)

        action { target.heal(50) }.increasesBy(50) { target.health }
    }

    @Test
    fun `healing cannot raise health above initial health`() {
        action { target.heal(50) }.doesNotChange { target.health }
    }

    @Test
    fun `dead characters cannot heal themselves`() {
        attacker.dealDamageTo(target, target.health)

        target.heal(1)

        assertThat(target.isDead).isTrue()
    }

    @Test
    fun `a Character cannot deal damage to itself`() {
        action { attacker.dealDamageTo(attacker, 1) }.doesNotChange { attacker.health }
    }

    @Test
    fun `if the target is 5 or more levels above the attacker, damage is reduced by 50%`() {
        val attacker = Character(1)
        val target = Character(6)

        action { attacker.dealDamageTo(target, 100) }.decreasesBy(50) { target.health }
    }

    @Test
    fun `if the target is 5 or more levels below the attacker, damage is increased by 50%`() {
        val attacker = Character(6)
        val target = Character(1)

        action { attacker.dealDamageTo(target, 100) }.decreasesBy(150) { target.health }
    }

    @Test
    fun `a Character cannot deal damage to an out-of-range target`() {
        val attacker = Character(maxAttackRange = 10)

        action { attacker.dealDamageTo(target, 100, 11) }.doesNotChange { target.health }
    }

    @Test
    fun `Melee characters have an attack range of 2 meters`() {
        val attacker = Character.melee()

        action { attacker.dealDamageTo(target, 100, 3) }.doesNotChange { target.health }

        action { attacker.dealDamageTo(target, 100, 2) }.decreasesBy(100) { target.health }
    }

    @Test
    fun `Ranged characters have an attack range of 20 meters`() {
        val attacker = Character.ranged()

        action { attacker.dealDamageTo(target, 100, 21) }.doesNotChange { target.health }

        action { attacker.dealDamageTo(target, 100, 20) }.decreasesBy(100) { target.health }
    }

    private val faction = Faction()
    private val anotherFaction = Faction()

    @Test
    fun `a character cannot deal damage to a character of the same faction`() {
        attacker.joinFaction(faction)
        target.joinFaction(faction)

        action { attacker.dealDamageTo(target, 100) }.doesNotChange { target.health }
    }

    @Test
    fun `a character can deal damage to characters of other factions`() {
        attacker.joinFaction(faction)
        attacker.joinFaction(anotherFaction)

        action { attacker.dealDamageTo(target, 100) }.decreasesBy(100) { target.health }
    }

    @Test
    fun `a character cannot deal damage to a character belonging to a shared faction`() {
        attacker.joinFaction(faction)
        target.joinFaction(faction)
        target.joinFaction(anotherFaction)

        action { attacker.dealDamageTo(target, 100) }.doesNotChange { target.health }
    }

    @Test
    fun `a character can heal a character of the same faction`() {
        target.joinFaction(faction)
        val healer = Character().apply {
            joinFaction(faction)
        }

        attacker.dealDamageTo(target, 150)
        action { healer.heal(target, 100) }.increasesBy(100) { target.health }
    }

    @Test
    fun `a character can heal a character a character belonging to a shared faction`() {
        target.joinFaction(faction)
        val healer = Character().apply {
            joinFaction(faction)
            joinFaction(anotherFaction)
        }

        attacker.dealDamageTo(target, 150)
        action { healer.heal(target, 100) }.increasesBy(100) { target.health }
    }

    @Test
    fun `a character cannot heal characters of other factions`() {
        val healer = Character()
        target.joinFaction(faction)
        healer.joinFaction(anotherFaction)

        attacker.dealDamageTo(target, 150)
        action { healer.heal(target, 100) }.doesNotChange { target.health }
    }

    private val thing = Tree(2000)

    @Test
    fun `a character can deal damage to things`() {
        action { attacker.dealDamageTo(thing, 100) }.decreasesBy(100) { thing.health }
    }

    @Test
    fun `when damage received by a thing exceeds current Health, Health becomes 0 and the thing is destroyed`() {
        attacker.dealDamageTo(thing, thing.health + 1)

        assertThat(thing.health).isEqualTo(0)
        assertThat(thing.isDestroyed).isTrue()
    }
}
