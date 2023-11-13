package com.example.btp_prop1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeAppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Appointments> appointmentsList;
    SimpleDateFormat inputFormat;
    SimpleDateFormat outputFormat;
    FirebaseFirestore firebaseFirestore;

    public HomeAppointmentsAdapter(Context context, List<Appointments> appointmentsList)
    {
        this.appointmentsList = appointmentsList;
        this.context = context;
        inputFormat = new SimpleDateFormat("dd/MM/yy");
        outputFormat = new SimpleDateFormat("MMM");
        firebaseFirestore = FirebaseFirestore.getInstance();
        Collections.sort(appointmentsList, new Comparator<Appointments>() {
            @Override
            public int compare(Appointments appointment1, Appointments appointment2) {
                String dateStr1 = appointment1.getDate();
                String dateStr2 = appointment2.getDate();

                try {
                    Date date1 = inputFormat.parse(dateStr1);
                    Date date2 = inputFormat.parse(dateStr2);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0; // Handle the exception as needed
                }
            }
        });
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_card_home, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (position < 3) {
//            holder.itemView.setVisibility(View.VISIBLE);
//        } else {
//            holder.itemView.setVisibility(View.GONE);
//        }

        Appointments apps = appointmentsList.get(holder.getAdapterPosition());
        ViewHolder holder1 =  ((ViewHolder)holder);

        firebaseFirestore.collection("Doctors").document(apps.getDid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                apps.setDoctorName(documentSnapshot.get("Name").toString());
                holder1.name.setText(apps.getDoctorName());
            }
        });
        try {
            Date date = inputFormat.parse(apps.getDate());
            holder1.month.setText(outputFormat.format(date));
            holder1.date.setText(String.valueOf(date.getDate()));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder1.time.setText(convertTo12HourFormat(apps.getTimeslot()));

        if(apps.getStatus().equals("Requested"))
        {
            holder1.status.setImageResource(R.drawable.baseline_av_timer_24);
        }
        else if(apps.getStatus().equals("Confirmed"))
        {
            holder1.status.setImageResource(R.drawable.baseline_task_alt_24);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(3, appointmentsList.size());
    }

    public void addAppointments(Appointments apps)
    {
        appointmentsList.add(apps);
        notifyDataSetChanged();

    }

    private static String convertTo12HourFormat(String time) {
        String convertedTime = "";

        DateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(time);
            convertedTime = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedTime;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView date, month, name, time;
        ImageView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.timeCard).findViewById(R.id.date);
            month = itemView.findViewById(R.id.timeCard).findViewById(R.id.month);
            name = itemView.findViewById(R.id.nameDoctor);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
        }
    }
}
