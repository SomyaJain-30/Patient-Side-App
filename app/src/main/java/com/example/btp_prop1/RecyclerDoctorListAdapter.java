package com.example.btp_prop1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerDoctorListAdapter extends RecyclerView.Adapter<RecyclerDoctorListAdapter.viewHolder> {
    Context context;
    ArrayList<Doctor> arrContacts;
    RecyclerDoctorListAdapter(Context context, ArrayList<Doctor> arrContacts){
        this.context=context;
        this.arrContacts= arrContacts;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_list_card,parent,false);
        viewHolder viewHolder = new viewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.imageDoctor.setImageResource(arrContacts.get(position).image);
        holder.txtname.setText(arrContacts.get(position).getName());
        holder.txtspeciality.setText(arrContacts.get(position).getSpeciality());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, doctor_detail.class);
                i.putExtra("Name",arrContacts.get(holder.getLayoutPosition()).getName());
                i.putExtra("Specialization", arrContacts.get(holder.getLayoutPosition()).getSpeciality());
                i.putExtra("Experience", String.valueOf(arrContacts.get(holder.getLayoutPosition()).getExperience()));
                i.putExtra("Education", arrContacts.get(holder.getLayoutPosition()).getEducation());
                i.putExtra("Gender" , arrContacts.get(holder.getLayoutPosition()).getGender());
                i.putExtra("Clinic Address",arrContacts.get(holder.getLayoutPosition()).getAddress());
                i.putExtra("Contact", arrContacts.get(holder.getLayoutPosition()).getContact());
                i.putExtra("Slots", (Serializable) arrContacts.get(holder.getLayoutPosition()).getSlots());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrContacts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView txtname, txtspeciality;
        ImageView imageDoctor;
        public viewHolder(@NonNull View itemView){
            super(itemView);

            txtname = itemView.findViewById(R.id.name_doctor_list_card);
            txtspeciality= itemView.findViewById(R.id.speciality_docto_list_card);
            imageDoctor = itemView.findViewById(R.id.doctor_image_doctor_list_card);
        }
    }
}
