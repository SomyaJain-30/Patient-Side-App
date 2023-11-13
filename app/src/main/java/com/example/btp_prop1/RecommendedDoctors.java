package com.example.btp_prop1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecommendedDoctors extends AppCompatActivity {
    ArrayList<Doctor> arrcontacts = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    Toolbar toolbar;
    private RecyclerDoctorListAdapter adapter;
    ArrayList<Doctor> searchList;
    private RecyclerView recyclerView;
    private Dialog floatingDialog;
    private Button dialogButton;
    private TextView dialogTextView;

    FirebaseFirestore firebaseFirestore;

    static FilterModel filterModel;

    static String pdfUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_doctors);

        toolbar = findViewById(R.id.toolbarofSearchDoctors);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setTitle("Select your own specialist");
//        this.getActionBar().setTitle("");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        floatingDialog = new Dialog(this);
        floatingDialog.setContentView(R.layout.filter_dialog);
        floatingDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        pdfUri = getIntent().getStringExtra("Uri");
        System.out.println("**************///////////////// " + pdfUri);

        firebaseFirestore = FirebaseFirestore.getInstance();

        filterModel = new FilterModel(-1,-1, new ArrayList<>(), new ArrayList<>());

        adapter = new RecyclerDoctorListAdapter(this, arrcontacts);
        recyclerView.setAdapter(adapter);
        firebaseFirestore.collection("Doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds: queryDocumentSnapshots)
                {
                    Doctor doctor = new Doctor(
                            ds.get("About").toString(),
                            ds.get("Clinic Address").toString(),
                            ds.get("E-mail address").toString(),
                            ds.getId(),
                            ds.get("Education").toString(),
                            ds.get("Experience").toString(),
                            ds.get("Gender").toString(),
                            (List<String>) ds.get("Language"),
                            ds.get("Name").toString(),
                            ds.get("Profile URL").toString(),
                            ds.get("Role").toString(),
                            (Map<String, List<String>>) ds.get("Slots"),
                            (List<String>) ds.get("Specialization"),
                            (List<Integer>) ds.get("Survey Data")
                    );
                    System.out.println(doctor);
                    if(doctor!=null)
                    {
                        adapter.addAtBeginning(doctor);
                    }
                    System.out.println(arrcontacts);

                }
            }
        });
    }

    private void showFloatingDialog() {
        //floatingDialog.show();
        Intent i = new Intent(RecommendedDoctors.this, FilterActivity.class);
        startActivityForResult(i,120);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==120)
            {
                adapter.filter(filterModel.getExp_idx(), filterModel.getGen_idx(), filterModel.getRoles(), filterModel.getLangs());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem filterItem = menu.findItem(R.id.action_filter);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                showFloatingDialog();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchList = new ArrayList<>();
                if(s.length()>0) {
                    for (int i = 0; i < arrcontacts.size(); i++) {
                        if (arrcontacts.get(i).getName().toUpperCase().contains(s.toUpperCase()) || arrcontacts.get(i).getRole().toUpperCase().contains(s.toUpperCase())) {
                            searchList.add(arrcontacts.get(i));
                        }
                    }
                    RecyclerDoctorListAdapter adapter1 = new RecyclerDoctorListAdapter(RecommendedDoctors.this, searchList);
                    recyclerView.setAdapter(adapter1);
                }
                else {
                    recyclerView.setAdapter(adapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList = new ArrayList<>();
                if(s.length()>0) {
                    for (int i = 0; i < arrcontacts.size(); i++) {
                        if (arrcontacts.get(i).getName().toUpperCase().contains(s.toUpperCase()) || arrcontacts.get(i).getRole().toUpperCase().contains(s.toUpperCase())) {
                            searchList.add(arrcontacts.get(i));
                        }
                    }
                    RecyclerDoctorListAdapter adapter1 = new RecyclerDoctorListAdapter(RecommendedDoctors.this, searchList);
                    recyclerView.setAdapter(adapter1);
                }
                else {
                    recyclerView.setAdapter(adapter);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        else if(id==R.id.action_filter){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}