package com.hermanbocharov.weatherforecast.domain.entities

object DirectionDeg {
    val NORTH = IntRange(0, 23) + IntRange(337, 360)
    val NORTHEAST = 24..68
    val EAST = 69..113
    val SOUTHEAST = 114..158
    val SOUTH = 159..203
    val SOUTHWEST = 204..248
    val WEST = 249..293
    val NORTHWEST = 294..336
}