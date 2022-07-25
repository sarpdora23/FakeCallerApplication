package com.sinamekidev.fakecallerapplication.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PersonDAO {
    @Query("SELECT * FROM Person")
    Single<List<Person>> getAll();
    @Insert
    Completable addPerson(Person person);
    @Delete
    Completable deletePerson(Person person);
}
