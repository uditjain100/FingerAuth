package udit.programmer.co.fingerauth

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BiometricActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        val activity: BiometricActivity = this

        BiometricPrompt.Builder(this)
            .setTitle("Fingerprint Authenticator")
            .setSubtitle("Scanner")
            .setDescription("Place your finger on secsor")
            .setNegativeButton(
                "Cancel",
                Executors.newSingleThreadExecutor(),
                DialogInterface.OnClickListener { dialog, which -> })
            .build()
            .authenticate(
                CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        activity.runOnUiThread(Runnable {
                            Toast.makeText(
                                this@BiometricActivity, "Biometric Authenticated",
                                Toast.LENGTH_LONG
                            ).show()
                        })
                    }
                })
    }
}