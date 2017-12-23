package com.example.mehmetmetin436.proje;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AnasayfaActivity extends MainActivity {


    Button kitapBtn,resimBtn;
    Button guncelleKitap;
    private static int RESULT_LOAD_IMAGE_GALERI=101;
    private static int RESULT_LOAD_IMAGE_KAMERA=102;
    PopupMenu popup;
    ImageView kitapResim;
    EditText kitapAdEt;
    EditText isbnEt;
    EditText yazarAdEt;
    EditText kitapTuruEt;
    ListView kitapList;

    int id=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);//https://gelecegiyazanlar.turkcell.com.tr/konu/android/egitim/android-201/eylem-dugmeleri-eklemek
        //proje yazısının anasayfa gibi çalışması için yazılan kod mainActivityi manifest dosyasında ana activity olarak ayarlamamız gerekiyor

        kitapBtn=(Button)findViewById(R.id.btnKitapEkle);
        resimBtn=(Button)findViewById(R.id.btnKitapResmi);
        kitapResim=(ImageView)findViewById(R.id.imageView);
        guncelleKitap=(Button)findViewById(R.id.btnKitapGuncelle);

        kitapAdEt=(EditText)findViewById(R.id.kitapAdi);
        isbnEt=(EditText)findViewById(R.id.kitapIsbn);
        yazarAdEt=(EditText)findViewById(R.id.kitapYazar);
        kitapTuruEt=(EditText) findViewById(R.id.kitapTuru);

        kitapList=(ListView)findViewById(R.id.liste);



        kitapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String item = kitapList.getItemAtPosition(index).toString();
                String []bol = item.split("--");
                id = Integer.valueOf(bol[0].toString());

                kitapAdEt.setText(bol[1].toString());
                isbnEt.setText(bol[2].toString());
                yazarAdEt.setText(bol[3].toString());
                kitapTuruEt.setText(bol[3].toString());

            }
        });
        listele();
        popup = new PopupMenu(AnasayfaActivity.this, resimBtn);
        popup.getMenuInflater().inflate(R.menu.kamera_galeri, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.kameraAc:
                        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intentKamera,RESULT_LOAD_IMAGE_KAMERA);
                        return  true;
                    case R.id.galeriAc:
                        Intent intentGaleri = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intentGaleri,RESULT_LOAD_IMAGE_GALERI);
                        return true;
                    default :
                        return false;
                }
            }
        });
    }
    //gelen fotoğrafon image viewimize yerleşesini sağlar.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==RESULT_LOAD_IMAGE_KAMERA){
            Bundle extras = data.getExtras();
            kitapResim.setImageBitmap((Bitmap) extras.get("data"));
        }
        else if(requestCode!=RESULT_LOAD_IMAGE_GALERI){
            Uri secFoto=data.getData();
            String []dosyaYolu={MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(secFoto,dosyaYolu,null,null,null);
            cursor.moveToFirst();


            int sutunIndex = cursor.getColumnIndex(dosyaYolu[0]);
            String resminYolu=cursor.getString(sutunIndex);
            cursor.close();
            kitapResim.setImageBitmap(BitmapFactory.decodeFile(resminYolu));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    /*public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.uygulmaKapat:
                System.exit(0);
                return true;
            case  R.id.cikisi_yap:
                Intent intent  = new Intent(AnasayfaActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    public void resimClicked(View view){
        popup.show();
    }

    public void listele(){
        Database db = new Database(AnasayfaActivity.this);
        ArrayList<String> list = db.DBArray();
        ArrayAdapter<String> adaptor = new ArrayAdapter<>(AnasayfaActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,list);
        kitapList.setAdapter(adaptor);
    }

    public void ekleClicked(View view){
        String kitap_adi=kitapAdEt.getText().toString();
        String Isbn=isbnEt.getText().toString();
        String yazar_adi=yazarAdEt.getText().toString();
        String kitap_turu=kitapTuruEt.getText().toString();
        if (kitap_adi.isEmpty() || Isbn.isEmpty() || yazar_adi.isEmpty() || yazar_adi.isEmpty() || kitap_turu.isEmpty()){
            Toast.makeText(this, R.string.bos_alan, Toast.LENGTH_SHORT).show();
        }else{
            Kitaplardb kitap=new Kitaplardb(kitap_adi,Isbn,yazar_adi,kitap_turu);
            Database db = new Database(AnasayfaActivity.this);
            db.kitapEkle(kitap);
            Toast.makeText(this, R.string.kitap_kayit, Toast.LENGTH_SHORT).show();
            listele();
            kitapAdEt.setText("");
            isbnEt.setText("");
            yazarAdEt.setText("");
            kitapTuruEt.setText("");
            Intent intent = new Intent(getApplicationContext(), AnasayfaActivity.class);
            PendingIntent pending = PendingIntent.getActivity(getApplicationContext(),
                    (int) System.currentTimeMillis(), intent, 0);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("İŞLEMİNİZ BAŞARILI")//Bildirim Başlık Metni
                    .setContentText(kitap_adi + " isimli kitabınız eklendi.") //Bildirimim açıklama metni
                    .setSmallIcon(R.mipmap.ic_launcher) //Bildirimin küçük iconu
                    .setContentIntent(pending) //Bildirim tıklanınca nereye gidilecek
                    .setAutoCancel(true) //Bildirim kapatılabilsinmi
                    .build(); //bildirim oluşturulsun

            notification.defaults |= Notification.DEFAULT_SOUND; //bildirim geldiğinde Sesle uyarı yapacak

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification); //Bildirimi ekranda göster

            // Bildirim otomatik yok olacak tıklanmadan sonra
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
    }
    public void guncelleClicked(View view) {
        Database DB = new Database(AnasayfaActivity.this);
        String kitap_adi = kitapAdEt.getText().toString();
        String isbn = isbnEt.getText().toString();
        String yazar_adi = yazarAdEt.getText().toString();
        String kitap_turu = kitapTuruEt.getText().toString();
        if (kitap_adi.isEmpty() || isbn.isEmpty() || yazar_adi.isEmpty() || yazar_adi.isEmpty() || kitap_turu.isEmpty()) {
            Toast.makeText(this, R.string.bos_alan, Toast.LENGTH_SHORT).show();
        } else {

            Kitaplardb kitap = new Kitaplardb(id, kitap_adi, isbn, yazar_adi, kitap_turu);

            DB.kitapGuncelle(kitap);
            Toast.makeText(this, R.string.kitap_basarili, Toast.LENGTH_SHORT).show();
            listele();
            kitapAdEt.setText("");
            isbnEt.setText("");
            yazarAdEt.setText("");
            kitapTuruEt.setText("");

        }
    }

    public void silClicked(View view){
        Kitaplardb kitap = new Kitaplardb(id);
        Database DB = new Database(AnasayfaActivity.this);
        DB.KitapSil(kitap);
        Toast.makeText(this, R.string.kitap_sil, Toast.LENGTH_SHORT).show();
        listele();
        kitapAdEt.setText("");
        isbnEt.setText("");
        yazarAdEt.setText("");
        kitapTuruEt.setText("");

    }
}
