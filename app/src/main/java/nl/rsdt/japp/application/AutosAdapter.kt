package nl.rsdt.japp.application

import android.app.AlertDialog
import android.text.method.CharacterPickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.data.bodies.AutoPostBody
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied

import nl.rsdt.japp.jotial.net.apis.AutoApi
import retrofit2.Callback
import java.util.*

class AutosAdapter(private val callback: Callback<Void>) : RecyclerView.Adapter<AutosAdapter.MyViewHolder>() {
    private var data: Map<String, List<AutoInzittendeInfo>> = HashMap()
    private var eigenaars:Array<String> = emptyArray()

    private fun setEigenaars() {
        val eigenaarsSet = data.keys
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
            val body = AutoPostBody.default
            if (tv.text.toString().contains(Japp.getString(R.string.create_car))) {
                body.setAutoEigenaar(JappPreferences.accountUsername)
            } else {
                body.setAutoEigenaar(eigenaar)
            }
            val itemsRol = listOf("Bestuurder", "Navigator", "Fietser", "Bijrijder").toTypedArray()
            val rolDialog = AlertDialog.Builder(view.context)
                    .setTitle(R.string.welke_rol)
                    .setItems(itemsRol){ _ , whichRol ->
                        val rol = itemsRol[whichRol]
                        body.setRol(rol)
                        val autoApi = Japp.getApi(AutoApi::class.java)
                        autoApi.post(body).enqueue(callback)
                    }
                    .create()
            val automatisch = Japp.getString(R.string.automatisch)
            val itemsTaak = listOf(automatisch, "terug naar HB","A", "B", "C", "D", "E", "F", "X").toTypedArray()
            val taakDialog = AlertDialog.Builder(view.context)
                    .setTitle(R.string.welke_taak)
                    .setItems(itemsTaak) { _, whichTaak ->
                        var taak = itemsTaak[whichTaak]
                        JappPreferences.autoTaak = (taak == automatisch)
                        if (JappPreferences.autoTaak){
                            Japp.lastLocation?.let{
                                taak = """"${Deelgebied.resolveOnLocation(it)?.name} $automatisch"""
                            }
                        }
                        body.setTaak(taak)
                        rolDialog.show()
                    }
                    .create()
            taakDialog.show()
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            holder.setEigenaar(Japp.getString(R.string.create_car))
            holder.setInzittendeInfos(ArrayList())
            return
        }
        val i = position - 1
        holder.setEigenaar(eigenaars[i])
        holder.setInzittendeInfos(data[eigenaars[i]])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data.size + 1
    }
}
