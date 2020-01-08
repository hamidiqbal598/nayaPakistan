package com.example.abdulmanan.nayapakistan1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Selection extends AppCompatActivity {

    private Spinner NaSpinner;
    private Spinner SupportSpinner;
    private Button Submit;
    String na=null;
    String selSupport=null;
    private ProgressDialog loadingBar;



    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Saving Information...");


        String[] consti = new String[]{
                "NA-1","NA-2","NA-3","NA-4","NA-5","NA-6","NA-7","NA-8","NA-9","NA-10",
                "NA-11","NA-12","NA-13","NA-14","NA-15","NA-16","NA-17","NA-18","NA-19","NA-20",
                "NA-21","NA-22","NA-23","NA-24","NA-25","NA-26","NA-27","NA-28","NA-29","NA-30",
                "NA-31","NA-32","NA-33","NA-34","NA-35","NA-36","NA-37","NA-38","NA-39","NA-40",
                "NA-41","NA-42","NA-43","NA-44","NA-45","NA-46","NA-47","NA-48","NA-49","NA-50",
                "NA-51","NA-52","NA-53","NA-54","NA-55","NA-56","NA-57","NA-58","NA-59","NA-60",
                "NA-61","NA-62","NA-63","NA-64","NA-65","NA-66","NA-67","NA-68","NA-69","NA-70",
                "NA-71","NA-72","NA-73","NA-74","NA-75","NA-76","NA-77","NA-78","NA-79","NA-80",
                "NA-81","NA-82","NA-83","NA-84","NA-85","NA-86","NA-87","NA-88","NA-89","NA-90",
                "NA-91","NA-92","NA-93","NA-94","NA-95","NA-96","NA-97","NA-98","NA-99","NA-100",
                "NA-101","NA-102","NA-103","NA-104","NA-105","NA-106","NA-107","NA-108","NA-109","NA-110",
                "NA-111","NA-112","NA-113","NA-114","NA-115","NA-116","NA-117","NA-118","NA-119","NA-120",
                "NA-121","NA-122","NA-123","NA-124","NA-125","NA-126","NA-127","NA-128","NA-129","NA-130",
                "NA-131","NA-132","NA-133","NA-134","NA-135","NA-136","NA-137","NA-138","NA-139","NA-140",
                "NA-141","NA-142","NA-143","NA-144","NA-145","NA-146","NA-147","NA-148","NA-149","NA-150",
                "NA-151","NA-152","NA-153","NA-154","NA-155","NA-156","NA-157","NA-158","NA-159","NA-160",
                "NA-161","NA-162","NA-163","NA-164","NA-165","NA-166","NA-167","NA-168","NA-169","NA-170",
                "NA-171","NA-172","NA-173","NA-174","NA-175","NA-176","NA-177","NA-178","NA-179","NA-180",
                "NA-181","NA-182","NA-183","NA-184","NA-185","NA-186","NA-187","NA-188","NA-189","NA-190",
                "NA-191","NA-192","NA-193","NA-194","NA-195","NA-196","NA-197","NA-198","NA-199","NA-200",
                "NA-201","NA-202","NA-203","NA-204","NA-205","NA-206","NA-207","NA-208","NA-209","NA-210",
                "NA-211","NA-212","NA-213","NA-214","NA-215","NA-216","NA-217","NA-218","NA-219","NA-220",
                "NA-221","NA-222","NA-223","NA-224","NA-225","NA-226","NA-227","NA-228","NA-229","NA-230",
                "NA-231","NA-232","NA-233","NA-234","NA-235","NA-236","NA-237","NA-238","NA-239","NA-240",
                "NA-241","NA-242","NA-243","NA-244","NA-245","NA-246","NA-247","NA-248","NA-249","NA-250",
                "NA-251","NA-252","NA-253","NA-254","NA-255","NA-256","NA-257","NA-258","NA-259","NA-260",
                "NA-261","NA-262","NA-263","NA-264","NA-265","NA-266","NA-267","NA-268","NA-269","NA-270",
                "NA-271","NA-272"
        };
        final String[] Support=new String[]{"Government","Opposition","No One"};

        NaSpinner = (Spinner) findViewById(R.id.spinnerNA);
        SupportSpinner= (Spinner) findViewById(R.id.spinnersupport);
        Submit=(Button) findViewById(R.id.sel_btn);


        List<String> SupportL=new ArrayList<>();
        SupportL.add(0,"I am supporting...");
        for (int i=0;i<Support.length; i++){
            SupportL.add(Support[i]);
        }

        List<String> constituency = new ArrayList<>();
        constituency.add(0,"Select your Constituency...");
        for (int i=0;i<consti.length; i++){
            constituency.add(consti[i]);
        }


        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,SupportL);

        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,constituency);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        NaSpinner.setAdapter(arrayAdapter);

        SupportSpinner.setAdapter(arrayAdapter1);

        SupportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).equals("I am supporting...")){
                    // Do nothing
                    selSupport=null;
                }
                else{
                    selSupport = parent.getItemAtPosition(position).toString();
                    Toast.makeText(Selection.this, "Selected: "+ selSupport, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        NaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Select your Constituency...")){
                    // Do nothing
                    na=null;
                }
                else{
                    na = parent.getItemAtPosition(position).toString();
                    Toast.makeText(Selection.this, "Selected: "+ na, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (na==null || selSupport==null) {
                    Toast.makeText(getApplicationContext(), "Please select your constituency or your support...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.show();
                    HashMap userMap = new HashMap();
                    userMap.put("halkaNo", na);
                    userMap.put("support", selSupport);
                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful())
                            {
                                loadingBar.dismiss();
                                SendUserToMain();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Selection.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

                }
            }
        });


    }

    private void SendUserToMain() {
        Intent login_intent=new Intent(Selection.this,MainActivity.class);
        login_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login_intent);
        finish();

    }



}
