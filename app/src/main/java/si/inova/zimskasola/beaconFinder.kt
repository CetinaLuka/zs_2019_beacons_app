package si.inova.zimskasola

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class beaconFinder: Service(), BeaconScanner.Listener{
    private var scanner = BeaconScanner(this, this)
    override fun onBeaconFound(data: String) {
        Log.i("BeaconListener", "Našel beacon!!!!")
    }

    override fun onBeaconLost(data: String) {
        Log.i("BeaconListener", "Izgubil beacon!!!!")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        scanner.start()
        for(i in 1..100)
            Log.i("Stejem", i.toString())
        return START_STICKY;
    }

    override fun onDestroy() {
        scanner.stop()
        super.onDestroy()
    }

}