package com.example.univefinal

import android.util.Log
import com.example.univefinal.AppMethods.Companion.getJsonFromURL
import com.example.univefinal.AppMethods.Companion.capitalizeWords
import com.example.univefinal.AppMethods.Companion.parseVehicleCosts
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
    fun testEmptyJSONUrl() {
        getJsonFromURL("") Equals ""
    }

    @Test
    fun testRDWEndpoint() {
        //check if we are receiving data from RDW API
        var endpoint = "https://opendata.rdw.nl/api/id/m9d7-ebf2.json?\$query=select%20%2A%20search%20%27$25djsg%27%20limit%20100&\$\$query_timeout_seconds=3"
        getJsonFromURL(endpoint) notEquals ""
    }

    @Test
    fun testAutoDiskEndpoint() {
        //check if we are receiving data from AutoDisk API
        var endpoint = "https://scr.autodisk.nl/wsTCO_Client/wsTCO.asmx/wsCalculeerTCO?nDebiteurNummer=4533429&strGebruikersnaam=Unive.NL&strWachtwoord=yhnK@uQ=53XWG23V%2597rbkg&strKenteken=25djsg&nKilometerStand=25000&nInzetLooptijd=24&nInzetKmPerJaar=10000&sngRentePercentage=3&nAantalMaandenInBezit=11"
        getJsonFromURL(endpoint) notEquals ""
    }

    @Test
    fun testParsingVehicleCosts() {
        //testing if we're not getting an empty ArrayList
        var endpoint = "https://scr.autodisk.nl/wsTCO_Client/wsTCO.asmx/wsCalculeerTCO?nDebiteurNummer=4533429&strGebruikersnaam=Unive.NL&strWachtwoord=yhnK@uQ=53XWG23V%2597rbkg&strKenteken=25djsg&nKilometerStand=25000&nInzetLooptijd=24&nInzetKmPerJaar=10000&sngRentePercentage=3&nAantalMaandenInBezit=11"
        var xml = getJsonFromURL(endpoint)
        parseVehicleCosts(xml).size notEquals 0
    }

    @Test
    fun testCapitalizeWords() {
        //checking if fun capitalizes words
        "dit is een teststring".capitalizeWords() Equals "Dit Is Een Teststring"
    }
}

infix fun Any?.Equals(o2: Any?) = assertEquals(this, o2)
infix fun Any?.notEquals(o3: Any?) = assertNotEquals(this, o3)
