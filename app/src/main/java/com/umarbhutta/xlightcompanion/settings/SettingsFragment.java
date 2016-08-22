package com.umarbhutta.xlightcompanion.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umarbhutta.xlightcompanion.particle.ParticleBridge;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

import java.util.ArrayList;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * Created by Umar Bhutta.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private Switch powerSwitch;
    private SeekBar brightnessSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;
    private LinearLayout scenarioNoneLL;
    private ToggleButton ring1Button, ring2Button, ring3Button;
    private TextView deviceRingLabel;

    private ArrayList<String> scenarioDropdown;

    private String colorHex;
    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);

        scenarioDropdown = new ArrayList<>(ScenarioFragment.name);
        scenarioDropdown.add(0, "None");

        powerSwitch = (Switch) view.findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) view.findViewById(R.id.brightnessSeekBar);
        colorTextView = (TextView) view.findViewById(R.id.colorTextView);
        scenarioNoneLL = (LinearLayout) view.findViewById(R.id.scenarioNoneLL);
        scenarioNoneLL.setAlpha(1);
        ring1Button = (ToggleButton) view.findViewById(R.id.ring1Button);
        ring2Button = (ToggleButton) view.findViewById(R.id.ring2Button);
        ring3Button = (ToggleButton) view.findViewById(R.id.ring3Button);
        deviceRingLabel = (TextView) view.findViewById(R.id.deviceRingLabel);

        scenarioSpinner = (Spinner) view.findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(getActivity(), R.layout.control_scenario_spinner_item, scenarioDropdown);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check if on or off
                state = isChecked;
                ParticleBridge.JSONCommandPower(ParticleBridge.DEFAULT_DEVICE_ID, state);
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(int color) {
                                Log.e(TAG, "int: " + color);
                                colorHex = String.format("%06X", (0xFFFFFF & color));
                                Log.e(TAG, "HEX: #" + colorHex);

                                int cw = 0;
                                int ww = 0;
                                int c = (int)Long.parseLong(colorHex, 16);
                                int r = (c >> 16) & 0xFF;
                                int g = (c >> 8) & 0xFF;
                                int b = (c >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);

                                colorHex = "#" + colorHex;
                                colorTextView.setText(colorHex);
                                colorTextView.setTextColor(Color.parseColor(colorHex));

                                //send message to Particle based on which rings have been selected
                                if ((ring1 && ring2 && ring3) || (!ring1 && !ring2 && !ring3))
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_ALL, state, cw, ww, r, g, b);
                                }
                                else if (ring1 && ring2)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                }
                                else if (ring2 && ring3)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                }
                                else if (ring1 && ring3)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                }
                                else if (ring1)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                }
                                else if (ring2)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                }
                                else if (ring3)
                                {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                }
                                else
                                {
                                    //do nothing
                                }
                            }
                        })
                        .create()
                        .show(getFragmentManager(), "dialog");
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "The brightness value is " + seekBar.getProgress());
                ParticleBridge.JSONCommandBrightness(ParticleBridge.DEFAULT_DEVICE_ID, seekBar.getProgress());
            }
        });

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString() == "None") {
                    scenarioNoneLL.animate().alpha(1).setDuration(600).start();
                } else {
                    //if anything but "None" is selected, fade scenarioNoneLL out
                    scenarioNoneLL.animate().alpha(0).setDuration(500).start();

                    ParticleBridge.JSONCommandScenario(ParticleBridge.DEFAULT_DEVICE_ID, position);
                    //position passed into above function corresponds to the scenarioId i.e. s1, s2, s3 to trigger
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ring1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring1 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring2 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring3Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring3 = isChecked;
                updateDeviceRingLabel();
            }
        });

        return view;
    }

    private void updateDeviceRingLabel() {
        String label = ParticleBridge.DEFAULT_LAMP_TEXT;

        if ((ring1 && ring2 && ring3) || (!ring1 && !ring2 && !ring3))
        {
            label += ": " + ParticleBridge.RINGALL_TEXT;
        }
        else if (ring1 && ring2)
        {
            label += ": " + ParticleBridge.RING1_TEXT + " & " + ParticleBridge.RING2_TEXT;
        }
        else if (ring2 && ring3)
        {
            label += ": " + ParticleBridge.RING2_TEXT + " & " + ParticleBridge.RING3_TEXT;
        }
        else if (ring1 && ring3)
        {
            label += ": " + ParticleBridge.RING1_TEXT + " & " + ParticleBridge.RING3_TEXT;
        }
        else if (ring1)
        {
            label += ": " + ParticleBridge.RING1_TEXT ;
        }
        else if (ring2)
        {
            label += ": " + ParticleBridge.RING2_TEXT ;
        }
        else if (ring3)
        {
            label += ": " + ParticleBridge.RING3_TEXT ;
        }
        else
        {
            label += "";
        }

        deviceRingLabel.setText(label);
    }
}