package com.shylendra.androiduploadimagetoserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downloadImageButton = (Button)findViewById(R.id.download_image);
        Button uploadImageButton = (Button)findViewById(R.id.upload_image);
        Button listImageButton = (Button)findViewById(R.id.list_image);
        assert downloadImageButton != null;
        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent downloadIntent = new Intent(MainActivity.this, DownloadImageActivity.class);
                startActivity(downloadIntent);
            }
        });
        assert uploadImageButton != null;
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImageIntent = new Intent(MainActivity.this, UploadImageActivity.class);
                startActivity(uploadImageIntent);
            }
        });
        assert listImageButton != null;
        listImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listImageIntent = new Intent(MainActivity.this, ListImagesActivity.class);
                startActivity(listImageIntent);
            }
        });
    }
}
