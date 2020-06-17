package udit.programmer.co.fingerauth

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


class MainActivity : AppCompatActivity() {

    //Medium Link
    //https://medium.com/@manuelvicnt/android-fingerprint-authentication-f8c7c76c50f8

    // Check 1: Android version should be greater or equal to Marshmallow
    // Check 2: Device has Fingerprint Scanner
    // Check 3: Have permission to use fingerprint scanner in the app
    // Check 4: Lock screen is secured with atleast 1 type of lock
    // Check 5: Atleast 1 Fingerprint is registered

    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var fingerPrintHandler: FingerPrintHandler
    private val KEY_NAME = "AndroidKey"
    private lateinit var keyStore: KeyStore
    private lateinit var cipher: Cipher
    private lateinit var keyGenerator: KeyGenerator

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
                generateKey()
                if (cipherInit()) {
                    val cryptoObject = FingerprintManager.CryptoObject(cipher)
                    fingerPrintHandler = FingerPrintHandler(this)
                    fingerPrintHandler.startAuth(fingerprintManager, cryptoObject)
                }
            }
        } else Toast.makeText(this, "Android Version Not Supported", Toast.LENGTH_LONG).show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    ).build()
            )
            keyGenerator.generateKey()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun cipherInit(): Boolean {

        cipher = try {
            Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        return try {
            keyStore.load(null)
            val key: SecretKey = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
}