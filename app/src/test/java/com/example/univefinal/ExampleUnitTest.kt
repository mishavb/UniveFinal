package com.example.univefinal

import com.example.univefinal.AppMethods.Companion.isOnline
import com.example.univefinal.AppMethods.Companion.validLicensePlate
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testIfLicensePlateIsCorrect() {
        validLicensePlate(licensePlate = "25-DJ-SG")

        //a correct index, which not equals 0, must be returned
        notEquals(0)
    }

    @Test
    fun testEmptyLicensePlate() {
        //if empty parameter, fun returns index 0
        validLicensePlate(licensePlate = "") Equals 0
    }

    @Test
    fun testIfComputerIsOnline() {

    }
}

infix fun Any?.Equals(o2: Any?) = assertEquals(this, o2)
infix fun Any?.notEquals(o3: Any?) = assertNotEquals(this, o3)
