package com.example.mandiexe.utils

object Constants {

    val defaultDaysAndDistance = 50

    val maximumDaysRange = 180
    val stepDays = 10               //18

    val maximumDistanceRange = 1000
    val stepDistance = 50          //50

    fun getDaysSpinnerList(): MutableList<Int> {

        val daysList = mutableListOf<Int>()
        val mSize = maximumDaysRange / stepDays

        for (i in 0 until mSize) {
            daysList.add(10 * (i + 1))
        }

        return daysList
    }

    fun getDistanceSpinnerList(): MutableList<Int> {

        val distanceList = mutableListOf<Int>()
        val mSize = maximumDistanceRange / stepDistance

        for (i in 0 until mSize) {
            distanceList.add(50 * (i + 1))
        }

        return distanceList
    }


}

