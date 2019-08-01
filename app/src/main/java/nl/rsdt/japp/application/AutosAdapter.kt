package nl.rsdt.japp.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.data.bodies.AutoPostBody
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.net.apis.AutoApi
import retrofit2.Callback
import java.util.*

class AutosAdapter(private val callback: Callback<Void>) : RecyclerView.Adapter<AutosAdapter.MyViewHolder>() {
    private var data: Map<String, List<AutoInzittendeInfo>>? = null
    private var eigenaars:Array<String> = emptyArray()

    init {
        data = HashMap()
    }

    private fun setEigenaars() {
        val eigenaarsSet = data!!.keys
        eigenaars = eigenaarsSet.toTypedArray()
        Arrays.sort(eigenaars)
    }

    fun setData(newData: Map<String, List<AutoInzittendeInfo>>) {
        this.data = newData
        setEigenaars()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AutosAdapter.MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.auto_list_layout, parent, false) as Button
        return MyViewHolder(v, callback)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(private val button: Button, private val callback: Callback<Void>) : RecyclerView.ViewHolder(button), View.OnClickListener {
        // each data item is just a string in this case
        private var eigenaar = ""
        private var inzittendeInfos: List<AutoInzittendeInfo>? = ArrayList()

        init {
            button.setOnClickListener(this)
        }

        fun refresh() {
            val text = eigenaar + ":" + inzittendeInfos!!.size
            button.text = text
        }

        fun setEigenaar(eigenaar: String) {
            this.eigenaar = eigenaar
            refresh()
        }

        fun setInzittendeInfos(inzittendeInfos: List<AutoInzittendeInfo>?) {
            this.inzittendeInfos = inzittendeInfos
            refresh()
        }

        override fun onClick(view: View) {
            val tv = view as Button
            tv.text = tv.text
            val autoApi = Japp.getApi(AutoApi::class.java)
            if (tv.text.toString().contains(Japp.appResources.getString(R.string.create_car))) {
                val body = AutoPostBody.default
                body.setAutoEigenaar(JappPreferences.accountUsername)
                autoApi.post(body).enqueue(callback)
            } else {
                val body = AutoPostBody.default
                body.setAutoEigenaar(eigenaar)
                autoApi.post(body).enqueue(callback)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            holder.setEigenaar(Japp.appResources.getString(R.string.create_car))
            holder.setInzittendeInfos(ArrayList())
            return
        }
        val i = position - 1
        holder.setEigenaar(eigenaars[i])
        holder.setInzittendeInfos(data!![eigenaars[i]])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data!!.size + 1
    }
}
