package nl.rsdt.japp.application.showcase


import android.app.Activity
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener
import com.github.amlcurran.showcaseview.targets.ViewTarget
import nl.rsdt.japp.R
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
open class ShowcaseSequence<A : Activity> {

    protected var count = 0

    protected var current: ShowcaseView? = null

    protected var members: ArrayList<ShowcaseSequenceItem>? = ArrayList()

    protected var activity: A? = null

    protected var callback: OnSequenceCompletedCallback<A>? = null

    fun setActivity(activity: A) {
        this.activity = activity
    }

    fun setCallback(callback: OnSequenceCompletedCallback<A>) {
        this.callback = callback
    }

    fun start() {
        continueToNext()
    }

    protected fun continueToNext() {
        if (current != null) {
            /**
             * Set the EventListener to null, so that it doesn't re-invokes the onShowcaseViewDidHide() method.
             */
            current!!.setOnShowcaseEventListener(null)
            current!!.hide()
            current = null
        }

        if (members != null) {
            if (members!!.size > count) {
                val member = members!![count]
                val builder = ShowcaseView.Builder(activity!!)
                current = builder.setTarget(member.target)
                        .setStyle(R.style.ShowCaseTheme)
                        .withMaterialShowcase()
                        .setContentTitle(member.title)
                        .setContentText(member.contentText)
                        .setShowcaseEventListener(member.eventListener)
                        .build()
                current!!.show()
                count++
            } else {
                if (callback != null) {
                    callback!!.onSequenceCompleted(this)
                }
                onDestroy()
            }
        }
    }

    fun end() {
        onDestroy()
    }

    private fun onDestroy() {
        if (current != null) {
            current!!.setOnShowcaseEventListener(null)
            current!!.hide()
            current = null
        }

        if (members != null) {
            members!!.clear()
            members = null
        }

        if (activity != null) {
            activity = null
        }

        if (callback != null) {
            callback = null
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-8-2016
     * Description...
     */
    abstract inner class ShowcaseSequenceItem {

        abstract val title: String

        abstract val contentText: String

        abstract val target: ViewTarget

        val eventListener: OnShowcaseEventListener
            get() = object : SimpleShowcaseEventListener() {
                override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
                    continueToNext()
                }
            }

    }


    interface OnSequenceCompletedCallback<A : Activity> {
        fun onSequenceCompleted(sequence: ShowcaseSequence<A>)
    }

}
