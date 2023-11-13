package com.example.btp_prop1;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HomeDoctorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Doctor> itemList; // Replace with your data type
    Context context;

    public HomeDoctorsAdapter(Context context, List<Doctor> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_home_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        MyViewHolder holder1 = (MyViewHolder) holder;
        holder1.imageDoctor.setImageResource(itemList.get(position).image);
        holder1.txtname.setText(itemList.get(position).getName());
        holder1.txtRole.setText(itemList.get(position).getRole());
        holder1.txtspeciality.setText(String.join(", ", itemList.get(position).getSpecialization()));
        holder1.abt.setText(itemList.get(position).getAbout());
        Glide.with(context)
                .load(Uri.parse(itemList.get(position).getProfileUrl()))
                .apply(RequestOptions.circleCropTransform())
                .into(holder1.imageDoctor);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addAtBeginning(Doctor doctor) {
        itemList.add(doctor);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtname, txtspeciality, txtRole, abt;
        ImageView imageDoctor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtname = itemView.findViewById(R.id.name_doctor_list_card);
            txtRole = itemView.findViewById(R.id.Role);
            txtspeciality = itemView.findViewById(R.id.speciality_docto_list_card);
            imageDoctor = itemView.findViewById(R.id.doctor_image_doctor_list_card);
            abt = itemView.findViewById(R.id.about);
            //textView = itemView.findViewById(R.id.text_view); // Replace with your view ID
        }
    }
}
