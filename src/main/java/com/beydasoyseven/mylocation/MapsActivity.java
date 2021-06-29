package com.beydasoyseven.mylocation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        //kullanicinin guncel konumunu almaya calistik

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //konum yonetecisiyle calisir.Konum degistiginde bana haber verir.

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                //konum degistirilince cagirilir

                /* konumu sürekli yenilemektense kullanici app i bir kere acikca basta alip ona göre islemler yapmasi
                icin basta şeyler yacagiz o yuzden bu satirlari yorum satiri halina getirdim (SON BİLİNEN KONUM) 124.satıra git kod orada !

                mMap.clear();
                LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userlocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));

                */

 /*
 kullanici konum üzerinde sitedigi yere tikladiginda o konumun enlem ve boylamini gostermek icin en yukarida arayüz ekledik ve en asagida
 public void onMapLongClick(@NonNull LatLng latLng) fonsiyonunu aktiflestirdi.Bunun sonucunda asagida yazdigimiz geocoder kodlarini
 bu fonksiyonunun icine alıyoruz ve bunları asagida yorum satiri haline getiriyoruz...

                // Geocoder = bir adresi enlem ve boylama yani koordinata cevirir
                // reverse geocoder = enlem ve boylami alip adrese cevirir


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addressList != null && addressList.size()>0){
                        System.out.println("Address:"+addressList.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }



*/
            }
        };

        //kullinicinin izni var mi yok mu onu konrol edecegiz
        //context=kontrol etme
        //checkSelfPermission()=istenilen izinler kontrol edilir
        //android olan Manifest dikkat et
        //Manifest.permission.ACCESS_FINE_LOCATION kullanıcının konumuna erisme izni
        //PERMISSION_GRANTED izin verilmediyse
        //else izin verildiyse

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //permission islemleri yapilacak

            ActivityCompat.requestPermissions(this , new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);

            //bu islemle izin istendi

        }
        else {
            //location islemleri yapilacak, kullanicinin konumunu istemeye baslayacagız

            //provider = konumu nereden alacak( parantez icinin 1.si)

            //mintime = min ne kadar surede guncelleme alayim, 0 verirsek devamlı kontrol etmeye calisir(2.)

            //minDistance = ne kadar mesafede bir kontrol edecek

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);

            //bilinen son konum kullanildi

            Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(lastlocation !=null){

                //uygulamada son konum yoksa cokmeye neden olabilir bu yuzden if ile null olup olmadigina bakiyoruz

                LatLng userlastlocation = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userlastlocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlastlocation,15));
            }

        }
    }

    //izinlerin sonucunda meydana gelen isleri ele aldigimiz metod

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //grant_Result = geri dönen sonuc var mi ona bakacagiz

        //requsestCode = istek kodu kontrol edecegiz

        if(grantResults.length > 0){
            if(requestCode == 1){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    //izin verildiyse ne yapacagizi kontrol edecegiz, yukarida else in altinda ne yapiliyorsa o yapilicak

                    //en ustte locationManager ve locationListener'i tanımla

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //haritaya uzun tiklandiginda ne olacak
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        mMap.clear();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address="";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList != null && addressList.size()>0){
                if(addressList.get(0).getThoroughfare() != null ){
                    address += addressList.get(0).getThoroughfare();
                    if(addressList.get(0).getSubThoroughfare() != null ){
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }
}