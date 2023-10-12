package com.example.btp_prop1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class BookSlotFragment extends BottomSheetDialogFragment {
    private Spinner dateSpinner;
    RecyclerView rv;
    String day;
    String date,Did;
    FirebaseAuth firebaseAuth;
    static TextView sendRequest;
    FirebaseFirestore firebaseFirestore;
    Map<String, List<String>> slotsMap;
    Map<String,List<String>> takenSlots;
    TimeSlotsAdapter timeSlotsAdapter;

    public static BookSlotFragment newInstance(String did) {
        BookSlotFragment fragment = new BookSlotFragment();
        Bundle args = new Bundle();
        args.putString("Did", did);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_book_slot, container, false);
        rv = v.findViewById(R.id.choose_timeslot);
        sendRequest = v.findViewById(R.id.snd_req);
        dateSpinner = v.findViewById(R.id.choose_date);
        takenSlots = new HashMap<>();
        timeSlotsAdapter = new TimeSlotsAdapter(getContext());
        populateSpinner();
        rv.setLayoutManager(new GridLayoutManager(getContext(),3));
        rv.setAdapter(timeSlotsAdapter);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        //comment
        Bundle args = getArguments();
        if (args != null) {
            Did = args.getString("Did");
        }
        firebaseFirestore.collection("Doctors").document(Did).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                takenSlots = (Map<String, List<String>>) documentSnapshot.get("Booked Slots");
                if(takenSlots==null)
                    takenSlots = new HashMap<>();

                rv.setVisibility(View.VISIBLE);
                dateSpinner.setVisibility(View.VISIBLE);
                setRecyclerView(dates.get(0));
            }
        });
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay =  (parent.getItemAtPosition(position)).toString();
                setRecyclerView(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String selectedDay =  (parent.getItemAtPosition(0)).toString();
                setRecyclerView(selectedDay);
            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.narrow_animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        processChosenSlot();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                sendRequest.startAnimation(animation);
            }
        });

        return v;
    }

    public void setRecyclerView(String selectedDay)
    {
        String[] parts =  selectedDay.split("  ");
        day = parts[0];
        date = parts[1];
        List<String> slotTaken = takenSlots.get(date);
        List<String> list1 = doctor_detail.slots1.get(day);
        if(slotTaken==null)
            slotTaken = new ArrayList<>();

        List<String> uniqueSlots = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTimeString = dateFormat.format(Calendar.getInstance().getTime());
        dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedDate = dateFormat.format(new Date());

        for (String slot : list1) {

            if (!slotTaken.contains(slot)) {
                if(!formattedDate.equals(date) || (formattedDate.equals(date) && slot.compareTo(currentTimeString) > 0))
                    uniqueSlots.add(slot);
            }
        }

        timeSlotsAdapter.setTimes(uniqueSlots);
    }

    private void processChosenSlot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Time Slot");
        builder.setMessage("Do you want to request the appointment on " + day + ", " + date + " at " + timeSlotsAdapter.getSelectedTime() + "?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                Toast.makeText(getContext(), "Your appointment is confirmed", Toast.LENGTH_SHORT).show();
                Map<String,Object> mp = new HashMap<>();
                mp.put("Did", Did);
                mp.put("Cid", firebaseAuth.getCurrentUser().getPhoneNumber());
                mp.put("Day", day);
                mp.put("Date", date);
                mp.put("TimeSlot", timeSlotsAdapter.getSelectedTime24());
                mp.put("Status", "Requested");



                firebaseFirestore.collection("Appointments").add(mp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String refId = documentReference.getId();

                        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<String> apids = (List<String>) documentSnapshot.get("Appointments");
                                        if(apids==null)
                                            apids = new ArrayList<>();
                                        apids.add(refId);
                                        Map<String,Object> tobeupdated = new HashMap<>();
                                        tobeupdated.put("Appointments", apids);
                                        firebaseFirestore.collection("Patients").document(firebaseAuth.getCurrentUser().getPhoneNumber())
                                                .update(tobeupdated);

                                    }
                                });

                        firebaseFirestore.collection("Doctors").document(Did)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<String> apids = (List<String>) documentSnapshot.get("Appointments");
                                        Map<String,List<String>> bookedSlots = (Map<String, List<String>>) documentSnapshot.get("Booked Slots");
                                        if(apids==null)
                                            apids = new ArrayList<>();
                                        apids.add(refId);

                                        if(bookedSlots == null)
                                        {
                                            bookedSlots = new HashMap<>();
                                        }
                                        List<String> slotOfDate = bookedSlots.get(date);
                                        if(slotOfDate==null)
                                            slotOfDate = new ArrayList<>();
                                        slotOfDate.add(timeSlotsAdapter.getSelectedTime24());
                                        bookedSlots.put(date, slotOfDate);
                                        Map<String,Object> tobeupdated = new HashMap<>();
                                        tobeupdated.put("Appointments", apids);
                                        tobeupdated.put("Booked Slots", bookedSlots);
                                        firebaseFirestore.collection("Doctors").document(Did)
                                                .update(tobeupdated);

                                    }
                                });
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User declined, you can perform actions her
                // For example, dismiss the dialog or update the UI
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    ArrayList<String> dates;
    private void populateSpinner() {

        dates = generateDateList(7); // Generate dates for the next 7 days
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
    }

    private ArrayList<String> generateDateList(int numDays) {
        ArrayList<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        for (int i = 0; i < numDays; i++) {
            Date currentDate = calendar.getTime();
            String formattedDate = dateFormat.format(currentDate);

            // Get the day of the week (e.g., "Sunday")
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            String dayOfWeek = dayFormat.format(currentDate);

            // Append day of the week to the formatted date
            String dateWithDay = dayOfWeek + "  " + formattedDate;

            dateList.add(dateWithDay);

            calendar.add(Calendar.DAY_OF_YEAR, 1); // Increment to the next day
        }

        return dateList;
    }
}