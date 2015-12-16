package com.promlert.qrcodegenerator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText urlEditText = (EditText) findViewById(R.id.url_edit_text);
        final ImageView qrCodeImageView = (ImageView) findViewById(R.id.qr_code_image);

        Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = urlEditText.getText().toString();
                Bitmap qrCodeBitmap = QRCode.from(urlString).withSize(500, 500).bitmap();
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            }
        });
    }
}
