package integrals.inlens.Activities;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.RecyclerItemClickListener;
import integrals.inlens.Models.SituationModel;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.SituationAdapter;

public class CloudAlbum extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private String CommunityID;
    private TextView textViewAlbumName;
    private Calendar calendar;
    private SituationAdapter adapter;
    private String sowner,stime,stitle,sKey,sTime;
    private List<SituationModel> SituationList;
    private List<String> SituationIDList;
    private DatabaseReference db,ComNotyRef;
    private String Album;
    private FloatingActionButton NewSituation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloud_album);
        String AlbumName = getIntent().getStringExtra("AlbumName");

        CommunityID = getIntent().getStringExtra("GlobalID::");
        recyclerView = (RecyclerView)findViewById(R.id.SituationRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        textViewAlbumName = (TextView) findViewById(R.id.AlbumTitleCloudAlbum);
        textViewAlbumName.setText(AlbumName);
        Album = AlbumName;
        SituationList = new ArrayList<>();
        SituationIDList = new ArrayList<>();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        SharedPreferences sPreferences = getSharedPreferences("ComIDPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreferences.edit();
        if(!TextUtils.isEmpty(CommunityID) && !TextUtils.isEmpty(Album))
        {
            editor.putString("GlobalID::",CommunityID);
            editor.putString("Album",AlbumName);
            editor.commit();
        }

        else
        {
            CommunityID = sPreferences.getString("GlobalID::","");
            AlbumName = sPreferences.getString("Album","");
        }

        getSupportActionBar().setTitle(AlbumName);

        //////////////////////////////////////////////////////////////////////////////////////////
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("Situations");
        ComNotyRef = FirebaseDatabase.getInstance().getReference().child("Communities")
                .child(CommunityID).child("CommunityPhotographer");
        ///////////////////////////////////////////////////////////////////////////////////////////

        // new situation layout
        final Dialog createNewSituation = new Dialog(CloudAlbum.this);
        createNewSituation.setContentView(R.layout.create_new_situation_layout);
        createNewSituation.setCancelable(false);
        final EditText SituationName = createNewSituation.findViewById(R.id.situation_name);
        SituationName.requestFocus();
        Button Done ,Cancel;
        Done = createNewSituation.findViewById(R.id.done_btn);
        Cancel = createNewSituation.findViewById(R.id.cancel_btn);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(SituationName.getText().toString()))
                {
                    calendar=Calendar.getInstance();
                    String SituationTimeIntervel=calendar.get(Calendar.YEAR)+ "-"
                            +calendar.get(Calendar.MONTH)+"-"
                            +calendar.get(Calendar.DAY_OF_MONTH)+"T"
                            +calendar.get(Calendar.HOUR_OF_DAY)+"-"
                            +calendar.get(Calendar.MINUTE)+"-"
                            +calendar.get(Calendar.SECOND);

                    Map situationmap = new HashMap();
                    situationmap.put("name",SituationName.getText().toString().trim());
                    situationmap.put("time", ServerValue.TIMESTAMP);
                    situationmap.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final String push_id =databaseReference.push().getKey();
                    // Added by Athul Krishna For Implementation of Situation Upload
                    situationmap.put("SituationKey",push_id);
                    situationmap.put("SituationTime",SituationTimeIntervel);
                    final Map member = new HashMap();
                    member.put("memid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("ComNoty");
                    ComNotyRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                String id = snapshot.child("Photographer_UID").getValue().toString();
                                dref.child(id).push().child("comid").setValue(CommunityID);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    databaseReference.child(push_id).setValue(situationmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                databaseReference.child(push_id).child("members").push().setValue(member);
                                Toast.makeText(CloudAlbum.this,"New Situation Created : "+SituationName.getText().toString(),Toast.LENGTH_SHORT).show();
                                SituationName.setText("");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(e.toString().contains("FirebaseNetworkException"))
                                Toast.makeText(CloudAlbum.this,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(CloudAlbum.this,"Unable to create new Situation.", Toast.LENGTH_SHORT).show();

                            SituationName.setText("");
                        }
                    });
                    createNewSituation.dismiss();
                }
                else
                {
                    Toast.makeText(CloudAlbum.this,"No name given",Toast.LENGTH_LONG).show();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewSituation.dismiss();
            }
        });


        // new situation

        NewSituation = findViewById(R.id.newsituation_btn);
        SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("UsingCommunity::",false) == true)
        {
        }
        else
        {
            NewSituation.setVisibility(View.GONE);
        }

            NewSituation.setOnClickListener(new View.OnClickListener() {
              @Override
             public void onClick(View view) {
                  createNewSituation.show();

                      }
                 }
                );
                }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean Default = false;
        SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {
            CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
            if((currentDatabase.GetLiveCommunityID()).contentEquals(CommunityID)){
                menu.add(0, 0, 0, "Add Participant")
                        .setIcon(R.drawable.ic_add_participant)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }


        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            startActivity(new Intent(CloudAlbum.this, QRCodeGenerator.class));
        }
        return true;
    }







    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                recyclerView.removeAllViews();
                SituationIDList.clear();
                SituationList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {



                    if(snapshot.hasChildren())
                    {
                        String SituationId = snapshot.getKey();


                        if(snapshot.hasChild("owner"))
                        {
                            String ownerid = snapshot.child("owner").getValue().toString();
                            sowner = ownerid;
                        }

                        if(snapshot.hasChild("time"))
                        {
                            String time = snapshot.child("time").getValue().toString();
                            stime = time;
                        }

                        if(snapshot.hasChild("name"))
                        {
                            String title = snapshot.child("name").getValue().toString();
                            stitle = title;

                            if(snapshot.hasChild("SituationKey"))
                            {
                                String SituationKey = snapshot.child("SituationKey").getValue().toString();
                                sKey = SituationKey;

                            }
                            if(snapshot.hasChild("SituationTime"))
                            {
                                String SituationTime = snapshot.child("SituationTime").getValue().toString();
                                sTime = SituationTime;

                            }

                        }

                        if(!SituationIDList.contains(SituationId))
                        {
                            SituationIDList.add(SituationId);
                            SituationModel model = new SituationModel(sowner,stime,stitle,sKey,sTime);
                            SituationList.add(model);
                        }

                    }


                }

                adapter = new SituationAdapter(getApplicationContext(),
                        SituationList,
                        SituationIDList,db,
                        CommunityID,databaseReference);
                recyclerView.setAdapter(adapter);


                //Done for Implementation of Recycler View OnClick
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(CloudAlbum.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                              try {
                                  Intent intent= new Intent(CloudAlbum.this,SituationActivity.class);
                                  intent.putExtra("TimeStart::",SituationList.get(position).getSituationTime());
                                  intent.putExtra("TimeEnd::",SituationList.get(position+1).getSituationTime());
                                  intent.putExtra("GlobalID::",CommunityID);
                                  startActivity(intent);

                              }catch (IndexOutOfBoundsException e){
                                  Intent intent= new Intent(CloudAlbum.this,SituationActivity.class);
                                  intent.putExtra("TimeStart::",SituationList.get(position).getSituationTime());
                                  intent.putExtra("TimeEnd::",SituationList.get(position).getSituationTime());
                                  intent.putExtra("GlobalID::",CommunityID);
                                  startActivity(intent);

                                }
                              }

                            @Override
                            public void onLongItemClick(View view, int position) {
                              }
                        })
                );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }


}