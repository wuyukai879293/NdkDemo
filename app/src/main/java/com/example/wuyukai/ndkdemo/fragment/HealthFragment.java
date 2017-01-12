package com.example.wuyukai.ndkdemo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.wuyukai.ndkdemo.MainActivity;
import com.example.wuyukai.ndkdemo.R;

/**
 * Created by wuyukai on 17/1/11.
 */
public class HealthFragment extends Fragment implements View.OnClickListener{
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment, container, false);
        view.findViewById(R.id.radio_common).setOnClickListener(this);
        view.findViewById(R.id.radio_picture).setOnClickListener(this);
        view.findViewById(R.id.radio_ppt).setOnClickListener(this);
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.radio_common:
                if (checked)
                    MainActivity.mUSER = MainActivity.modeUSER.GENERAL;
                // output.setText(mode);
                // Pirates are the best
                break;
            case R.id.radio_picture:
                if (checked)
                    MainActivity.mUSER = MainActivity.modeUSER.PICTURE;
                //output.setText(mode);
                // Ninjas rule
                break;
            case R.id.radio_ppt:
                if (checked)
                    MainActivity.mUSER = MainActivity.modeUSER.PPT;
                //output.setText(mode);
                break;
        }
    }

}
