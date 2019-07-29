package nl.rsdt.japp.jotial.auth


import android.util.Log

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author ?
 * @version 1.0
 * @since 15-1-2016
 * Tool for SHA1.
 */
object AeSimpleSHA1 {
    private fun convertToHex(data: ByteArray): String {
        val buf = StringBuilder()
        for (b in data) {
            var halfbyte = (b.toInt() ushr 4) and 0x0F
            var two_halfs = 0
            do {
                buf.append(if (0 <= halfbyte && halfbyte <= 9) ('0'.toInt() + halfbyte).toChar() else ('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = b.toInt() and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }

    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun SHA1(text: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        md.update(text.toByteArray(charset("iso-8859-1")), 0, text.length)
        val sha1hash = md.digest()
        return convertToHex(sha1hash)
    }

    fun trySHA1(text: String): String {
        try {
            return SHA1(text)
        } catch (e: Exception) {
            Log.e("AeSImpleSHA1", e.localizedMessage, e)
        }

        return "error-in-trySHA1"
    }

}