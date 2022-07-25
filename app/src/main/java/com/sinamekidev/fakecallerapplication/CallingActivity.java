package com.sinamekidev.fakecallerapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.sinamekidev.fakecallerapplication.database.Person;

public class CallingActivity extends AppCompatActivity {
    private Person person;
    private FragmentManager fragmentManager;
    private String person_name;
    private String person_number;
    private byte[] image_array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        this.person_name = getIntent().getStringExtra("person_name");
        this.person_number = getIntent().getStringExtra("person_number");
        this.image_array = getIntent().getByteArrayExtra("image_blob");
        Bundle bundle = new Bundle();
        bundle.putString("person_name",person_name);
        bundle.putString("person_number",person_number);
        bundle.putByteArray("image_array",image_array);
        this.fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CallingFragment callingFragment = new CallingFragment();
        callingFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.calling_frame,callingFragment);
        fragmentTransaction.commit();
    }
    public void onDecline(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void onAccept(){
        Bundle bundle = new Bundle();
        bundle.putString("person_name",person_name);
        bundle.putString("person_number",person_number);
        bundle.putByteArray("image_array",image_array);
        OnCallingFragment onCallingFragment = new OnCallingFragment();
        onCallingFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calling_frame,onCallingFragment);
        fragmentTransaction.commit();
    }
}