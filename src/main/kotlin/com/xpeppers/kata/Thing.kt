package com.xpeppers.kata

import kotlin.math.min

open class Thing(health: Int) {
    var health = health
        private set

    fun receiveDamage(damage: Int) {
        health -= min(damage, health)
    }
}

class Tree(health: Int) : Thing(health)