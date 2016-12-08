package com.shylendra.androiduploadimagetoserver;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;
public class ListImageAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<EntityObject> dataStorage;
    private Context context;

    public ListImageAdapter(Context context, List<EntityObject> customizedListView) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.dataStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return dataStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return dataStorage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_images, parent, false);
            listViewHolder.downloadedImage = (ImageView) convertView.findViewById(R.id.displayed_image);
            listViewHolder.deleteIcon = (ImageView) convertView.findViewById(R.id.delete_image);
            listViewHolder.imageFilename = (TextView) convertView.findViewById(R.id.downloaded_filename);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        final EntityObject mObject = dataStorage.get(position);
        listViewHolder.imageFilename.setText(dataStorage.get(position).getName());
        Uri imageUri = Uri.fromFile(new File(Helper.PATH_TO_EXTERNAL_STORAGE + File.separator + dataStorage.get(position).getImage()));
        Glide.with(context).load(imageUri).override(90, 90).centerCrop().into(listViewHolder.downloadedImage);
        listViewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteImageName = listViewHolder.imageFilename.getText().toString();
                String deletePath = Helper.PATH_TO_EXTERNAL_STORAGE + File.separator + deleteImageName;
                File deleteFile = new File(deletePath);
                if (deleteFile.exists()) {
                    deleteFile.delete();
                    Toast.makeText(context, "The image has been deleted", Toast.LENGTH_LONG).show();
                    dataStorage.remove(mObject);
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }

    public void refreshDataStorage(List<EntityObject> listObject) {
        this.dataStorage = listObject;
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView downloadedImage;
        ImageView deleteIcon;
        TextView imageFilename;
    }
}