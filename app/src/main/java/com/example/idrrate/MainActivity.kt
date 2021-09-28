package com.example.idrrate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.idrrate.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    private val currencyList = mapOf(
        "AUD" to "Australian Dollar",
        "BND" to "Brunei Dollar",
        "CAD" to "Canadian Dollar",
        "CHF" to "Swiss Franc",
        "CNH" to "Chinese Yuan Offshore",
        "CNY" to "Chinese Yuan",
        "DKK" to "Danish Krone",
        "EUR" to "European Euro",
        "GBP" to "British Pound Sterling",
        "HKD" to "Hongkong Dollar",
        "JPY" to "Japanese Yen",
        "KRW" to "Korean Won",
        "KWD" to "Kuwaiti Dollar",
        "MYR" to "Malaysian Ringgit",
        "NOK" to "Norwegian Krone",
        "NZD" to "New Zealand Dollar",
        "PGK" to "Papua New Guinean Kina",
        "PHP" to "Phillipines Peso",
        "SAR" to "Saudi Arabian Riyal",
        "SEK" to "Swedish Krona",
        "SGD" to "Singapore Dollar",
        "THB" to "Thai Bath",
        "USD" to "US Dollar"
    )

    companion object {
        private const val STATE_RESULT = "state_result"
    }

    @SuppressLint("SetTextI18n")
    private fun webScrap(key: String) {
        thread {
            try {
                val doc = Jsoup
                    .connect("https://www.bi.go.id/en/statistik/informasi-kurs/transaksi-bi/Default.aspx")
                    .get()
                val all = doc.getElementsByTag("tbody")[1]
                val date = doc.getElementsContainingOwnText("Last Update")
                    .toString()
                    .split("<")[2]
                    .split(">")[1]

                this.runOnUiThread {
                    val split = all.text().split(" ")
                    val selected = split.indexOf(key)

                    if (selected != -1) {
                        val sell = "IDR " + split[selected + 2]
                        val buy = "IDR " + split[selected + 3]

                        binding.currencyName.text = currencyList[key]
                        binding.sellView.text = sell
                        binding.buyView.text = buy
                        binding.dateView.text = date
                    } else {
                        Toast.makeText(this, "Selected currency is not available", Toast.LENGTH_LONG).show()
                    }
                }
            }
            catch (e: Exception) {
                this.runOnUiThread {
                    binding.dateView.text = "Network or source website\nis unavailable."
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(
            this,
            R.layout.custom_spinner,
            currencyList.keys.toList()
        )

        binding.selectCurrency.adapter = adapter

        binding.selectCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.getCurrency.setOnClickListener(this@MainActivity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        if (savedInstanceState != null) {
            val result = savedInstanceState.getString(STATE_RESULT)
            binding.buyView.text = result
            binding.sellView.text = result
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_RESULT, binding.buyView.text.toString())
        outState.putString(STATE_RESULT, binding.sellView.text.toString())
    }

    override fun onClick(v: View?) {
        val selectedCurrency = binding.selectCurrency.selectedItem.toString()

        if (v?.id == R.id.getCurrency) webScrap(selectedCurrency)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val about = Intent(this@MainActivity, AboutMe::class.java)
        startActivity(about)
        return super.onOptionsItemSelected(item)
    }
}