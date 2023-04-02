package com.example.btp_prop1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerDoctorListAdapter extends RecyclerView.Adapter<RecyclerDoctorListAdapter.viewHolder> {

    Context context;
    ArrayList<contactModel> doctorList;
    RecyclerDoctorListAdapter(Context context, ArrayList<contactModel> doctorList)
    {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_list_for_fragment_clock,parent,false);
        viewHolder viewHolder = new viewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.imageDoctor.setImageResource(doctorList.get(position).image);
        holder.txtname.setText(doctorList.get(position).name);
        holder.txtspeciality.setText(doctorList.get(position).speciality);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        TextView txtname,txtspeciality;
        ImageView imageDoctor;
        public viewHolder(@NonNull View itemView)
        {
            super(itemView);
            txtname = itemView.findViewById(R.id.name_doctor_list_card);
            txtspeciality = itemView.findViewById(R.id.speciality_docto_list_card);
            imageDoctor = itemView.findViewById(R.id.doctor_image_doctor_list_card);

        }
    }
}
