package com.umarbhutta.xlightcompanion.control;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.SDK.ParticleBridge;
import com.umarbhutta.xlightcompanion.R;

/**
 * Created by Umar Bhutta.
 */
public class DevicesListAdapter extends RecyclerView.Adapter {

    private Handler m_handlerDeviceList;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item, parent, false);
        return new DevicesListViewHolder(view);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if( m_handlerDeviceList != null ) {
            MainActivity.m_mainDevice.removeDeviceEventHandler(m_handlerDeviceList);
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DevicesListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    private class DevicesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDeviceName;
        private Switch mDeviceSwitch;

        public DevicesListViewHolder(View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView.findViewById(R.id.deviceName);
            mDeviceSwitch = (Switch) itemView.findViewById(R.id.deviceSwitch);

            itemView.setOnClickListener(this);

            mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //ParticleBridge.FastCallPowerSwitch(ParticleBridge.DEFAULT_DEVICE_ID, isChecked);
                    MainActivity.m_mainDevice.PowerSwitch(isChecked);
                }
            });
        }

        public void bindView (int position) {
            mDeviceName.setText(MainActivity.deviceNames[position]);
            if (position == 0) {
                // Main device
                mDeviceSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
                m_handlerDeviceList = new Handler() {
                    public void handleMessage(Message msg) {
                        int intValue =  msg.getData().getInt("State", -255);
                        if( intValue != -255 ) {
                            mDeviceSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
                        }
                    }
                };
                MainActivity.m_mainDevice.addDeviceEventHandler(m_handlerDeviceList);
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
