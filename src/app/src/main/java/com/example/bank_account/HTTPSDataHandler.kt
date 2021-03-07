package com.example.bank_account

import android.widget.Toast
import java.io.*
import java.lang.StringBuilder
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class HTTPSDataHandler {
    public fun GetHTTPSData(urlString: String): String? {
        var stream: String? = null
        try {
            val connection = URL(urlString).openConnection() as HttpsURLConnection
            if(connection.responseCode == 200){
                val input: InputStream = BufferedInputStream(connection.inputStream)

                val reader: BufferedReader = BufferedReader(InputStreamReader(input))
                val stringBuilder: StringBuilder = StringBuilder()
                var line: String? = reader.readLine()
                while(line != null){
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
                stream = stringBuilder.toString();

                connection.disconnect()
            }
        }catch (e:MalformedURLException){
            e.printStackTrace();
        }catch (e:IOException){
            e.printStackTrace();
        }
        return stream
    }
}