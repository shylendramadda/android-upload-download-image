package com.shylendra.androiduploadimagetoserver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.pavlospt.CircleView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class DownloadImageActivity extends AppCompatActivity {
    private static final String TAG = DownloadImageActivity.class.getSimpleName();
    private EditText urlInputField;
    private CircleView circularProgress;
    private final int REQUEST_WRITE_PERMISSION = 200;
    private File storageDirectory;
    private Bitmap bitmap;
    private long timeBeforeDownload;
    private long timeAfterDownload;
    private long fileSizeInBytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);
        setTitle("Download image");
        urlInputField = (EditText)findViewById(R.id.input_url);
        circularProgress = (CircleView) findViewById(R.id.circular_display);
        assert circularProgress != null;
        circularProgress.setTitleText("0 Kb/s");
        circularProgress.setSubtitleText("Download speed");
        Button downloadImageButton = (Button)findViewById(R.id.download_button);
        assert downloadImageButton != null;
        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = Helper.getUrl(urlInputField);
                boolean isValid = Helper.isTextNullOrEmpty(newUrl);
                if(isValid){
                    Helper.displayNoticeMessage(DownloadImageActivity.this, getResources().getString(R.string.download_input_error));
                    return;
                }
                if(urlInputField.getText().toString().length() < 15){
                    Helper.displayNoticeMessage(DownloadImageActivity.this, getResources().getString(R.string.url_error));
                    return;
                }
                timeBeforeDownload = System.currentTimeMillis();
                returnDownloadedImageAsBitmap(newUrl);
            }
        });
    }
    private void returnDownloadedImageAsBitmap(String pathToImageDownload){
        Glide.with(DownloadImageActivity.this).load(pathToImageDownload).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bitmap = resource;
                saveBitmapToExternalStorage();
                timeAfterDownload = System.currentTimeMillis();
                double timeDiff = (double) ((timeAfterDownload - timeBeforeDownload) / 1000);
                double fileSizeInKiloByte = (double)(fileSizeInBytes / 1024);
                double fileUploadRate = (timeDiff / fileSizeInKiloByte);
                String result = String.format("%.2f", fileUploadRate);
                circularProgress.setTitleText(result + "Kb/s");
                circularProgress.setSubtitleText("Download speed");
            }
        });
    }
    private void saveBitmapToExternalStorage() {
        if(!Helper.isExternalStorageWritable() && !Helper.isExternalStorageReadable()){
            Toast.makeText(DownloadImageActivity.this, "There is no external or writable storage in your device", Toast.LENGTH_LONG).show();
            return;
        }
        storageDirectory = new File(Helper.PATH_TO_EXTERNAL_STORAGE);
        if (!storageDirectory.exists()) {
            Log.i(TAG, "Storage directory has been created");
            storageDirectory.mkdir();
        }
        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DownloadImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            return;
        }
        createAndStoreFileInExternalStorage();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(DownloadImageActivity.this, "Sorry!!!, you can't use this app without granting this permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        storageDirectory = new File(Helper.PATH_TO_EXTERNAL_STORAGE);
        if(!storageDirectory.exists()){
            storageDirectory.mkdir();
        }
        if(null == storageDirectory.list() && null != bitmap){
            createAndStoreFileInExternalStorage();
        }
    }
    private void createAndStoreFileInExternalStorage(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
        String timeStamp = dateFormat.format(new Date());
        String filename = "image-" + timeStamp + ".jpg";
        File createNewFile = new File(storageDirectory, filename);
        if (createNewFile.exists()) {
            createNewFile.delete();
        }
        try {
            createNewFile.createNewFile();
            fileSizeInBytes = createNewFile.length();
            FileOutputStream outStream = new FileOutputStream(createNewFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}