package pabloromeromontero.r.demosfunciones.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import id.zelory.compressor.Compressor
import me.echodev.resizer.Resizer
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.coroutineContext

//region [Varios]
fun Context?.log(msg: String) {
    this?.apply {
        F.log(this.javaClass.simpleName, msg)
    }
}

fun Context?.log(e: Exception) {
    this?.apply {
        F.log(this.javaClass.simpleName, e)
    }
}

fun Fragment.log(msg: String) {
    context.log(msg)
}

fun Fragment.log(e: Exception) {
    context.log(e)
}

fun Context?.dialog(message: String) {
    dialog(message) {}
}

fun Context?.dialog(message: String, okListener: (DialogInterface) -> Unit) {
    this?.apply {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { d, _ -> okListener(d) }
                .create()
                .show()
    }
}

fun Context?.dialog(message: String, okListener: ((DialogInterface) -> Unit), cancelListener: ((DialogInterface) -> Unit)?) {
    this?.apply {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { d, _ -> okListener(d) }
                .setNegativeButton(android.R.string.cancel) { d, _ -> cancelListener?.invoke(d) }
                .create()
                .show()
    }
}

fun Fragment.dialog(message: String) {
    context.dialog(message) {}
}

fun Fragment.dialog(message: String, okListener: (DialogInterface) -> Unit) {
    context.dialog(message, okListener)
}

fun Fragment.dialog(message: String, okListener: ((DialogInterface) -> Unit), cancelListener: ((DialogInterface) -> Unit)?) {
    context.dialog(message, okListener, cancelListener)
}

fun Context?.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        Toast.makeText(this, message, duration).show()
    }
}

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(message, duration)
}

inline fun <reified T> Context?.systemService(): T? {
    return this?.let {
        ContextCompat.getSystemService(it, T::class.java)
    }
}

fun Context?.getImei(): String {
    this?.apply {
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> systemService<TelephonyManager>()?.imei
                else -> systemService<TelephonyManager>()?.deviceId
            } ?: ""
        else ""
    }
    return ""
}

fun Context?.isNetworkAvailable(): Boolean {
    return systemService<ConnectivityManager>()?.activeNetworkInfo?.isConnected ?: false
}

fun Context?.hideKeyboard(view: View?) {
    systemService<InputMethodManager>()?.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Context?.startActivity(activityClass: Class<out Activity>) {
    this?.apply {
        startActivity(Intent(this, activityClass))
    }
}

@Suppress("DEPRECATION")
fun Context?.startService(serviceClass: Class<out Service>) {
    if (systemService<ActivityManager>()?.getRunningServices(Integer.MAX_VALUE)?.none { serviceClass.name == it.service.className } == true)
        this?.startService(Intent(this, serviceClass))
}

@Suppress("DEPRECATION")
fun Context?.stopService(serviceClass: Class<out Service>) {
    if (systemService<ActivityManager>()?.getRunningServices(Integer.MAX_VALUE)?.any { serviceClass.name == it.service.className } == true)
        this?.stopService(Intent(this, serviceClass))
}

fun Context.toUri(file: File): Uri? {

    return when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> Uri.fromFile(file)
        else -> FileProvider.getUriForFile(this, C.APP.AUTHORITY, file)
    }
}

fun File.toByteArray(): ByteArray {
    val fis = FileInputStream(this)

    val length = this.length()
    var bytes = ByteArray(0)
    if (length > Integer.MAX_VALUE) {
        return bytes
    }
    bytes = ByteArray(length.toInt())
    var offset = 0
    val numRead: Int = fis.read(bytes, offset, bytes.size - offset)
    while (offset < bytes.size && numRead >= 0) {
        offset += numRead
    }

    if (offset < bytes.size) {
        throw IOException("Could not completely read file " + this.name)
    }

    fis.close()
    return bytes
}

fun File.save(text: String) {

    if (this.create()) {
        try {
            val bos = BufferedWriter(FileWriter(this, true))
            bos.write(text)
            bos.newLine()
            bos.flush()
            bos.close()
        } catch (e: Exception) {
            F.log("File.save", e)
        }
    }
}

