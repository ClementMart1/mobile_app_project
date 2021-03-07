package com.example.bank_account

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SQLiteDatabase
import org.json.JSONArray
import org.json.JSONException
import java.io.File

class AccountMenu : AppCompatActivity() {
    protected lateinit var RefreshButton: Button
    private lateinit var accountsView:TextView
    private lateinit var database:SQLiteDatabase

    private val urlString:String = "https://60102f166c21e10017050128.mockapi.io/labbbank/accounts/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_menu)
        val extras: Bundle? = intent.extras
        var name:String? = null
        var lastName:String? = null
        var id:String? = null
        if(extras != null){
            name = extras.getString("name")
            lastName = extras.getString("lastName")
            id = extras.getString("id")
        }
        val texteBienvenue: TextView = findViewById(R.id.bienvenue)
        accountsView = findViewById(R.id.accountsView)
        val database = InitializeSQLCipher()
        texteBienvenue.text =  name + " " + lastName+ "'s account(s)"
        var result:MutableList<AccountData>? = runBlocking { ProcessJSONAccounts() }
        if (result!=null){
            deleteAccounts(database)
            for(i in 0..result.size-1){
                var id: Int = result[i].id
                var accountName: String = result[i].name
                var amount:Double = result[i].amount
                var iban:String = result[i].iban
                var currency:String = result[i].currency
                insertAccount(database,accountName, amount, iban, currency)
            }
        }
        else{
            Toast.makeText(this, "There was an error, please try again", Toast.LENGTH_SHORT).show()
        }
        var results:MutableList<AccountData> = searchForAccounts(database)
        if (results != null) {
            for(i in 0..results.size-1){
                var accountSummary: String = results[i].name + ": "+ results[i].amount + " " + results[i].currency +
                        "\nIBAN: "+ results[i].iban + "\n\n"
                accountsView.append(accountSummary)
            }
        }
        RefreshButton = findViewById(R.id.refresh)
        RefreshButton.setOnClickListener(View.OnClickListener {
            finish()
            startActivity(intent)
        })
    }

    suspend fun getData(urlString: String) = Dispatchers.Default{
        var dataStream: String? = null

        val handler: HTTPSDataHandler = HTTPSDataHandler()
        dataStream = handler.GetHTTPSData(urlString)

        return@Default dataStream
    }
    suspend fun ProcessJSONAccounts(): MutableList<AccountData>?{
        val dataStream:String? = getData(urlString)
        if(dataStream !=null){
            try{
                var data:MutableList<AccountData> =  ArrayList()
                val reader = JSONArray(dataStream)
                for(i in 0..reader.length()-1){
                    var account = reader.getJSONObject(i)
                    var id:Int = account.getString("id").toInt()
                    var accountName: String = account.getString("accountName")
                    var iban:String = account.getString("iban")
                    var currency:String = account.getString("currency")
                    if(account.get("amount") is Int){
                        var amount:Int = account.getInt("amount")
                        var accountData = AccountData(
                            id,
                            accountName,
                            amount.toDouble(),
                            iban,
                            currency
                        )
                        data.add(accountData)
                    }
                    else{
                        var amount:String = account.getString("amount")
                        var accountData:AccountData = AccountData(
                            id,
                            accountName,
                            amount.toDouble(),
                            iban,
                            currency
                        )
                        data.add(accountData)
                    }
                }
                return data
            }catch (e: JSONException){
                e.printStackTrace()
            }
        }
        return null
    }

    private fun InitializeSQLCipher(): SQLiteDatabase{
        SQLiteDatabase.loadLibs(this)
        val databaseFile: File = getDatabasePath("Bank.db")
        databaseFile.mkdirs()
        databaseFile.delete()
        val database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "kotlinissofunhaha", null)
        val strSql: String = "create table if not exists Accounts (" +
                "id integer primary key autoincrement," +
                "name text not null," +
                "amount double not null," +
                "iban text not null," +
                "currency text not null)";
        database.execSQL(strSql)
        return database
    }
    public fun insertAccount(
        database: SQLiteDatabase,
        name: String,
        amount: Double,
        iban: String,
        currency: String
    ){
        val strSql:String = "insert into Accounts (name,amount,iban,currency) values ('" +
                name + "', " + amount + ", '" + iban + "', '" + currency + "')"
        database.execSQL(strSql)
    }

    public fun deleteAccounts(database: SQLiteDatabase){
        val strSql:String= "delete from Accounts"
        database.execSQL(strSql)
    }

    public fun searchForAccounts(database: SQLiteDatabase): MutableList<AccountData> {
        var result:MutableList<AccountData> = mutableListOf()
        val strSql:String = "select * from Accounts"
        val cursor: Cursor = database.rawQuery(strSql, null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            val accountData: AccountData = AccountData(
                cursor.getInt(0),
                cursor.getString(1), cursor.getDouble(2), cursor.getString(3),
                cursor.getString(4)
            )
            result.add(accountData)
            cursor.moveToNext()
        }
        cursor.close()
        return result
    }
}