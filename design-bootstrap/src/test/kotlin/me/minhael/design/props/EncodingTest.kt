package me.minhael.design.props

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EncodingTest {

    @Test
    fun testBase64() {
        val str = "Quick Brown Fox Jump Over Lazy Dog".encodeToByteArray()
        val b64 = "UXVpY2sgQnJvd24gRm94IEp1bXAgT3ZlciBMYXp5IERvZw=="

        assertEquals(b64, str.base64())
        assertArrayEquals(str, b64.base64())

        assertArrayEquals(str, str.base64().base64())
        assertEquals(b64, b64.base64().base64())
    }

    @Test
    fun testUrlEncode() {
        val str = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9 - _ . ~ ! * ' ( ) ; : @ & = + \$ , / ? % # [ ]"
        val url = "A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z+a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s+t+u+v+w+x+y+z+0+1+2+3+4+5+6+7+8+9+-+_+.+%7E+%21+*+%27+%28+%29+%3B+%3A+%40+%26+%3D+%2B+%24+%2C+%2F+%3F+%25+%23+%5B+%5D"

        assertEquals(url, str.urlEncode())
        assertEquals(str, url.urlDecode())

        assertEquals(str, str.urlEncode().urlDecode())
        assertEquals(url, url.urlDecode().urlEncode())
    }
}