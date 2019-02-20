package si.inova.zimskasola

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import com.example.zimskasola.R
import com.google.firebase.auth.FirebaseAuth


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
        return view
    }
}
