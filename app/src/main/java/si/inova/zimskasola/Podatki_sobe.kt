package si.inova.zimskasola

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_podatki_sobe.*
import com.google.firebase.firestore.FirebaseFirestore
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_moznosti.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

class Podatki_sobe : AppCompatActivity(), BeaconScanner.Listener {
    private var scanner = BeaconScanner(this, this)
    override fun onBeaconFound(data: String) {

        val beaconJson = JSONObject(data)
        val floor = beaconJson.get("floor_id").toString().trim()
        val room = beaconJson.get("room_id").toString().trim()
        try {
            val url = "https://firebasestorage.googleapis.com/v0/b/zs-beacons-2019.appspot.com/o/25022c4a-3035-11e9-bb6a-a5c92278bce1.json?alt=media&token=4607d4e9-c453-4ce8-9a9a-3b3d76ce244b"
            val queue = Volley.newRequestQueue(this)
            val stringReq = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    var strResp = URLDecoder.decode(URLEncoder.encode(response.toString(), "iso8859-1"),"UTF-8")
                    Log.i("STR", strResp)
                    moja_lokacija = Moja_lokacija()
                    val bundle = Bundle()
                    bundle.putString("floor", floor)
                    bundle.putString("room", room)
                    bundle.putString("json", strResp)
                    moja_lokacija.setArguments(bundle)
                    loadFragment(moja_lokacija)

                    val jsonObj: JSONObject = JSONObject(strResp)

                    location_adress.text = jsonObj.get("description").toString()
                    location_name.text = jsonObj.get("title").toString()
                    val floors: JSONArray = jsonObj.getJSONArray("floors")
                    for (i in 0 until floors.length()) {
                        var nadstropje: JSONObject = floors.getJSONObject(i)
                        if(nadstropje.get("floor_id").toString().trim() == floor){
                            val rooms: JSONArray = nadstropje.getJSONArray("rooms")
                            for (j in 0 until rooms.length()) {
                                var soba: JSONObject = rooms.getJSONObject(j)
                                if(soba.get("room_id").toString().trim() == room){
                                    val soba_name = soba.get("name").toString()

                                    val n = Intent(this, Podatki_sobe::class.java)
                                    n.putExtra("floor",floor)
                                    n.putExtra("room",room)
                                    n.putExtra("description",jsonObj.get("description").toString())
                                    n.putExtra("title",jsonObj.get("title").toString())
                                    n.putExtra("json",strResp)
                                    val contentIntent = PendingIntent.getActivity(this, 0, n, 0)

                                    var mBuilder = NotificationCompat.Builder(this, "0")
                                        .setSmallIcon(com.example.zimskasola.R.drawable.ic_beacons_icon_small)
                                        .setContentTitle("Vstopili ste v sobo $soba_name")
                                        .setContentText("Zaznali smo nov prostor. Poglejte kaj lahko v njem počnete")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(contentIntent)
                                        .setAutoCancel(true)
                                    with(NotificationManagerCompat.from(this)){
                                        notify(0, mBuilder.build())
                                    }
                                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                                    val timerIntent = Intent(this, Timer::class.java)
                                    timerIntent.putExtra("ime_sobe", soba_name)
                                    timerIntent.putExtra("countDownTime", sharedPreferences.getLong("gibanjeCas", 65000))
                                    startService(timerIntent)
                                }
                            }
                        }
                    }
                },
                Response.ErrorListener {
                    loadFragment(napaka)
                })
            queue.add(stringReq)
        }
        catch (ex: Exception){
            loadFragment(napaka)
        }
    }
    override fun onBeaconLost(data: String) {
        stopService(Intent(this, Timer::class.java))
        val ns = Context.NOTIFICATION_SERVICE
        val nMgr = this.getSystemService(ns) as NotificationManager
        nMgr.cancel(0)
        nMgr.cancel(1)
        supportFragmentManager
            .beginTransaction()
            .remove(moja_lokacija)
            .commit()
        moja_lokacija = Moja_lokacija()
        loadFragment(moja_lokacija)
        stopService(Intent(this, Timer::class.java))
    }

    var moja_lokacija = Moja_lokacija()
    val vsi_prostori = Vsi_prostori()
    val moznosti = Moznosti()
    val napaka = Napaka()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.zimskasola.R.layout.activity_podatki_sobe)

        if(intent.getStringExtra("floor")!=null){
            moja_lokacija = Moja_lokacija()
            val bundle = Bundle()
            bundle.putString("floor", intent.getStringExtra("floor"))
            bundle.putString("room", intent.getStringExtra("room"))
            bundle.putString("json", intent.getStringExtra("json"))
            location_adress.text = intent.getStringExtra("description")
            location_name.text = intent.getStringExtra("title")
            moja_lokacija.setArguments(bundle)
            loadFragment(moja_lokacija)
            bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)
        }
        else if(!isOnline()){
            loadFragment(napaka)
            bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)
        }
        else{

            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val name = "Obvestila o prostorih"
                val descriptionText = "Ko vstopite  nov prostor dobite obvestilo"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("0", name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                val name2 = "Obvestila o razgibavanju"
                val descriptionText2 = "Če se predolgo zadržujete v enem prostoru vas aplikacija obvesti."
                val importance2 = NotificationManager.IMPORTANCE_DEFAULT
                val channel2 = NotificationChannel("1", name2, importance2).apply {
                    description = descriptionText2
                }
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel2)
            }
            loadFragment(moja_lokacija)
            bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)
        }
        scanner.start()
    }
    override fun onStart() {
        try{
            super.onStart()
        }
        catch(ex:Exception){
            loadFragment(napaka)
        }


    }

    override fun onStop() {
        super.onStop()
    }

    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
        }
        return false
    }
    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {

    }
    override fun onDestroy() {
        super.onDestroy()
        scanner.stop()
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(com.example.zimskasola.R.id.soba_fragment_holder, fragment)
                .addToBackStack (null)
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
class Timer : Service(){
    lateinit var timer : CountDownTimer
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val countDownTime = intent!!.getLongExtra("countDownTime", 40000)
        timer = object: CountDownTimer(countDownTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("Timer", (millisUntilFinished/1000).toString())
            }

            override fun onFinish() {
                val ime_sobe = intent!!.getStringExtra("ime_sobe")
                var mBuilder = NotificationCompat.Builder(this@Timer, "1")
                    .setSmallIcon(com.example.zimskasola.R.drawable.ic_beacons_icon_small)
                    .setContentTitle("Opozorilo za razgibavanje")
                    .setContentText("Že dolgo ste v sobi $ime_sobe. Pojdite na sprehod.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //.setContentIntent(contentIntent)
                    .setAutoCancel(true)
                with(NotificationManagerCompat.from(this@Timer)){
                    notify(1, mBuilder.build())
                }
                stopSelf()
            }
        }
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }
}
