package com.example.univefinal;

class LicensePlate {
    var voertuigsoort: String? = null
    var merk: String? = null
    var handelsbenaming: String? = null
    var brandstof: String? = null
    var vervaldatum_apk: String? = null
    var eerste_kleur: String? = null
    var inrichting: String? = null
    var massa_ledig_voertuig: String? = null
    var cilinderinhoud: String? = null


    constructor() : super() {}

    constructor(voertuigsoort : String, merk : String, handelsBenaming : String, brandstof : String, vervaldatum_apk : String, eerste_kleur : String, inrichting : String, massa_ledig_voertuig : String, cilinderinhoud : String) : super() {
        this.voertuigsoort = voertuigsoort
        this.merk = merk
        this.handelsbenaming = handelsBenaming
        this.brandstof = brandstof
        this.vervaldatum_apk = vervaldatum_apk
        this.eerste_kleur = eerste_kleur
        this.inrichting = inrichting
        this.massa_ledig_voertuig = massa_ledig_voertuig
        this.cilinderinhoud = cilinderinhoud
    }

}
