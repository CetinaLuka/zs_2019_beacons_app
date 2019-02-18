package si.inova.zimskasola

import android.app.*
import android.bluetooth.BluetoothManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.os.Build
import android.os.Build.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_podatki_sobe.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_moja_lokacija_fragment.*
import si.inova.zimskasola.BeaconScanner.Listener
import android.R




class Podatki_sobe : AppCompatActivity(), BeaconScanner.Listener {
    private var scanner = BeaconScanner(this, this)
    val db = FirebaseFirestore.getInstance()
    override fun onBeaconFound(data: String) {
        /*val n = Intent(this, Moja_lokacija::class.java)
        n.putExtra("id_prostora",data)
        val contentIntent = PendingIntent.getActivity(this, 0, n, 0)*/


        var mBuilder = NotificationCompat.Builder(this, "0")
            .setSmallIcon(com.example.zimskasola.R.drawable.ic_beacons_icon_small)
            .setContentTitle("Vstopili ste v sobo $data")
            .setContentText("Zaznali smo nov prostor. Poglejte kaj lahko v njem počnete")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent(contentIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)){
            notify(0, mBuilder.build())
        }
        db.document("locations/1/").get().addOnSuccessListener {result ->
            val name = result.get("name")
            val address = result.get("address")
            Log.i("Rezultat", "Name: $name, address: $address")
            location_adress.text = address.toString()
            location_name.text = name.toString()
        }


        moja_lokacija = Moja_lokacija()
        val bundle = Bundle()
        bundle.putString("id_prostora", data)
        moja_lokacija.setArguments(bundle)
        loadFragment(moja_lokacija)

    }

    override fun onBeaconLost(data: String) {
        val ns = Context.NOTIFICATION_SERVICE
        val nMgr = this.getSystemService(ns) as NotificationManager
        nMgr.cancel(0)
        supportFragmentManager
            .beginTransaction()
            .remove(moja_lokacija)
            .commit()
        moja_lokacija = Moja_lokacija()
        loadFragment(moja_lokacija)
    }

    var moja_lokacija = Moja_lokacija()
    val vsi_prostori = Vsi_prostori()
    val moznosti = Moznosti()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.zimskasola.R.layout.activity_podatki_sobe)


        val id_prostora = intent.getStringExtra("id_prostora")
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = "chanel"
            val descriptionText = "chanel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("0", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        loadFragment(moja_lokacija)
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)
    }
    override fun onStart() {
        super.onStart()
        scanner.start()
    }

    override fun onStop() {
        super.onStop()
        scanner.stop()
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(com.example.zimskasola.R.id.soba_fragment_holder, fragment)
                //.addToBackStack (null)
                .commit()
            return true
        }
        return false
    }
    val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            com.example.zimskasola.R.id.bottom_lokacija -> {
                loadFragment(moja_lokacija)
                return@OnNavigationItemSelectedListener true
            }
            com.example.zimskasola.R.id.bottom_prostori -> {
                loadFragment(vsi_prostori)
                return@OnNavigationItemSelectedListener true
            }
            com.example.zimskasola.R.id.bottom_moznosti -> {
                loadFragment(moznosti)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
