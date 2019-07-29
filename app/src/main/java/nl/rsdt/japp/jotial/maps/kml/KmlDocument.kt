package nl.rsdt.japp.jotial.maps.kml

import java.util.*


internal class KmlDocument {


    private var name: String? = null
    private val folders = LinkedList<KmlFolder>()
    private val styles = LinkedList<KmlStyle>()

    fun setName(name: String) {
        this.name = name
    }

    fun addFolder(folder: KmlFolder) {
        this.folders.add(folder)
    }

    fun addStyle(style: KmlStyle) {
        this.styles.add(style)
    }

    fun toKmlFile(): KmlFile {
        var organisatie: KmlScoutingGroep? = null
        var groepen: List<KmlScoutingGroep> = LinkedList()
        val styles = LinkedList<KmlStyleBase>()
        var deelgebieden: List<KmlDeelgebied> = LinkedList()
        for (folder in folders) {
            when (folder.type) {
                KmlFolder.Type.Organisatie -> organisatie = folder.toKmlOrganisatie()
                KmlFolder.Type.Groepen -> groepen = folder.toKmlGroepen()
                KmlFolder.Type.Deelgebieden -> deelgebieden = folder.toKmlDeelgebieden()
            }
        }
        for (style in this.styles) {
            val styleBase = style.toStyleBase()
            if (styleBase is KmlGroepStyle) {
                for (groep in groepen) {
                    if (groep.styleUrl == styleBase.id) {
                        groep.style = styleBase
                    }
                }
            }
            if (styleBase is KmlDeelgebiedStyle) {
                for (deelgebied in deelgebieden) {
                    if (deelgebied.styleId == styleBase.id) {
                        deelgebied.style = styleBase
                    }
                }
            }
        }

        return KmlFile(this.name, organisatie, groepen, styles, deelgebieden)
    }
}
