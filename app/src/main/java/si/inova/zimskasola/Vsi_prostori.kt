package si.inova.zimskasola

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.zimskasola.R
import kotlinx.android.synthetic.main.activity_podatki_sobe.*
import kotlinx.android.synthetic.main.fragment_vsi_prostori.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Vsi_prostori.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Vsi_prostori.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Vsi_prostori : Fragment() {
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.zimskasola.R.layout.fragment_vsi_prostori, null)

        try {
            val url = "https://firebasestorage.googleapis.com/v0/b/zs-beacons-2019.appspot.com/o/25022c4a-3035-11e9-bb6a-a5c92278bce1.json?alt=media&token=4607d4e9-c453-4ce8-9a9a-3b3d76ce244b"
            val queue = Volley.newRequestQueue(view.context)
            val stringReq = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    var strResp = URLDecoder.decode(URLEncoder.encode(response.toString(), "iso8859-1"),"UTF-8")
                    val jsonObj: JSONObject = JSONObject(strResp)
                    val floors: JSONArray = jsonObj.getJSONArray("floors")
                    var sobe = arrayListOf<String>()
                    for (i in 0 until floors.length()) {
                        var nadstropje: JSONObject = floors.getJSONObject(i)
                        sobe.add("-${nadstropje.get("name")}")
                        val rooms: JSONArray = nadstropje.getJSONArray("rooms")
                        for (j in 0 until rooms.length()) {
                            var soba: JSONObject = rooms.getJSONObject(j)
                            val soba_name = soba.get("name").toString()
                            sobe.add(soba_name)

                        }

                    }
                    view.findViewById<ProgressBar>(R.id.progress_vsi_prostori).visibility = View.GONE
                    val adapter = Vsi_prostori_adapter(view.context, sobe)
                    view.findViewById<ListView>(R.id.seznam_vseh_prostorov).adapter = adapter
                },
                Response.ErrorListener {

                })
            queue.add(stringReq)
        }
        catch (ex: Exception){

        }

        return view
    }
}
class Vsi_prostori_adapter(context: Context, seznam:ArrayList<String>) : BaseAdapter(){
    private val mContext: Context
    private val seznam_sob = seznam
    init {
        mContext=context

    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val row = layoutInflater.inflate(R.layout.vse_sobe_row, parent, false)
        val separator = layoutInflater.inflate(R.layout.vse_sobe_separator, parent, false)
        val current_item = seznam_sob.get(position)
        if(current_item.get(0)=='-'){
            separator.findViewById<TextView>(R.id.nadstropje_seznam).text = current_item.substring(1)
            return separator
        }
        else{
            row.findViewById<TextView>(R.id.soba_seznam).text = current_item
            return row
        }
    }

    override fun getItem(position: Int): Any {
        return seznam_sob.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return seznam_sob.size
    }

}
