package com.sinamekidev.fakecallerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sinamekidev.fakecallerapplication.adapter.RVAdapter;
import com.sinamekidev.fakecallerapplication.database.Person;
import com.sinamekidev.fakecallerapplication.database.PersonDAO;
import com.sinamekidev.fakecallerapplication.database.PersonDatabase;
import com.sinamekidev.fakecallerapplication.databinding.ActivityMainBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PersonDatabase database;
    private PersonDAO personDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = Room.databaseBuilder(this,PersonDatabase.class,"Person").build();
        personDAO = database.getPersonDAO();
        compositeDisposable.add(personDAO.getAll()
                .subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(this::postHandler));
    }
    private void postHandler(List<Person> personList){
        RVAdapter rvAdapter = new RVAdapter(personList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerView.setAdapter(rvAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_person){
            Intent intent = new Intent(this,AddPersonActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}