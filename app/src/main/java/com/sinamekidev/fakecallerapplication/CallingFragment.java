package com.sinamekidev.fakecallerapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinamekidev.fakecallerapplication.CallingActivity;
import com.sinamekidev.fakecallerapplication.R;
import com.sinamekidev.fakecallerapplication.database.Person;
import com.sinamekidev.fakecallerapplication.databinding.CallingFragmentBinding;

public class CallingFragment extends Fragment {
    private Person person;
    private CallingFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CallingFragmentBinding.inflate(inflater,container,false);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                handler.postDelayed(this,2000);
            }
        });

        Ringtone ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(),notification);
        ringtone.play();
        binding.callingNameText.setText(person.person_name);
        binding.callingNumberText.setText(person.person_phone);
        binding.callingImage.setImageBitmap(BitmapFactory.decodeByteArray(person.image_blob,0,person.image_blob.length));
        binding.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallingActivity activity = (CallingActivity) getActivity();
                activity.onDecline();
                ringtone.stop();
                handler.removeCallbacksAndMessages(null);
            }

        });
        binding.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallingActivity activity = (CallingActivity) getActivity();
                activity.onAccept();
                ringtone.stop();
                handler.removeCallbacksAndMessages(null);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        person = new Person(getArguments().getString("person_name"),getArguments().getString("person_number"), getArguments().getByteArray("image_array"));

    }
}
