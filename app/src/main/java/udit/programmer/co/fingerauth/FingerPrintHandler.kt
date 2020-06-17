package udit.programmer.co.fingerauth

import android.app.Activity
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.widget.Toast

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
            MainActivity().changeImage()
        } else Toast.makeText(this.context, str, Toast.LENGTH_LONG).show()
    }

}