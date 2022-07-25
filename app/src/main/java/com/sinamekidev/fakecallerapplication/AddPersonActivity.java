package com.sinamekidev.fakecallerapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.sinamekidev.fakecallerapplication.database.Person;
import com.sinamekidev.fakecallerapplication.database.PersonDAO;
import com.sinamekidev.fakecallerapplication.database.PersonDatabase;
import com.sinamekidev.fakecallerapplication.databinding.ActivityAddPersonBinding;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddPersonActivity extends AppCompatActivity {
    private ActivityAddPersonBinding binding;
    private ActivityResultLauncher<String> permisionLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private PersonDAO personDAO;
    private PersonDatabase personDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registar();
        personDatabase = Room.databaseBuilder(this,PersonDatabase.class,"Person").build();
        personDAO = personDatabase.getPersonDAO();
    }
    public void onImageSelection(View view){
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for galery", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction(
                        "Allow", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }
                ).show();
            }
            else{
                permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        }
    }
    private void registar(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent imageIntent = result.getData();
                    Uri imageUri = imageIntent.getData();
                    binding.personImageView.setImageURI(imageUri);
                }
            }
        });
        permisionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }
                else{
                    Toast.makeText(AddPersonActivity.this,"Permission Denied!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onCreateButton(View view){
        if(binding.personImageView.getDrawable() != getDrawable(R.drawable.default_image)){
            String personName = binding.personNameView.getText().toString();
            String personNumber = binding.phoneNumberView.getText().toString();
            if(personNumber.isEmpty() || personName.isEmpty()){
                Toast.makeText(this,"Person name and number can't be empty",Toast.LENGTH_LONG).show();
            }
            else{
                Bitmap bitmap = ((BitmapDrawable) binding.personImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] image_blob = baos.toByteArray();
                Person person = new Person(personName,personNumber,image_blob);
                compositeDisposable.add(personDAO.addPerson(person)
                        .subscribeOn(Schedulers.io()).subscribe(this::postHandler));
            }
        }
        else{
            Toast.makeText(this,"SELECT IMAGE",Toast.LENGTH_LONG).show();
        }
    }
    private void postHandler(){
        Intent intent = new Intent(AddPersonActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}