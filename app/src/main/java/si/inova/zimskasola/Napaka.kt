package si.inova.zimskasola

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.example.zimskasola.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Napaka.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Napaka.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Napaka : Fragment() {
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.zimskasola.R.layout.fragment_napaka, null)
        return view
    }
}