fun File.create(): Boolean {

    val root = File(this.parent)
    return if (!root.exists()) {
        root.mkdirs()
    } else {
        true
    }
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}
//endregion

//region [F]
object F {

    /*fun isLogged(): Boolean {
        return DB.getUsers().isNotEmpty()
    }*/

    fun log(tag: String, msg: String) {
        if (C.APP.DEBUG) {
            Log.wtf(tag, msg)
        }
    }

    fun log(tag: String, e: Exception) {
        if (tag.isEmpty())
            log("error", e.toString())
        else
            log(tag, "error: " + e.toString())
    }

    fun writeToLog(stacktrace: String, filename: String) {
        try {
            val bos = BufferedWriter(FileWriter(C.PATH.LOGS + File.separator + filename, true))
            bos.write(stacktrace)
            bos.flush()
            bos.close()
        } catch (e: Exception) {
            log("F.writeToLog", e)
        }
    }

    fun getDeviceInfo(): String {
        return ("VERSION.RELEASE : " + Build.VERSION.RELEASE
                + ", VERSION.INCREMENTAL : " + Build.VERSION.INCREMENTAL
                + ", VERSION.SDK.NUMBER : " + Build.VERSION.SDK_INT
                + ", BOARD : " + Build.BOARD
                + ", BOOTLOADER : " + Build.BOOTLOADER
                + ", BRAND : " + Build.BRAND
                // + "\nCPU_ABI : " + Build.CPU_ABI
                // + "\nCPU_ABI2 : " + Build.CPU_ABI2
                + ", DISPLAY : " + Build.DISPLAY
                + ", FINGERPRINT : " + Build.FINGERPRINT
                + ", HARDWARE : " + Build.HARDWARE
                + ", HOST : " + Build.HOST
                + ", ID : " + Build.ID
                + ", MANUFACTURER : " + Build.MANUFACTURER
                + ", MODEL : " + Build.MODEL
                + ", PRODUCT : " + Build.PRODUCT
                // + "\nSERIAL : " + Build.SERIAL
                + ", TAGS : " + Build.TAGS
                + ", TIME : " + Build.TIME
                + ", TYPE : " + Build.TYPE
                + ", UNKNOWN : " + Build.UNKNOWN
                + ", USER : " + Build.USER)
    }


    ///////////////////////////////  DateTime  /////////////////////////

    object DateTime {

        class Date(private val d: Int, private val m: Int, private val y: Int) {

            /** format dd/MM/yyyy **/
            constructor(s: String) : this(d = s.split("/")[0].toInt(), m = s.split("/")[1].toInt(), y = s.split("/")[2].toInt())

            constructor(cal: Calendar) : this(d = cal.get(Calendar.DAY_OF_MONTH), m = cal.get(Calendar.MONTH) + 1, y = cal.get(Calendar.YEAR))

            constructor(millis: Long) : this(cal = Calendar.getInstance().apply { timeInMillis = millis })

            override fun toString(): String {
                return "${d.to2Digits()}/${m.to2Digits()}/$y"
            }

        }

        class Time(private val h: Int = 0, private val m: Int = 0) {

            /** format HH:mm **/
            constructor(s: String) : this(h = s.split(":")[0].toInt(), m = s.split(":")[1].toInt())

            constructor(cal: Calendar) : this(h = cal.get(Calendar.HOUR_OF_DAY), m = cal.get(Calendar.MINUTE))

            constructor(millis: Long) : this(cal = Calendar.getInstance().apply { timeInMillis = millis })

            operator fun plus(other: Time): Time {
                return Time(h = (this.h + other.h) + (this.m + other.m) / 60, m = (this.m + other.m) % 60)
            }

            operator fun minus(other: Time): Time {

                var mh = this.h - other.h
                var mm = this.m - other.m

                if (mm < 0) {
                    mm += 60
                    mh -= 1
                }

                return Time(h = mh, m = mm)
            }

