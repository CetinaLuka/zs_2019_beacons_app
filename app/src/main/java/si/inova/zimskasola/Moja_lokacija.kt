package si.inova.zimskasola

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.example.zimskasola.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Moja_lokacija_fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Moja_lokacija_fragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Place_action(stuff_id:String, subtitle:String, title:String, type:String, type_icon:String){
    var action_id : String = stuff_id
    var description : String = subtitle
    var name : String = title
    var category : String = type
    var icon : String = type_icon
}
class Place(title:String, description:String, floors:Array<Floor>){
    var title:String = title
    var description:String = description
    var floors:Array<Floor> = floors
}
class Floor(floor_id:String, name:String, rooms:Array<Room>){
    var floor_id:String = floor_id
    var name:String = name
    var rooms:Array<Room> = rooms
}
class Room(room_id:String, beacon_id:String, name:String, image:String, stuff:Array<Place_action>){
    var room_id:String = room_id
    var beacon_id:String = beacon_id
    var name:String = name
    var image:String = image
    var stuff:Array<Place_action> = stuff
}
class Moja_lokacija : Fragment() {
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.zimskasola.R.layout.fragment_moja_lokacija_fragment, null)
        val context = view.context
        val bundle = this.arguments
        if (bundle != null) {
            val floor = bundle.get("floor").toString()
            val room = bundle.get("room").toString()
            val strResp = bundle.get("json").toString()
            val jsonObj: JSONObject = JSONObject(strResp)

            val address = jsonObj.get("description").toString()
            val floors: JSONArray = jsonObj.getJSONArray("floors")
            var stuffArray = arrayListOf<Place_action>()
            for (i in 0 until floors.length()) {
                var nadstropje_obj: JSONObject = floors.getJSONObject(i)
                if(nadstropje_obj.get("floor_id").toString().trim() == floor){
                    view.findViewById<TextView>(R.id.nahajas_se).text = "NAHAJAŠ SE V"
                    view.findViewById<TextView>(R.id.nadstropje_seznam).text = nadstropje_obj.get("name").toString()
                    val rooms: JSONArray = nadstropje_obj.getJSONArray("rooms")
                    for (j in 0 until rooms.length()) {
                        var soba: JSONObject = rooms.getJSONObject(j)
                        if(soba.get("room_id").toString().trim() == room){
                            view.findViewById<TextView>(R.id.ime_sobe).text = soba.get("name").toString()
                            Glide.with(view.context)
                                .load(soba.get("image"))
                                .into(view.findViewById(R.id.slika_sobe))
                            var stuff: JSONArray = soba.getJSONArray("stuff")
                            for(k in 0 until stuff.length()){
                                val action = stuff.getJSONObject(k)
                                val place_action = Place_action(
                                    action.get("stuff_id").toString(),
                                    action.get("name").toString(),
                                    action.get("category").toString(),
                                    action.get("description").toString(),
                                    action.get("icon").toString())
                                stuffArray.add(place_action)
                            }
                        }
                    }
                }
            }
            view.findViewById<ProgressBar>(R.id.progress_moja_lokacija).visibility = View.GONE
            view.findViewById<TextView>(R.id.iscem_sobo).visibility = View.GONE
            view.findViewById<TextView>(R.id.vklopi_bluetooth).visibility = View.GONE
            view.findViewById<ListView>(R.id.description_items_list).adapter = MyAdapter(view.context, stuffArray)

        }
        return view
    }
}
class MyAdapter(context: Context, seznam:ArrayList<Place_action>) : BaseAdapter(){
    private val mContext: Context
    private val seznam_akcij = seznam
    init {
        mContext=context

    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val row_layout = layoutInflater.inflate(R.layout.row_layout, parent, false)
        val tip_action = row_layout.findViewById<TextView>(R.id.tip_action)
        val naslov_action = row_layout.findViewById<TextView>(R.id.naslov_action)
        val opis_action = row_layout.findViewById<TextView>(R.id.opis_action)
        val slika_action= row_layout.findViewById<ImageView>(R.id.slika_action)
        val current_item = seznam_akcij.get(position)
        tip_action.text = current_item.category
        naslov_action.text = current_item.name
        opis_action.text = current_item.description
        Glide.with(mContext)
            .load(current_item.icon)
            .into(slika_action)

        return row_layout
    }

    override fun getItem(position: Int): Any {
        return seznam_akcij.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return seznam_akcij.size
    }

}
