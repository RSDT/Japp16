package nl.rsdt.japp.application.showcase

import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import nl.rsdt.japp.R
import nl.rsdt.japp.application.activities.MainActivity

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-9-2016
 * Description...
 */
class JappShowcaseSequence(mainActivity: MainActivity) : ShowcaseSequence<MainActivity>() {

    init {
        activity = mainActivity
        populate()
    }

    protected fun populate() {
        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Navigatie Menu"

            override val contentText: String
                get() = "Vanuit hier kun je navigeren naar verschillende pagina's"

            override val target: ViewTarget?
                get() {
                    try {
                        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
                        val field = Toolbar::class.java.getDeclaredField("mNavButtonView")
                        field.isAccessible = true
                        val navigationView = field.get(toolbar) as View
                        return ViewTarget(navigationView)

                    } catch (e: Exception) {
                        Log.e("ShowcaseSequence", e.toString(), e)
                    }

                    return null
                }
        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Refresh Knop"

            override val contentText: String
                get() = "Hiermee kun je de app handmatig laten updaten, al is dit vaak niet nodig omdat de app zichzelf update"

            override val target: ViewTarget
                get() = ViewTarget(R.id.refresh, activity!!)
        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {

            override val title: String
                get() = "Actie Menu"

            override val contentText: String
                get() = "Vanuit hier kun je acties ondernemen afhankelijk van de pagina waarop je bent"

            override val target: ViewTarget
                get() {
                    val menu = activity!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                    menu.setOnMenuToggleListener { opened ->
                        if (opened) {
                            current!!.hide()
                        }
                    }
                    return ViewTarget(menu.menuIconView)
                }

            override val eventListener: OnShowcaseEventListener
                get() = object : SimpleShowcaseEventListener() {
                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
                        val menu = activity!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                        menu.open(true)
                        continueToNext()
                        menu.setOnMenuToggleListener(null)
                    }
                }

        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Volg Mij Knop"

            override val contentText: String
                get() = "Met deze knop kan je jezelf laten volgen op de kaart, je kunt zelf bepalen hoe ver en in welke hoek de camera moet staan tijdens het volgen"

            override val target: ViewTarget
                get() {
                    val follow = activity!!.findViewById<FloatingActionButton>(R.id.fab_follow)
                    return ViewTarget(follow)
                }
        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Mark Knop"

            override val contentText: String
                get() = "Met deze knop kan je voor jezelf iets markeren op de kaart"

            override val target: ViewTarget
                get() {
                    val mark = activity!!.findViewById<FloatingActionButton>(R.id.fab_mark)
                    return ViewTarget(mark)
                }
        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Spot Knop"

            override val contentText: String
                get() = "Met deze knop kun je een vos spotten, je selecteert een locatie op de kaart en voegd eventueel wat informatie toe"

            override val target: ViewTarget
                get() {
                    val spot = activity!!.findViewById<FloatingActionButton>(R.id.fab_spot)
                    return ViewTarget(spot)
                }
        })

        members!!.add(object : ShowcaseSequence<MainActivity>.ShowcaseSequenceItem() {
            override val title: String
                get() = "Hunt Knop"

            override val contentText: String
                get() = "Met deze knop kun je een vos hunten, je selecteert een locatie op de kaart en voegd eventueel wat informatie toe"

            override val target: ViewTarget
                get() {
                    val hunt = activity!!.findViewById<FloatingActionButton>(R.id.fab_hunt)
                    return ViewTarget(hunt)
                }
        })


    }
}
