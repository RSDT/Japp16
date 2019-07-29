package nl.rsdt.japp.jotial.maps.kml

import java.util.*

class KmlFolder {
    var type: Type? = null
    private val placeMarkList = ArrayList<KmlPlaceMark>()

    fun toKmlOrganisatie(): KmlScoutingGroep {
        if (type != Type.Organisatie) throw AssertionError()
        if (placeMarkList.size != 1) throw AssertionError()
        return placeMarkList[0].toKmlScoutingGroep()
    }

    fun toKmlGroepen(): List<KmlScoutingGroep> {
        if (type != Type.Groepen) throw AssertionError()
        val groepen = LinkedList<KmlScoutingGroep>()
        for (placeMark in placeMarkList) {
            groepen.add(placeMark.toKmlScoutingGroep())
        }
        return groepen
    }

    fun toKmlDeelgebieden(): List<KmlDeelgebied> {
        if (type != Type.Deelgebieden) throw AssertionError()
        val deelgebieden = LinkedList<KmlDeelgebied>()
        for (placeMark in placeMarkList) {
            deelgebieden.add(placeMark.toKmlDeelgebied())
        }
        return deelgebieden
    }


    enum class Type {
        Organisatie, Groepen, Deelgebieden, Unkown;


        companion object {

            fun parse(s: String): Type {
                when (s.toLowerCase()) {
                    "organisatie" -> return Organisatie
                    "groepen" -> return Groepen
                    "deelgebieden" -> return Deelgebieden
                    else -> return Unkown
                }

            }
        }
    }

    fun addPlacemark(placeMark: KmlPlaceMark): Boolean {
        return placeMarkList.add(placeMark)
    }
}
