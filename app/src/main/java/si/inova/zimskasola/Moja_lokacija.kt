package si.inova.zimskasola

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.zimskasola.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_moja_lokacija_fragment.*


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
class Place_action(subtitle:String, title:String, type:String, type_icon:String){
    var subtitle : String = subtitle
    var title : String = title
    var type : String = type
    var type_icon : String = type_icon


}
class Moja_lokacija : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.zimskasola.R.layout.fragment_moja_lokacija_fragment, null)
        val context = view.context
        val bundle = this.arguments
        if (bundle != null) {
            val id_prostora = bundle.getString("id_prostora", "0")
            db.document("locations/1/places/$id_prostora/").get().addOnSuccessListener {result ->
                val floor = result.get("floor").toString()
                val name = result.get("name").toString()
                val image = result.get("image").toString()
                ime_sobe.text = name
                nadstropje.text = floor

                val slika_prostora : ImageView = slika_sobe

                Glide.with(this)
                    .load(image)
                    .into(slika_sobe)
            }
            db.collection("locations/1/places/$id_prostora/description_items")
                .get()
                .addOnSuccessListener {result ->
                    var seznam = arrayListOf<Place_action>()
                    for(data in result){
                        seznam.add(Place_action(data.get("subtitle").toString(), data.get("title").toString(), data.get("type").toString(), data.get("type_icon").toString()))
                    }
                    val description_items = view.findViewById<ListView>(R.id.description_items_list)

                    //val adapter = ArrayAdapter<Place_action>(context,android.R.layout.simple_list_item_1, seznam)
                    val adapter = MyAdapter(context, seznam)
                    description_items.adapter = adapter
                }
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
        tip_action.text = current_item.type
        naslov_action.text = current_item.title
        opis_action.text = current_item.subtitle
        Glide.with(mContext)
            .load(current_item.type_icon)
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
