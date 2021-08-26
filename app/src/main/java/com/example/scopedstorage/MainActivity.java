package com.example.scopedstorage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button saveBtn,nxtBtn;
    String urlLink;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int x =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        saveBtn = findViewById(R.id.saveBtn);
        nxtBtn = findViewById(R.id.nxtBtn);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkConnection();

        //urlLink = "https://random.imagecdn.app/400/400";

        //LoadImage loadImage = new LoadImage(imageView);
       // loadImage.execute(urlLink);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x=3;
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                saveImageToGallery(bitmap);
                encodeTobase64(bitmap);
               // sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = sharedPreferences.edit();
                editor.putString("imagePreference",encodeTobase64(bitmap));
                editor.apply();

            }
        });

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               checkConnection();

            }
        });



    }

    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+ ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "TestFolder");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                //pathName = "/storage/emulated/0/Pictures/TestFolder/Image_.jpg";

                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);


                Toast.makeText(this,"Image Saved",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
          Toast.makeText(this,"Image Not Saved \n" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadImage extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView1;
        public LoadImage(ImageView imageView) {
            this.imageView1 = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
           String urlLink = strings[0];
           Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlLink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null!=activeNetwork){
          Toast.makeText(this,"Internet Connected",Toast.LENGTH_SHORT).show();
            urlLink = "https://random.imagecdn.app/400/400";

            LoadImage loadImage = new LoadImage(imageView);
            loadImage.execute(urlLink);
        } else {
            if (sharedPreferences == null){
                Toast.makeText(this,"You Have Not Saved Any Images",Toast.LENGTH_SHORT).show();
            } else {

                String images123 = sharedPreferences.getString("imagePreference", null);
                decodeBase64(images123);
                imageView.setImageBitmap(decodeBase64(images123));
                Toast.makeText(this, "Internet Not Connected,Previously Stored Image will be Displayed", Toast.LENGTH_SHORT).show();
            }



           /* urlLink = "https://random.imagecdn.app/400/400";
            Toast.makeText(this,"Internet Not Connected",Toast.LENGTH_SHORT).show();
            Glide.with(MainActivity.this)
                    .load(urlLink)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.image)
                    .into(imageView); */
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap image11 = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image11.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


  /*  public void getImage(String url){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Tag","onResponse: "+ response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(arrayRequest);
    } */
}