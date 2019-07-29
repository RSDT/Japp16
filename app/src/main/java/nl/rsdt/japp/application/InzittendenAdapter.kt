package nl.rsdt.japp.application

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import java.util.*


class InzittendenAdapter : RecyclerView.Adapter<InzittendenAdapter.MyViewHolder>() {

    private var data: List<AutoInzittendeInfo>? = null

    init {
        data = ArrayList()
    }

    fun setData(newData: List<AutoInzittendeInfo>) {
        this.data = newData
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(private val v: TextView) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        private var inzittendeInfo: AutoInzittendeInfo? = null

        fun refresh() {
            val text: String
            if (inzittendeInfo != null) {
                text = inzittendeInfo!!.gebruikersNaam + " " + inzittendeInfo!!.rol
            } else {
                text = Japp.appResources.getString(R.string.unkown)
            }
            v.text = text
        }


        fun setInzittendeInfo(inzittendeInfo: AutoInzittendeInfo) {
            this.inzittendeInfo = inzittendeInfo
            refresh()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): InzittendenAdapter.MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.auto_inzittenden_list_layout, parent, false) as TextView
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setInzittendeInfo(data!![position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data!!.size
    }
}
