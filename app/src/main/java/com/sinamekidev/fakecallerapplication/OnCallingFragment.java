package com.sinamekidev.fakecallerapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinamekidev.fakecallerapplication.database.Person;
import com.sinamekidev.fakecallerapplication.databinding.OnCallingFragmentBinding;

public class OnCallingFragment extends Fragment {
    private OnCallingFragmentBinding binding;
    private Person person;
    private int second = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = OnCallingFragmentBinding.inflate(inflater,container,false);
        binding.callNameText.setText(person.person_name);
        Bitmap bitmap = BitmapFactory.decodeByteArray(person.image_blob,0,person.image_blob.length);
        binding.callPp.setImageBitmap(bitmap);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minute = second / 60;
                int sec = second - (minute * 60);
                String min_str;
                String sec_str;
                if(minute < 10){
                    min_str = "0" + String.valueOf(minute);
                }
                else{
                    min_str = String.valueOf(minute);
                }
                if(sec < 10){
                    sec_str = "0" + String.valueOf(sec);
                }
                else{
                    sec_str = String.valueOf(sec);
                }
                binding.timerText.setText(min_str + ":"+sec_str);
                second++;
                handler.postDelayed(this,1000);
            }
        });
        binding.hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacksAndMessages(null);
                CallingActivity callingActivity = (CallingActivity) getActivity();
                callingActivity.onDecline();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        person = new Person(getArguments().getString("person_name"),getArguments().getString("person_number"),getArguments().getByteArray("image_array"));
        super.onCreate(savedInstanceState);
    }
}
