package com.shylendra.androiduploadimagetoserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class ListImagesActivity extends AppCompatActivity {
    private ListImageAdapter listImageAdapter;
    private ListView listAllImages;
    private List<EntityObject> workingDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_images);
        setTitle("List and delete images");
        listAllImages = (ListView) findViewById(R.id.list_all_images);
        TextView noImageFound = (TextView) findViewById(R.id.no_image);
        File mainStorageDirectory = new File(Helper.PATH_TO_EXTERNAL_STORAGE);
        if (mainStorageDirectory.length() > 0) {
            assert noImageFound != null;
            noImageFound.setVisibility(View.GONE);
            assert listAllImages != null;
            listAllImages.setVisibility(View.VISIBLE);
            /// add an adapter for the listView
            workingDataSource = returnDataSource(mainStorageDirectory);
            listImageAdapter = new ListImageAdapter(ListImagesActivity.this, workingDataSource);
            listAllImages.setAdapter(listImageAdapter);
            listImageAdapter.refreshDataStorage(workingDataSource);
        }
        else{
            assert noImageFound != null;
            noImageFound.setVisibility(View.VISIBLE);
            assert listAllImages != null;
            listAllImages.setVisibility(View.GONE);
            noImageFound.setText("There is no image in the storage folder. You can start by downloading an image");
        }
    }
    private List<EntityObject> returnDataSource(File mainStorageDirectory) {
        List<String> storeFilenamesInString = new ArrayList<String>();
        String[] allFilesInStringFormat = mainStorageDirectory.list();
        for (String filename : allFilesInStringFormat) {
            System.out.println("Stored image url " + filename);
            if (filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
                int lastPosition = filename.lastIndexOf("/");
                String justFilename = filename.substring(lastPosition + 1, filename.length());
                storeFilenamesInString.add(justFilename);
            }
        }
        // create the data source object
        List<EntityObject> mainDataSource = new ArrayList<EntityObject>();
        for (int i = 0; i < allFilesInStringFormat.length; i++) {
            mainDataSource.add(new EntityObject(allFilesInStringFormat[i], storeFilenamesInString.get(i)));
        }
        return mainDataSource;
    }
}