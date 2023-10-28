package com.example.btp_prop1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IT_IS_DATE = 0;
    private static final int IT_IS_APPOINTMENT = 1;
    List<Appointments> appointmentsList;
    List<Object> date_app;
    Context context;
    FragmentActivity activity;
    Fragment parentFragment;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String tab;
    public AppointmentCardAdapter(List<Appointments> appointmentsList, Context context, FragmentActivity activity, Fragment parentFragment, String tab) {
        this.appointmentsList = appointmentsList;
        this.context = context;
        this.activity = activity;
        this.parentFragment = parentFragment;
        this.tab = tab;
        date_app = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        date_app = makeArrayList(appointmentsList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==IT_IS_APPOINTMENT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_card,parent,false);
            return new AppointmentCardAdapter.RequestedCardViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_date_day, parent, false);
            return new AppointmentCardAdapter.viewHolder2(view);
        }
    }


    private ArrayList<Object> makeArrayList(List<Appointments> Appointments) {
        ArrayList<Object> date_app = new ArrayList<>();

        Collections.sort(Appointments, new Comparator<Appointments>() {
            @Override
            public int compare(Appointments o1, Appointments o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);
                try {
                    Date dateTime1 = sdf.parse(o1.getDate() + " " + o1.getTimeslot());
                    Date dateTime2 = sdf.parse(o2.getDate() + " " + o2.getTimeslot());
                    int dateComparison = dateTime1.compareTo(dateTime2);

                    if (dateComparison == 0) {
                        // Dates are the same, compare based on time
                        return o1.getTimeslot().compareTo(o2.getTimeslot());
                    } else {
                        return dateComparison;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });


        String currentDate = null;
        for(Appointments ad: Appointments)
        {
            if(!ad.getDate().equals(currentDate))
            {
                date_app.add(ad.getDate() + ", " + ad.getDay());
                currentDate = ad.getDate();
            }
            date_app.add(ad);
        }
        System.out.println(date_app.size());
        return date_app;
    }

    @Override
    public int getItemViewType(int position) {
        if(date_app.get(position) instanceof String)
            return IT_IS_DATE;
        else
            return IT_IS_APPOINTMENT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(holder instanceof RequestedCardViewHolder)
        {
            RequestedCardViewHolder  requestedCardViewHolder = (RequestedCardViewHolder) holder;
            firebaseFirestore.collection("Doctors").document(((Appointments)date_app.get(position)).getDid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Glide.with(context).load(documentSnapshot.get("Profile URL")).into(((RequestedCardViewHolder) holder).imageView);
                    ((Appointments)date_app.get(position)).setDoctorName(documentSnapshot.get("Name").toString());
                    requestedCardViewHolder.doctorname.setText(((Appointments)date_app.get(position)).getDoctorName());
                    String str = convertTo12HourFormat(((Appointments)date_app.get(position)).getTimeslot());
                    requestedCardViewHolder.appointmentTiming.setText(str);
                }
            });

            requestedCardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tab.equals("confirmed"))
                    {
                        makeVideoCallDialog(requestedCardViewHolder);
                    }
                }
            });
        }
        else
        {
            viewHolder2 hold = (viewHolder2) holder;
            hold.tv.setText((String)date_app.get(position));
        }
    }

    public void makeVideoCallDialog(RequestedCardViewHolder holder)
    {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View dialogView = inflater.inflate(R.layout.call_dialog, null);

        TextView name, dateDayTime;
        name = dialogView.findViewById(R.id.accept_reject_doctorname);
        dateDayTime = dialogView.findViewById(R.id.accept_reject_date);
        Button call;
        call = dialogView.findViewById(R.id.join_call);

        name.setText(((Appointments)date_app.get(holder.getAdapterPosition())).getDoctorName());
        String str =  ((Appointments)date_app.get(holder.getAdapterPosition())).getDate() + ", " +
                ((Appointments)date_app.get(holder.getAdapterPosition())).getDay() + ", " +
                convertTo12HourFormat(String.valueOf(((Appointments)date_app.get(holder.getAdapterPosition())).getTimeslot()));

        dateDayTime.setText(str);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.RoundedCornerDialog);
        dialogBuilder.setView(dialogView);
        AlertDialog acceptRejectDialog = dialogBuilder.create();
        acceptRejectDialog.show();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, CallActivity.class);
                in.putExtra("AId", ((Appointments)date_app.get(holder.getAdapterPosition())).getAppointmentId());
                context.startActivity(in);
                acceptRejectDialog.dismiss();
            }
        });
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

    @Override
    public int getItemCount() {
        return date_app.size();
    }


    private class RequestedCardViewHolder extends RecyclerView.ViewHolder {
        TextView doctorname;
        TextView appointmentTiming;
        ImageView imageView;

        public RequestedCardViewHolder(View view) {
            super(view);
            doctorname  = view.findViewById(R.id.doctor_name_text);
            appointmentTiming = view.findViewById(R.id.date_text);
            imageView = view.findViewById(R.id.doctor_img);
        }
    }

    private class viewHolder2 extends RecyclerView.ViewHolder {
        TextView tv;
        public viewHolder2(View view) {
            super(view);
            tv = view.findViewById(R.id.tv);
        }
    }
}
