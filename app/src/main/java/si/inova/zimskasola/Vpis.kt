package si.inova.zimskasola

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zimskasola.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_vpis.*

class Vpis : AppCompatActivity() {
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vpis)
        if(firebaseAuth.currentUser != null){
            openMainScreen()
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password= passwordEditText.text.toString()

            if(email.isBlank() or password.isBlank()){
                return@setOnClickListener
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if(result.isSuccessful){
                        openMainScreen()
                    }
                    else{
                        Toast.makeText(this, "Napaka ${result.exception}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password= passwordEditText.text.toString()

            if(email.isBlank() or password.isBlank()){
                return@setOnClickListener
            }
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { result ->
                    if(result.isSuccessful){
                        openMainScreen()
                    }
                    else{
                        Toast.makeText(this, "Napaka ${result.exception}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun openMainScreen() {
        startActivity(Intent(this, Podatki_sobe::class.java))
    }
}
