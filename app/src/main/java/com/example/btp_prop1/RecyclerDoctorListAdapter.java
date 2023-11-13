package com.example.btp_prop1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecyclerDoctorListAdapter extends RecyclerView.Adapter<RecyclerDoctorListAdapter.viewHolder> {
    Context context;
    ArrayList<Doctor> arrContacts;

    private Integer selectedExperience;
    private Integer selectedGender;
    private List<String> selectedRoles;
    private List<String> selectedLanguages;
    private List<Doctor> filteredList;
    RecyclerDoctorListAdapter(Context context, ArrayList<Doctor> arrContacts){
        this.context=context;
        this.arrContacts= arrContacts;
        filteredList = new ArrayList<>(arrContacts);
    }

    void addAtBeginning(Doctor doctor)
    {
        arrContacts.add(doctor);
        filteredList.add(doctor);
        notifyDataSetChanged();
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
        position = holder.getAdapterPosition();
        holder.imageDoctor.setImageResource(filteredList.get(position).image);
        holder.txtname.setText(filteredList.get(position).getName());
        holder.txtspeciality.setText(filteredList.get(position).getRole());
        holder.txtLanguage.setText(String.join(", ", filteredList.get(position).getLanguages()));
        Glide.with(context)
                .load(Uri.parse(filteredList.get(position).getProfileUrl()))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageDoctor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, doctor_detail.class);
                i.putExtra("Did", filteredList.get(holder.getLayoutPosition()).getContact());
                i.putExtra("Name",filteredList.get(holder.getLayoutPosition()).getName());
                i.putExtra("Specialization", filteredList.get(holder.getLayoutPosition()).getSpeciality());
                i.putExtra("Profile URL", filteredList.get(holder.getLayoutPosition()).getProfileUrl());
                i.putExtra("Role", filteredList.get(holder.getLayoutPosition()).getRole());
                i.putExtra("Experience", String.valueOf(filteredList.get(holder.getLayoutPosition()).getExperience()));
                i.putExtra("Education", filteredList.get(holder.getLayoutPosition()).getEducation());
                i.putExtra("Gender" , filteredList.get(holder.getLayoutPosition()).getGender());
                i.putExtra("About",filteredList.get(holder.getLayoutPosition()).getAbout());
                i.putExtra("Languages", String.join(", ", filteredList.get(holder.getLayoutPosition()).getLanguages()));
                i.putExtra("Slots", (Serializable) filteredList.get(holder.getLayoutPosition()).getSlots());
                i.putExtra("Proforma", RecommendedDoctors.pdfUri);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(Integer experience, Integer gender, List<String> roles, List<String> languages) {
        selectedExperience = experience;
        selectedGender = gender;
        selectedRoles = roles;
        selectedLanguages = languages;
        System.out.println(selectedExperience + " " + selectedGender + " " + selectedRoles + " " + selectedLanguages );
        filteredList.clear();

        for (Doctor model : arrContacts) {
            // Apply filters based on selected values
            if ((selectedExperience == -1 || checkExperience(model)) &&
                    (selectedGender == -1 || checkGender(model)) &&
                    (selectedRoles.isEmpty() || selectedRoles.contains(model.getRole())) &&
                    (selectedLanguages.isEmpty() || containsCommonLanguage(selectedLanguages,model.getLanguages()))){
                filteredList.add(model);
            }
        }

        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    private boolean containsCommonLanguage(List<String> modelLanguages, List<String> selectedLanguages) {
        for (String language : modelLanguages) {
            if (selectedLanguages.contains(language)) {
                return true;
            }
        }
        return false;
    }

    boolean checkExperience(Doctor model)
    {
        Integer exp = Integer.parseInt(model.getExperience());

        return (exp<5 && selectedExperience==0) || (exp>=5 && exp<10 && selectedExperience==1)
                || (exp>=10 && exp<20 && selectedExperience==2) || (exp>=20 && selectedExperience==3);
    }

    boolean checkGender(Doctor model)
    {
        String gen = model.getGender();
        return (gen.equals("Male") && selectedGender==0) || (gen.equals("Female") && selectedGender==1)
                || (gen.equals("Other") && selectedGender==2);
    }


    public class viewHolder extends RecyclerView.ViewHolder{
        TextView txtname, txtspeciality, txtLanguage;
        ImageView imageDoctor;
        public viewHolder(@NonNull View itemView){
            super(itemView);

            txtname = itemView.findViewById(R.id.name_doctor_list_card);
            txtspeciality= itemView.findViewById(R.id.speciality_docto_list_card);
            txtLanguage = itemView.findViewById(R.id.language_docto_list_card);
            imageDoctor = itemView.findViewById(R.id.doctor_image_doctor_list_card);
        }
    }
}
