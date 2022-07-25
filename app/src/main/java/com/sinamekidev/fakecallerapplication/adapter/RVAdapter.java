package com.sinamekidev.fakecallerapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.sinamekidev.fakecallerapplication.AddPersonActivity;
import com.sinamekidev.fakecallerapplication.CallingActivity;
import com.sinamekidev.fakecallerapplication.MainActivity;
import com.sinamekidev.fakecallerapplication.R;
import com.sinamekidev.fakecallerapplication.database.Person;
import com.sinamekidev.fakecallerapplication.database.PersonDAO;
import com.sinamekidev.fakecallerapplication.database.PersonDatabase;
import com.sinamekidev.fakecallerapplication.databinding.RowLayoutBinding;

import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
    private List<Person> personList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PersonDatabase personDatabase;
    private PersonDAO personDAO;
    public RVAdapter(List<Person> personList){
        this.personList = personList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowLayoutBinding rowLayoutBinding = RowLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(rowLayoutBinding);
        personDatabase = Room.databaseBuilder(parent.getContext(),PersonDatabase.class,"Person").build();
        personDAO = personDatabase.getPersonDAO();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(position >= personList.size()){
            holder.binding.nameText.setText("Create Person");
            holder.binding.numberText.setText("");
            holder.binding.imageView.setImageResource(R.drawable.writing);
            holder.binding.nameText.setTextSize(26);
        }
        else{
            holder.binding.nameText.setText(personList.get(position).person_name);
            holder.binding.numberText.setText(personList.get(position).person_phone);
            byte[] image_byte_array = personList.get(position).image_blob;
            Bitmap image_bitmap = BitmapFactory.decodeByteArray(image_byte_array,0,image_byte_array.length);
            holder.binding.imageView.setImageBitmap(image_bitmap);
        }
        int temp_pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nameText = view.findViewById(R.id.nameText);
                TextView numberText = view.findViewById(R.id.numberText);
                if(nameText.getText().toString().equals("Create Person") && numberText.getText().toString().equals("")){
                    Intent intent = new Intent(view.getContext(), AddPersonActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intent);
                }
                else{
                    Person person = personList.get(temp_pos);
                    getCall(view.getContext(),person);
                }
            }
        });
        if(holder.binding.nameText.getText().toString().equals("Create Person") && holder.binding.numberText.getText().toString().equals("")){
            holder.itemView.setLongClickable(false);
        }
        else{
            int temp_position = position;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Person selectedPerson = personList.get(temp_position);
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(menuItem.getItemId() == R.id.delete_item){
                                compositeDisposable.add(personDAO.deletePerson(selectedPerson)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(RVAdapter.this::postHandler));
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                view.getContext().startActivity(intent);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return false;
                }
            });
        }
    }
    private void getCall(Context context,Person person)  {
        Intent intent = new Intent(context, CallingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("person_name",person.person_name);
        intent.putExtra("person_number",person.person_phone);
        intent.putExtra("image_blob",person.image_blob);
        context.startActivity(intent);
    }
    private void postHandler(){
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return personList.size() + 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowLayoutBinding binding;
        public MyViewHolder(@NonNull RowLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
