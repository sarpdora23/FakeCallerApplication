package com.sinamekidev.fakecallerapplication.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Person {
    @PrimaryKey (autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "person_name")
    public String person_name;
    @ColumnInfo(name = "person_phone")
    public String person_phone;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image_blob;
    public Person(String person_name,String person_phone,byte[] image_blob){
        this.person_name = person_name;
        this.person_phone = person_phone;
        this.image_blob = image_blob;
    }
}
