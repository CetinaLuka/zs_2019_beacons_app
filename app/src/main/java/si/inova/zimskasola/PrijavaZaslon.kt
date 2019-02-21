package si.inova.zimskasola

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zimskasola.R
import kotlinx.android.synthetic.main.activity_prijava_zaslon.*
import java.util.jar.Manifest

class PrijavaZaslon : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prijava_zaslon)

        prijavi_se_gumb.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
            }
            else{
                val i = Intent(this, Vpis::class.java)
                startActivity(i)
            }
        }
    }
}