            override fun toString(): String {
                return "${h.to2Digits()}:${m.to2Digits()}"
            }

        }

        private fun Int.to2Digits(): String {
            return String.format("%02d", this)
        }

    }


    ///////////////////////////////  VALIDATES  /////////////////////////

    object Validate {


    }

    ///////////////////////////////  PHOTOS  ////////////////////////////

    object Photo {

        /**
         * @param filename complete path name for the file
         * @return compressed image file to byte[]
         */
        fun compressImage(filename: String): ByteArray? {

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // target
            var tHeight = 900
            var tWidth = 1200

            if (photoW < photoH) {
                tHeight = 1200
                tWidth = 900
            }

            //scaled
            var sHeight = tHeight
            var sWidth = (photoW.toFloat() / (photoH.toFloat() / tHeight.toFloat())).toInt()

            if (photoW < photoH) {
                sWidth = tWidth
                sHeight = (photoH.toFloat() / (photoW.toFloat() / tWidth.toFloat())).toInt()
            }

            // crop
            val cWidth = tWidth
            val cHeight = tHeight

            var bm: ByteArray?
            do {
                try {

                    bm = toByteArray(rotateBitmap(filename, cropBitmap(scaleBitmap(convertBitmap(filename), sWidth, sHeight), cWidth, cHeight), tWidth, tHeight))
                } catch (oome: OutOfMemoryError) {
                    bm = null
                    log("Photo.generateBitmap", oome.toString())
                    System.gc()
                    try {
                        Thread.sleep(2000)
                    } catch (ie: InterruptedException) {
                        log("F.Photo.compressImage", ie)
                    }

                }

            } while (bm == null)

            System.gc()

            return bm
        }

       fun compressImage(filename: String, context: Context): ByteArray? {

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // target
            var tHeight = 900
            var tWidth = 1200

            if (photoW < photoH) {
                tHeight = 1200
                tWidth = 900
            }

            //scaled
            var sHeight = tHeight
            var sWidth = (photoW.toFloat() / (photoH.toFloat() / tHeight.toFloat())).toInt()

            if (photoW < photoH) {
                sWidth = tWidth
                sHeight = (photoH.toFloat() / (photoW.toFloat() / tWidth.toFloat())).toInt()
            }

            // crop
            val cWidth = tWidth
            val cHeight = tHeight

            var bm: ByteArray?
            do {
                try {

                    bm = toByteArray(rotateBitmap(filename, cropBitmap(scaleBitmap( convertBitmap(filename, context), sWidth, sHeight), cWidth, cHeight), tWidth, tHeight))

                } catch (oome: OutOfMemoryError) {
                    bm = null
                    log("Photo.generateBitmap", oome.toString())
                    System.gc()
                    try {
                        Thread.sleep(2000)
                    } catch (ie: InterruptedException) {
                        log("F.Photo.compressImage", ie)
                    }

                }

            } while (bm == null)

            System.gc()

            return bm
        }

        /***
         *
         * @param activity  Activity
         * @param tipo      C.FOTO.*
         * @param detalle   String campo representativo, normalmente el id
         */
        fun takePhoto(activity: Activity, nombreFoto:String, path: String, codePhotoRequest: Int) {

            val root = File(path)
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    activity.dialog("Error de escritura.\nInténtelo de nuevo más tarde.")
                    return // null;
                }
            }

