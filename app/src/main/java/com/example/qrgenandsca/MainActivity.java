package com.example.qrgenandsca;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    EditText qr_value;
    Button generate_btn, scan_btn;
    ImageView qr_image;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qr_value = findViewById(R.id.qr_input);
        generate_btn = findViewById(R.id.generate_btn);
        scan_btn = findViewById(R.id.scan_btn);
        qr_image = findViewById(R.id.qr_place_holder);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("QR_Codes");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String bitmap = ds.getValue().toString();
                    Log.d("Value_kya_hai", "onDataChange: " + bitmap);
                    Bitmap bitmap1 = StringToBitMap(bitmap);
                    qr_image.setImageBitmap(bitmap1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            }
        });

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data = qr_value.getText().toString().trim();
                if (!TextUtils.isEmpty(data)) {
                    QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
                    //qrgEncoder.setColorBlack(Color.RED);
                    //qrgEncoder.setColorWhite(Color.BLUE);

                    // Getting QR-Code as Bitmap
                    Bitmap bitmap = qrgEncoder.getBitmap();
                    Log.d("qrgencoder", "onClick: " + bitmap);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("QR_Codes");
                    String str = BitMapToString(bitmap);
                    ref.push().setValue(str);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // Setting Bqrgencoderitmap to ImageView
                    qr_image.setImageBitmap(bitmap);
                    Toast.makeText(MainActivity.this, "Generated Successfully ...", Toast.LENGTH_SHORT).show();
                } else {
                    qr_value.setError("Type a text ...");
                    qr_value.requestFocus();
                }
            }
        });
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] b = byteArray.toByteArray();
        String result = Base64.encodeToString(b, Base64.DEFAULT);
        return result;
    }

    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);

            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}