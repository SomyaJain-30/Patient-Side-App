package com.example.btp_prop1;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimeSlotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<String> times;
    List<String> convertedTimes;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public TimeSlotsAdapter(Context context)
    {
        this.context = context;
        times = new ArrayList<>();
        convertedTimes = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_card, parent, false);
        return new TimeSlotCardHolder(view);
    }
    int selectedPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String time = convertedTimes.get(holder.getAdapterPosition());
        ((TimeSlotCardHolder) holder).tv.setText(time);
        CardView cv = ((TimeSlotCardHolder) holder).cardView;
        int whiteColor = ContextCompat.getColor(context, R.color.white);
        int lightBlueColor = ContextCompat.getColor(context, R.color.veryLightBlue);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();

                if (selectedPosition != currentPosition) {
                    // Unselect the previously selected card
                    if (selectedPosition != -1) {
                        notifyItemChanged(selectedPosition);
                    }

                    // Select the clicked card
                    selectedPosition = currentPosition;
                    notifyItemChanged(selectedPosition);
                } else {
                    // Deselect the clicked card
                    selectedPosition = -1;
                    notifyItemChanged(currentPosition);
                }


            }
        });

        if (selectedPosition == holder.getAdapterPosition()) {
            cv.setCardBackgroundColor(lightBlueColor);
        } else {
            cv.setCardBackgroundColor(whiteColor);
        }

        if(selectedPosition==-1)
            BookSlotFragment.sendRequest.setVisibility(View.INVISIBLE);
        else
            BookSlotFragment.sendRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return convertedTimes.size();
    }



    private static List<String> convertTo12HourFormat(List<String> times) {
        List<String> convertedTimes = new ArrayList<>();

        DateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for (String time : times) {
            try {
                Date date = inputFormat.parse(time);
                String convertedTime = outputFormat.format(date);
                convertedTimes.add(convertedTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return convertedTimes;
    }

    public void setTimes(List<String> times) {
        this.times = times;
        Collections.sort(times);
        Set<String> uniqueTimesSet = new LinkedHashSet<>(times);
        times = new ArrayList<>(uniqueTimesSet);
        convertedTimes = convertTo12HourFormat(times);
        notifyDataSetChanged();
    }

    public String getSelectedTime() {
        return convertedTimes.get(selectedPosition);
    }

    public String getSelectedTime24() {
        return times.get(selectedPosition);
    }

    private class TimeSlotCardHolder extends RecyclerView.ViewHolder {
        TextView tv;
        CardView cardView;
        boolean isWhite;
        public TimeSlotCardHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.time);
            cardView = view.findViewById(R.id.cardView);
            isWhite = true;
        }
    }
}
