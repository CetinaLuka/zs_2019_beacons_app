package si.inova.zimskasola

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.Nullable
import com.example.zimskasola.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_moznosti.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Moznosti.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Moznosti.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Moznosti : Fragment() {
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.zimskasola.R.layout.fragment_moznosti, null)
        view.findViewById<Button>(R.id.odjava).setOnClickListener {
            firebaseAuth.signOut()
            val i = Intent(view.context, PrijavaZaslon::class.java)
            startActivity(i)
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        val preberiPref = sharedPreferences.getLong("gibanjeCas", 5500).toInt()
        val position : Int
        when(preberiPref){
            30000 -> position = 0
            900000  -> position = 1
            3600000 -> position = 2
            7200000 -> position = 3
            18000000 -> position = 4
            else -> position = 3
        }
        view.findViewById<Spinner>(R.id.gibanje_spinner).setSelection(position)
        view.findViewById<Spinner>(R.id.gibanje_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = gibanje_spinner.selectedItem.toString()
                var gibanjeCas : Long
                when(selectedItem){
                    "30 sekund" -> gibanjeCas = 30000
                    "15 minut" -> gibanjeCas = 900000
                    "1 ura" -> gibanjeCas = 3600000
                    "2 ure" -> gibanjeCas = 7200000
                    "5 ur" -> gibanjeCas = 18000000
                    else -> gibanjeCas = 7200000
                }
                sharedPreferences.edit().putLong("gibanjeCas", gibanjeCas).apply()
            }

        }
        return view
    }
}
