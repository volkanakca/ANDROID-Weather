package com.example.havadurumu

import android.content.pm.PackageManager
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    var location:SimpleLocation?=null
    var latitute:String?=null
    var longitute:String?=null
    var tvSehir:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.sehirler,R.layout.spinner_tek_satir)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSehirler.background.setColorFilter(resources.getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP)
        spnSehirler.setTitle("Şehir Seçin")
        spnSehirler.adapter=spinnerAdapter


        spnSehirler.adapter=spinnerAdapter
        spnSehirler.setOnItemSelectedListener(this)



        spnSehirler.setSelection(1)
        verileriGetir("Ankara")














    }

    private fun oankiSehriGetir(lat: String?, longt:String?) {
        var sehirAdi:String?=null
        val url="https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longt+"&appid=8b0bdf6b9c48ab7b4dbf959d82c04233&lang=tr&units=metric"
        val havadurumuObjeRequest2=JsonObjectRequest(Request.Method.GET,url,null,object:Response.Listener<JSONObject>{
            override fun onResponse(response: JSONObject?) {
                var main=response?.getJSONObject("main")
                var sicaklik=main?.getInt("temp")
                tvSicaklik.text=sicaklik.toString()
                 sehirAdi=response?.getString("name")
                tvSehir?.setText(sehirAdi)



                var weather=response?.getJSONArray("weather")
                var aciklama=weather?.getJSONObject(0)?.getString("description")
                tvAciklama.text=aciklama
                var icon=weather?.getJSONObject(0)?.getString("icon")
                if(icon?.last()=='d'){
                    rootLayout.background=getDrawable(R.drawable.gunduz2)
                    tvAciklama.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvSicaklik.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvTarih.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    textView4.setTextColor(resources.getColor(R.color.colorPrimaryDark))

                }else {
                    rootLayout.background=getDrawable(R.drawable.gece2)
                    tvAciklama.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvSicaklik.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvTarih.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    textView4.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                }

                var resimDosyaAdi=resources.getIdentifier("a_"+icon.sonKarakterSil(),"drawable",packageName)
                imgHavaDurumu.setImageResource(resimDosyaAdi)


                tvTarih.text=tarihYazdir()
            }


        },object:Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {

            }

        })


        MySingleton.getInstance(this)?.addToRequestQueue(havadurumuObjeRequest2)



    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tvSehir=view as TextView
        if(position==0){
            location= SimpleLocation(this)
            if(!location!!.hasLocationEnabled()){
                spnSehirler.setSelection(1)
                Toast.makeText(this,"gps aç",Toast.LENGTH_LONG).show()
                SimpleLocation.openSettings(this)

            }else{

                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                   ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),60)
                }else{
                    location= SimpleLocation(this)
                    latitute= String.format("%,6f",location?.latitude)
                    longitute= String.format("%,6f",location?.longitude)
                    oankiSehriGetir(latitute,longitute)
                }
            }




        }else{
            var secilenSehir=parent?.getItemAtPosition(position).toString()

            verileriGetir(secilenSehir)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==60){
            if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                location= SimpleLocation(this)
                latitute= String.format("%,6f",location?.latitude)
                longitute= String.format("%,6f",location?.longitude)
                oankiSehriGetir(latitute,longitute)

            }else{
                spnSehirler.setSelection(1)
                Toast.makeText(this,"izin vermedin",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    fun tarihYazdir(): String? {
        var takvim= Calendar.getInstance().time
        var formatlayici=SimpleDateFormat("EEEE MMMM yyyy",Locale("tr"))
        var tarih=formatlayici.format(takvim)
        return tarih

    }
    fun verileriGetir(sehir:String){
        val url="https://api.openweathermap.org/data/2.5/weather?q="+sehir+"&appid=8b0bdf6b9c48ab7b4dbf959d82c04233&lang=tr&units=metric"
        val havadurumuObjeRequest=JsonObjectRequest(Request.Method.GET,url,null,object:Response.Listener<JSONObject>{
            override fun onResponse(response: JSONObject?) {
                var main=response?.getJSONObject("main")
                var sicaklik=main?.getInt("temp")
                tvSicaklik.text=sicaklik.toString()
                var sehirAdi=response?.getString("name")



                var weather=response?.getJSONArray("weather")
                var aciklama=weather?.getJSONObject(0)?.getString("description")
                tvAciklama.text=aciklama
                var icon=weather?.getJSONObject(0)?.getString("icon")
                if(icon?.last()=='d'){
                    rootLayout.background=getDrawable(R.drawable.gunduz2)
                    tvAciklama.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvSicaklik.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvTarih.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    textView4.setTextColor(resources.getColor(R.color.colorPrimaryDark))

                }else {
                    rootLayout.background=getDrawable(R.drawable.gece2)
                    tvAciklama.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvSicaklik.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    tvTarih.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    textView4.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                }

                var resimDosyaAdi=resources.getIdentifier("a_"+icon.sonKarakterSil(),"drawable",packageName)
                imgHavaDurumu.setImageResource(resimDosyaAdi)


                tvTarih.text=tarihYazdir()
            }


        },object:Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {

            }

        })


        MySingleton.getInstance(this)?.addToRequestQueue(havadurumuObjeRequest)
    }




}

private fun String?.sonKarakterSil(): String {
    return this!!.substring(0, this.length-1)

}
