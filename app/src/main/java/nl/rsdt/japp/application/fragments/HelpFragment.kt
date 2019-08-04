package nl.rsdt.japp.application.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat

import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
class HelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val r = inflater.inflate(R.layout.fragment_help, container, false)
        val helpTextView: TextView? = r.findViewById(R.id.help_tv)
        helpTextView?.text = HtmlCompat.fromHtml(Japp.getString(R.string.help_text), 0)
        return r
    }

    companion object {

        val TAG = "HelpFragment"
    }
}
