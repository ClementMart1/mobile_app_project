package com.example.bank_account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.lang.StringBuilder
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    protected lateinit var LName:EditText
    protected lateinit var LLastName:EditText
    protected lateinit var LButton: Button

    private val urlString: String = "https://60102f166c21e10017050128.mockapi.io/labbbank/config/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LName = findViewById(R.id.name)
        LLastName = findViewById(R.id.lastname)
        LButton = findViewById(R.id.login)
        LButton.setOnClickListener(View.OnClickListener {
            var nameInput:String = LName.text.toString()
            var lastNameInput:String = LLastName.text.toString()
            var result:String?= runBlocking { ProcessJSON(nameInput,lastNameInput) }
            if(result == null){
                Toast.makeText(this,"There was an error, please try again",Toast.LENGTH_SHORT).show()
            }
            else{
                val intent:Intent = Intent(this,AccountMenu::class.java)
                intent.putExtra("name",nameInput)
                intent.putExtra("lastName",lastNameInput)
                intent.putExtra("id",result)
                startActivity(intent)
            }
        })
    }

    suspend fun getData(urlString: String) = Dispatchers.Default{
        var dataStream: String? = null

        val handler: HTTPSDataHandler = HTTPSDataHandler()
        dataStream = handler.GetHTTPSData(urlString)

        return@Default dataStream
    }
    suspend fun ProcessJSON(LName: String,LLastName: String): String?{
        val dataStream:String? = getData(urlString)

        if(dataStream !=null){
            try{
                val reader:JSONArray = JSONArray(dataStream)
                var user:JSONObject
                var id:String
                var name:String
                var lastName:String
                for(i in 0..reader.length()-1){
                    user = reader.getJSONObject(i)
                    id = user.getString("id")
                    name= user.getString("name")
                    lastName= user.getString("lastname")
                    if(LName.equals(name) and LLastName.equals(lastName)){
                        return id
                    }
                }
            }catch (e:JSONException){
                e.printStackTrace()
            }
        }
        return null
    }


}