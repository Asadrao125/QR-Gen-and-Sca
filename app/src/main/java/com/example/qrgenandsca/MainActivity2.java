package com.example.qrgenandsca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

public class MainActivity2 extends AppCompatActivity {
    private CodeScanner codeScanner;
    private CodeScannerView scanView;
    TextView tvResult;
    private AlertDialog alertDialog;
    String review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvResult = findViewById(R.id.tvResult);
        scanView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scanView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity2.this, "" + result.getText(), Toast.LENGTH_SHORT).show();

                        showDialog();

                        tvResult.setText(result.getText());

                    }
                });
            }
        });

    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View v = inflater.inflate(R.layout.dialog_rating, null);

        Button btnSubmit = v.findViewById(R.id.btnSubmit);
        EditText edtReview = v.findViewById(R.id.review);
        final RatingBar ratingBar = v.findViewById(R.id.rating);

        alertDialog = new AlertDialog.Builder(MainActivity2.this)
                .setView(v)
                .create();

        alertDialog.show();

        review = edtReview.getText().toString().trim();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("R_R");
                ref.child("review").setValue(review);
                ref.child("rating").setValue(ratingBar.getRating());
                alertDialog.cancel();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        codeScanner.stopPreview();
    }
}