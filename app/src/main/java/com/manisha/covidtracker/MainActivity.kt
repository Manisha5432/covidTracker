package com.manisha.covidtracker



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {


    private  lateinit var countryCasesTV: TextView
    private lateinit var countryRecoveredTV: TextView
    private lateinit var countryDeathsTV: TextView
    private lateinit var stateRV: RecyclerView
    private lateinit var stateRVAdapter: StateRVAdapter
   private lateinit var stateList: List<StateModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countryCasesTV = findViewById(R.id.idTVIndiaCases)
        countryRecoveredTV = findViewById(R.id.idTVIndiaRecovered)
        countryDeathsTV = findViewById(R.id.idTVIndiaDeaths)
        stateRV = findViewById(R.id.idRVStates)
       stateList = ArrayList()
        getStateInfo()

    }

    private fun getStateInfo() {
        val url = "https://api.rootnet.in/covid19-in/stats/latest"
        val queue = Volley.newRequestQueue(this@MainActivity)

                val request =
            JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                try {
                    val success = response.getBoolean("success")

                    if (success) {

                        val dataObj = response.getJSONObject("data")
                        val summaryObj = dataObj.getJSONObject("summary")
                        val cases: Int = summaryObj.getInt("total")
                        val recovered: Int = summaryObj.getInt("discharged")
                        val deaths: Int = summaryObj.getInt("deaths")

                        countryCasesTV.text = cases.toString()
                        countryRecoveredTV.text = recovered.toString()
                        countryDeathsTV.text = deaths.toString()

                        val regionalArray = dataObj.getJSONArray("regional")
                        for (i in 0 until regionalArray.length()) {
                            val regionalObj = regionalArray.getJSONObject(i)
                            val stateName: String = regionalObj.getString("loc")
                            val cases: Int = regionalObj.getInt("totalConfirmed")
                            val deaths: Int = regionalObj.getInt("deaths")
                            val recovered: Int = regionalObj.getInt("discharged")

                            val stateModel = StateModel(stateName, recovered, deaths, cases)
                            stateList = stateList + stateModel

                        }
                        stateRVAdapter = StateRVAdapter(stateList)
                        stateRV.layoutManager = LinearLayoutManager(this)
                        stateRV.adapter = stateRVAdapter
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, {
                    error(

                        Toast.makeText(this@MainActivity,"Volley error occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    )
                    })
       queue.add(request)


    }


}
