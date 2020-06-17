package udit.programmer.co.fingerauth

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import kotlinx.android.synthetic.main.activity_main.*

@TargetApi(Build.VERSION_CODES.M)
class FingerPrintHandler() : FingerprintManager.AuthenticationCallback() {

    private lateinit var context: Context

    constructor(context: Context) : this() {
        this.context = context
    }

    fun startAuth(
        fingerprintManager: FingerprintManager,
        cryptoObject: FingerprintManager.CryptoObject?
    ) {
        fingerprintManager.authenticate(cryptoObject, CancellationSignal(), 0, this, null)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        return this.update("There was an Auth ERROR : $errString", false)
    }

    override fun onAuthenticationFailed() {
        this.update("Auth Failed", false)
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        this.update("ERROR : $helpString", false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        this.update("You can now access the Application", true)
    }

    private fun update(str: String, temp: Boolean) {
        if (temp) {
            Toast.makeText(this.context, str, Toast.LENGTH_LONG).show()
            val btn = (context as Activity).btn_enter as Button
            val iv = (context as Activity).first_view as ImageView
            val tv = (context as Activity).second_tv as TextView
            iv.setImageResource(R.drawable.ic_baseline_cloud_done_24)
            tv.setText("Fingerprint Authenticated")
            MaterialStyledDialog.Builder(context)
                .setTitle("Successful")
                .setDescription("Authentication Successful")
                .setIcon(R.drawable.ic_baseline_cloud_done_24)
                .setPositiveText("OK")
                .onPositive {
                    btn.isClickable = true
                    btn.setOnClickListener {
                        context.startActivity(Intent(this.context, BiometricActivity::class.java))
                    }
                }.show()
        } else Toast.makeText(this.context, str, Toast.LENGTH_LONG).show()
    }

}