            //val iFoto = P[activity.applicationContext, P.I.FOTO]
            val fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())


            //val name = "$fecha·$idPersona·$idUsuario·$tipo·$detalle·$iFoto.jpg"
            val nombreFoto = "$nombreFoto·$fecha.jpg"

            activity.toUri(File(root, nombreFoto))?.also { uri ->
                startCameraActivity(activity, uri, codePhotoRequest)
            }

        }

        fun takePhoto(activity: Activity, nombreFoto:String, path: String, codePhotoRequest: Int, sobrecarga: Boolean) {

            val root = File(path)
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    activity.dialog("Error de escritura.\nInténtelo de nuevo más tarde.")
                    return // null;
                }
            }


            activity.toUri(File(root, nombreFoto))?.also { uri ->
                startCameraActivity(activity, uri, codePhotoRequest)
            }

        }

        /*class ImageLoaderTask : AsyncTask<String, String, Boolean>() {

            var callback: AsyncCallback? = null

            override fun doInBackground(vararg params: String): Boolean? {
                return F.Photo.saveImage(File(C.PATH.PHOTO, params[0]).toString())
            }

            override fun onPostExecute(result: Boolean?) {
                callback?.processFinish(result!!)
            }

            interface AsyncCallback {
                fun processFinish(result: Boolean)
            }

        }*/

        /*private fun saveImage(filename: String): Boolean {

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // target
            var tHeight = 1200
            var tWidth = 1600

            if (photoW < photoH) {
                tHeight = 1600
                tWidth = 1200
            }

            //scaled
            var sHeight = tHeight
            var sWidth = (photoW.toFloat() / (photoH.toFloat() / tHeight.toFloat())).toInt()

            if (photoW < photoH) {
                sWidth = tWidth
                sHeight = (photoH.toFloat() / (photoW.toFloat() / tWidth.toFloat())).toInt()
            }

            // crop
            val cWidth = tWidth
            val cHeight = tHeight

            var bm: ByteArray?
            do {
                try {
                    bm = toByteArray(rotateBitmap(filename, cropBitmap(scaleBitmap(convertBitmap(filename), sWidth, sHeight), cWidth, cHeight), tWidth, tHeight))
                } catch (oome: OutOfMemoryError) {
                    bm = null
                    log("Photo.generateBitmap", oome.toString())
                    System.gc()
                    try {
                        Thread.sleep(2000)
                    } catch (ie: InterruptedException) {
                        log("F.Photo.saveImage", ie)
                    }

                }

            } while (bm == null)

            System.gc()
            try {
                byteArrayToFile(bm, filename)
                return true
            } catch (e: IOException) {
                log("", e)
            }

            return false
        }*/

        fun convertBitmap(path: String): Bitmap? {

            log("Photo", "convertBitmap")
            var bitmap: Bitmap? = null
            val bfOptions = BitmapFactory.Options()
            bfOptions.inTempStorage = ByteArray(32 * 1024)

            val file = File(path)


            var fs: FileInputStream? = null
            try {
                fs = FileInputStream(file)
            } catch (e: FileNotFoundException) {
                log("F.Photo.convertBitmap", e)
            }

            try {
                if (fs != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fs.fd, null, bfOptions)
                }
            } catch (e: IOException) {
                log("F.Photo.convertBitmap", e)
            } finally {
                if (fs != null) {
                    try {
                        fs.close()
                    } catch (e: IOException) {
                        log("F.Photo.convertBitmap", e)
                    }

                }
            }
            return bitmap
        }

        fun convertBitmap(path: String, context: Context): Bitmap? {

            log("Photo", "convertBitmap")
            var bitmap: Bitmap? = null
            val bfOptions = BitmapFactory.Options()
            bfOptions.inTempStorage = ByteArray(32 * 1024)


            /*val fileAux = File(path)
            var file = Compressor(context)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).absolutePath
                )
                .compressToFile(fileAux)*/

            val file = File(path)


            var fs: FileInputStream? = null
            try {
                fs = FileInputStream(file)
            } catch (e: FileNotFoundException) {
                log("F.Photo.convertBitmap", e)
            }

            try {
                if (fs != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fs.fd, null, bfOptions)
                }
            } catch (e: IOException) {
                log("F.Photo.convertBitmap", e)
            } finally {
                if (fs != null) {
                    try {
                        fs.close()
                    } catch (e: IOException) {
                        log("F.Photo.convertBitmap", e)
                    }

                }
            }


            return bitmap
        }

        private fun scaleBitmap(original: Bitmap?, width: Int, height: Int): Bitmap {

            log("Photo", "scaleBitmap")
            return Bitmap.createScaledBitmap(original!!, width, height, false)
        }

        private fun cropBitmap(original: Bitmap, width: Int, height: Int): Bitmap {

            log("Photo", "cropBitmap")
            val croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val canvas = Canvas(croppedImage)

            val srcRect = Rect(0, 0, original.width, original.height)
            val dstRect = Rect(0, 0, width, height)

            val dx = (srcRect.width() - dstRect.width()) / 2
            val dy = (srcRect.height() - dstRect.height()) / 2

            // If the srcRect is too big, use the center part of it.
            srcRect.inset(Math.max(0, dx), Math.max(0, dy))

            // If the dstRect is too big, use the center part of it.
            dstRect.inset(Math.max(0, -dx), Math.max(0, -dy))

            // Draw the cropped bitmap in the center
            canvas.drawBitmap(original, srcRect, dstRect, null)

            original.recycle()

            return croppedImage
        }

        private fun rotateBitmap(filename: String, original: Bitmap, width: Int, height: Int): Bitmap {

            log("Photo", "rotateBitmap")
            val matrix = Matrix()
            var exifReader: ExifInterface? = null
            try {
                exifReader = ExifInterface(filename)
            } catch (e: IOException) {
                log("F.Photo.rotateBitmap", e)
            }

            var orientation = 0
            if (exifReader != null) {
                orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
            }

            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> matrix.postRotate(0f)
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(original, 0, 0, width, height, matrix, false)
        }

        private fun toByteArray(original: Bitmap): ByteArray {

            log("Photo", "toByteArray")

            val baos = ByteArrayOutputStream()
            original.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            return baos.toByteArray()
        }

        fun startCameraActivity(activity: Activity, file: Uri, codePhotoRequest: Int) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            activity.startActivityForResult(intent, codePhotoRequest)
        }





    }

    ///////////////////////////////  SECURITY  //////////////////////////

    object Security {

        /**
         * @param value data to encrypt
         * @return String result of encryption
         */
        fun encrypt(value: String, key: String): String {
            val valueBytes = value.toByteArray(charset("UTF-8"))
            val keyBytes = getKeyBytes(key)
            return Base64.encodeToString(encrypt(valueBytes, keyBytes, keyBytes), 0)
        }

        private fun encrypt(paramArrayOfByte1: ByteArray, paramArrayOfByte2: ByteArray, paramArrayOfByte3: ByteArray): ByteArray {
            // setup AES cipher in CBC mode with PKCS #7 padding
            val localCipher = Cipher.getInstance("AES/CBC/PKCS7Padding")//PKCS7Padding

            // encrypt
            localCipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(paramArrayOfByte2, "AES"), IvParameterSpec(paramArrayOfByte3))
            return localCipher.doFinal(paramArrayOfByte1)
        }

        /**
         * @param value data to decrypt
         * @return String result after decryption
         */
        fun decrypt(value: String, key: String): String {
            val valueBytes = Base64.decode(value, 0)
            val keyBytes = getKeyBytes(key)
            return String(decrypt(valueBytes, keyBytes, keyBytes), charset("UTF-8"))
        }

        private fun decrypt(ArrayOfByte1: ByteArray, ArrayOfByte2: ByteArray, ArrayOfByte3: ByteArray): ByteArray {
            // setup AES cipher in CBC mode with PKCS #7 padding
            val localCipher = Cipher.getInstance("AES/CBC/PKCS7Padding")//PKCS7Padding

            // decrypt
            localCipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(ArrayOfByte2, "AES"), IvParameterSpec(ArrayOfByte3))
            return localCipher.doFinal(ArrayOfByte1)
        }

        private fun getKeyBytes(paramString: String): ByteArray {
            val arrayOfByte1 = ByteArray(16)
            val arrayOfByte2 = paramString.toByteArray(charset("UTF-8"))
            System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, Math.min(arrayOfByte2.size, arrayOfByte1.size))
            return arrayOfByte1
        }
    }


}
//endregion

