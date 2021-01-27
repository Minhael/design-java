package me.minhael.design.props

import org.spongycastle.util.encoders.Base64
import java.net.URLDecoder
import java.net.URLEncoder

fun String.base64() = Base64.decode(this)
fun ByteArray.base64() = String(Base64.encode(this))

/**
 * Encode the string according to HTML 5, not exactly RFC 3986
 *
 * https://stackoverflow.com/questions/6533561/urlencode-the-asterisk-star-character
 */
fun String.urlEncode() = URLEncoder.encode(this, "utf-8")
fun String.urlDecode() = URLDecoder.decode(this, "utf-8")