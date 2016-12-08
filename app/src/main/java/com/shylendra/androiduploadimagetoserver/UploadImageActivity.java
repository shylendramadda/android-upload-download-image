package com.shylendra.androiduploadimagetoserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.pavlospt.CircleView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
public class UploadImageActivity extends AppCompatActivity {
    private CircleView circularProgress;
    private final int REQUEST_IMAGE_FROM_GALLERY = 200;
    private ProgressDialog progressDialog;
    private String encodedString;
    private String filename;
    private ImageView imageToUpload;
    private RequestQueue queue;
    private ServerImageObject imageObject;
    private long timeBeforeUpload;
    private long timeAfterUpload;
    private long fileSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        setTitle("Upload image");
        queue = Volley.newRequestQueue(this);
        circularProgress = (CircleView) findViewById(R.id.circular_display);
        assert circularProgress != null;
        circularProgress.setTitleText("0 Kb/s");
        circularProgress.setSubtitleText("Upload speed");
        imageToUpload = (ImageView) findViewById(R.id.image_to_upload);
        Button uploadImageButton = (Button) findViewById(R.id.upload_button);
        assert uploadImageButton != null;
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromDeviceGallery();
            }
        });
    }
    private void pickImageFromDeviceGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_FROM_GALLERY && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            String fileNameSegments[] = picturePath.split("/");
            filename = fileNameSegments[fileNameSegments.length - 1];
            Bitmap myImg = BitmapFactory.decodeFile(picturePath);
            imageToUpload.setImageBitmap(myImg);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            fileSize = byte_arr.length;
            encodedString = Base64.encodeToString(byte_arr, 0);
            timeBeforeUpload = System.currentTimeMillis();
            uploadSelectedImageToServer();
        }
    }
    private void uploadSelectedImageToServer() {
        // make a post request to the server
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(UploadImageActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                imageObject = gson.fromJson(response, ServerImageObject.class);
                if (null == imageObject) {
                    Toast.makeText(UploadImageActivity.this, "Something went wrong and file was not uploaded in the server", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (imageObject.getSuccess().equals("0")) {
                        // something went wrong
                        Toast.makeText(UploadImageActivity.this, "Something went wrong and file was not uploaded in the server", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        //Successful upload
                        timeAfterUpload = System.currentTimeMillis();
                        double timeDifferenceInSeconds = (double)(timeAfterUpload - timeBeforeUpload) / 1000;
                        double fileSizeInKiloByte = (double)(fileSize / 1024);
                        double fileUploadRate = (timeDifferenceInSeconds / fileSizeInKiloByte);
                        String result = String.format("%.2f", fileUploadRate);
                        circularProgress.setTitleText(result + "Kb/s");
                        circularProgress.setSubtitleText("Upload speed");
                        Toast.makeText(UploadImageActivity.this, "Your image was successfully uploaded to the server", Toast.LENGTH_LONG).show();
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadImageActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Helper.IMAGE_STRING, encodedString);
                params.put(Helper.IMAGE_FILENAME, filename);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }
}