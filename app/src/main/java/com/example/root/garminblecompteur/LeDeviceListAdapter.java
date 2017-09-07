package com.example.root.garminblecompteur;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 03/08/17.
 */

class LeDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {




    public LeDeviceListAdapter(Listdevices context, List<BluetoothDevice> lvDevice) {
        super(context, 0, lvDevice);
    }

    @Override
    public void add(@Nullable BluetoothDevice object) {
        super.add(object);
    }

    @Override
    public void addAll(@NonNull Collection<? extends BluetoothDevice> collection) {
        super.addAll(collection);
    }

    @Override
    public void addAll(BluetoothDevice... items) {
        super.addAll(items);
    }

    @Override
    public void insert(@Nullable BluetoothDevice object, int index) {
        super.insert(object, index);
    }

    @Override
    public void remove(@Nullable BluetoothDevice object) {
        super.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void sort(@NonNull Comparator<? super BluetoothDevice> comparator) {
        super.sort(comparator);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        super.setNotifyOnChange(notifyOnChange);
    }

    @NonNull
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public BluetoothDevice getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable BluetoothDevice item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         //super.getView(position, convertView, parent);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.linearlisteview,parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.devicename = (TextView) convertView.findViewById(R.id.devicename);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<bluethoof> ble
        BluetoothDevice bluedevice = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.devicename.setText(bluedevice.getName());
        viewHolder.text.setText(String.valueOf(bluedevice.getAddress()));

        return convertView;
    }

    private class TweetViewHolder{
        public TextView devicename;
        public TextView text;
        public ImageView avatar;
    }





    @Override
    public void setDropDownViewResource(@LayoutRes int resource) {
        super.setDropDownViewResource(resource);
    }

    @Override
    public void setDropDownViewTheme(@Nullable Resources.Theme theme) {
        super.setDropDownViewTheme(theme);
    }

    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return super.getDropDownViewTheme();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}
