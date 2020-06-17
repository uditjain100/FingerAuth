package udit.programmer.co.fingerauth

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var fingerPrintHandler: FingerPrintHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (!fingerprintManager.isHardwareDetected())
                Toast.makeText(this, "Fingerprint Scanner not detected", Toast.LENGTH_LONG).show()
            else if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.USE_FINGERPRINT
                ) != PackageManager.PERMISSION_GRANTED
            )
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show()
            else if (!keyguardManager.isKeyguardSecure)
                Toast.makeText(this, "Add lock to your phone", Toast.LENGTH_LONG).show()
            else if (!fingerprintManager.hasEnrolledFingerprints())
                Toast.makeText(this, "Atleast 1 Fingerprint is Required", Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(this, "Good to go", Toast.LENGTH_LONG).show()
                fingerPrintHandler = FingerPrintHandler(this)
                fingerPrintHandler.startAuth(fingerprintManager, null)
            }
        } else Toast.makeText(this, "Android Version Not Supported", Toast.LENGTH_LONG).show()
    }
    
}