package integrals.inlens.Activities;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.PhotoListHelper;
import integrals.inlens.MainActivity;
import integrals.inlens.Models.SituationModel;
import integrals.inlens.R;
import integrals.inlens.ViewHolder.SituationAdapter;

public class CloudAlbum extends AppCompatActivity {
    private RecyclerView            recyclerView;
    private DatabaseReference       databaseReference;
    private String                  CommunityIDLocal;
    private String                  CommunityID;
    private Calendar                calendar;
    private SituationAdapter        adapter;
    private String                  sowner,stime,stitle,sKey,sTime;
    private List<SituationModel>    SituationList;
    private List<String>            SituationIDList;
    private DatabaseReference       db,ComNotyRef,deleteDatabaseReference;
    private String                  Album;
    private Button                  NewSituation;
    private String                  ReturnName="Oops";
    private String                  TimeEnd,TimeStart,GlobalID;
    private Boolean                 LastPost;
    private DatabaseReference   databaseReferencePhotoList=null;
    private Dialog createNewSituation , Renamesituation;

    private String   Name;
    private Button SwipeControl;
    private Boolean SwipeUp=false;
    private String TestCommunityID=null;
    private Activity activity;
    private String LocalID;
    private String CurrentUser;
    private EditText SitEditName;
    private Activity cloudalbumcontext;

//////QR CODE DIALOG///////////////////////////////////////////////////////////////////////////////|
    private   String PhotographerID;
    ImageView QRCodeImageView;
    ActionBar actionBar;
    private String Default="No current community";
    private String QRCommunityID="1122333311101";
    private TextView textView;
    private Button InviteLinkButton;
    private Dialog QRCodeDialog;
    private PhotoListHelper photoListHelper;


    private Dialog mBottomSheetDialog;
    private RecyclerView mBottomSheetDialogRecyclerView;
    private ImageButton mBottomSheetDialogCloseBtn;
    private TextView mBottomSheetDialogTitle;
    private ProgressBar mBottomSheetDialogProgressbar;

    ///////////////////////////////////////////////////////////////////////////////////////////////////|
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_cloud_album);
        actionBar=getSupportActionBar();
        QRCodeInit();
        cloudalbumcontext = this;
        activity=this;
        CommunityIDLocal=getIntent().getStringExtra("PostKeyLocal::");
        SwipeControl=(Button)findViewById(R.id.SwipeControl);
        String AlbumName = getIntent().getStringExtra("AlbumName");
        CommunityID = getIntent().getStringExtra("GlobalID::");
        LocalID=getIntent().getStringExtra("LocalID::");
        CurrentUser=getIntent().getStringExtra("UserID::");
        recyclerView = (RecyclerView)findViewById(R.id.SituationRecyclerView);
        deleteDatabaseReference=FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Users")
                .child(CurrentUser)
                .child("Communities").child(LocalID);

        final GridLayoutManager gridLayoutManager = new
                GridLayoutManager(getApplicationContext(),1,
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        Album = AlbumName;
        SituationList = new ArrayList<>();
        SituationIDList = new ArrayList<>();
        TimeEnd="2100-8-6T13-22-45";
        TimeStart="2000-8-6T13-22-45";
        Name="Album Photos";
        LastPost=false;
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




        ///////////Create New Situation////////////////////////////////////////////////////////////
        createNewSituation = new Dialog(CloudAlbum.this);
        createNewSituation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createNewSituation.setContentView(R.layout.create_new_situation_layout);
        createNewSituation.setCancelable(false);
        createNewSituation.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        SitEditName = createNewSituation.findViewById(R.id.situation_name);
        SitEditName.requestFocus();
        Button Done ,Cancel;
        Done =   createNewSituation.findViewById(R.id.done_btn);
        Cancel = createNewSituation.findViewById(R.id.cancel_btn);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(SitEditName.getText().toString()))
                {   calendar=Calendar.getInstance();
                    String SituationTimeIntervel=calendar.get(Calendar.YEAR)+ "-"
                            +calendar.get(Calendar.MONTH)+"-"
                            +calendar.get(Calendar.DAY_OF_MONTH)+"T"
                            +calendar.get(Calendar.HOUR_OF_DAY)+"-"
                            +calendar.get(Calendar.MINUTE)+"-"
                            +calendar.get(Calendar.SECOND);
                    String SituationTime=calendar.get(Calendar.YEAR)+ "/"
                            +calendar.get(Calendar.MONTH)+"/"
                            +calendar.get(Calendar.DAY_OF_MONTH)+"  -   "
                            +calendar.get(Calendar.HOUR_OF_DAY)+":"
                            +calendar.get(Calendar.MINUTE)+":"
                            +calendar.get(Calendar.SECOND);

                    Map situationmap = new HashMap();
                    situationmap.put("name",SitEditName.getText().toString().trim());
                    situationmap.put("time", SituationTime);
                    situationmap.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final String push_id =databaseReference.push().getKey();



                    // Added by  For Implementation of Situation Upload
                    situationmap.put("SituationKey",push_id);
                    situationmap.put("SituationTime",SituationTimeIntervel);
                    final Map member = new HashMap();
                    member.put("memid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("ComNoty");
                    ComNotyRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // Situation Notification function by elson jose

                            Map notymap = new HashMap();
                            notymap.put("name",SitEditName.getText().toString().trim());
                            notymap.put("ownername",GetUserName(FirebaseAuth.getInstance().getCurrentUser().getUid()));


                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                String id = snapshot.child("Photographer_UID").getValue().toString();
                                if(!id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    dref.child(id).push().setValue(notymap);
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
                                Toast.makeText(CloudAlbum.this,"New Situation Created : "+SitEditName.getText().toString(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(e.toString().contains("FirebaseNetworkException"))
                                Toast.makeText(CloudAlbum.this,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(CloudAlbum.this,"Unable to create new Situation.", Toast.LENGTH_SHORT).show();


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

////////////////////////////////////////////////////////////////////////////////////////////////////
        CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),
                "",null,1);
        TestCommunityID=currentDatabase.GetLiveCommunityID();
        currentDatabase.close();
////////////////////////////////////////////////////////////////////////////////////////////////////
        databaseReferencePhotoList=FirebaseDatabase.getInstance().getReference().child("Communities")
        .child(CommunityID).child("BlogPosts");
////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////Implementing Bottom Recycler View Dialog//////////////////////////////////////
        mBottomSheetDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);
        mBottomSheetDialog.setContentView(R.layout.participants_bottomsheet_layout);
        mBottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.BottomUpSlideDialogAnimation;

        Window window = mBottomSheetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0.75f);

        mBottomSheetDialogRecyclerView = mBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_recyclerview);
        mBottomSheetDialogRecyclerView.setHasFixedSize(true);
        GridLayoutManager Gridmanager = new GridLayoutManager(CloudAlbum.this, 2);
        mBottomSheetDialogRecyclerView.setLayoutManager(Gridmanager);

        mBottomSheetDialogTitle = mBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_title);

        mBottomSheetDialogCloseBtn = mBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_closebtn);
        mBottomSheetDialogCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialogProgressbar = mBottomSheetDialog.findViewById(R.id.particpants_bottomsheet_progressbar);

////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void QRCodeInit() {

        QRCodeDialog = new Dialog(this);
        QRCodeDialog.setCancelable(true);
        QRCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        QRCodeDialog.setContentView(R.layout.activity_qrcode_generator);
        QRCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        QRCodeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        CurrentDatabase currentDatabase=new CurrentDatabase(getApplicationContext(),"",null,1);
        QRCommunityID=currentDatabase.GetLiveCommunityID();
        currentDatabase.close();

        InviteLinkButton= QRCodeDialog.findViewById(R.id.InviteLinkButton);
        PhotographerID=QRCommunityID;

        ImageButton QRCodeCloseBtn = QRCodeDialog.findViewById(R.id.QR_dialog_closebtn);
        QRCodeCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QRCodeDialog.dismiss();

            }
        });
        textView= QRCodeDialog.findViewById(R.id.textViewAlbumQR);
        QRCodeImageView= QRCodeDialog.findViewById(R.id.QR_Display);

        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
        try {
            BitMatrix bitMatrix=multiFormatWriter.encode(PhotographerID, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
            QRCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            actionBar.setTitle("No current album");
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");
        }catch (NullPointerException e){
            actionBar.setTitle("No current album");
            QRCodeImageView.setVisibility(View.INVISIBLE);
            textView.setText("You must be in an album to generate QR code");

        }
        InviteLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent SharingIntent = new Intent(Intent.ACTION_SEND);
                SharingIntent.setType("text/plain");
                String CommunityPostKey=QRCommunityID;

                SharingIntent.putExtra(Intent.EXTRA_TEXT,"InLens Cloud-Album Invite Link \n\n" +
                        "Long press Link to copy and paste the link on InLens app https://inlens.in/joins/"+CommunityPostKey);
                startActivity(SharingIntent);

            }
        });
    }


    private String GetUserName(String uid) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                ReturnName = name;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return ReturnName;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean Default = false;
        SharedPreferences sharedPreferences = getSharedPreferences("InCommunity.pref", MODE_PRIVATE);
        SharedPreferences sharedPreferences1=getSharedPreferences("Owner.pref",MODE_PRIVATE);

        if (sharedPreferences.getBoolean("UsingCommunity::", Default) == true) {
        if((TestCommunityID).contentEquals(getIntent().getExtras().getString("GlobalID::"))){
            menu.add(0, 0, 0, "Add Participant")
                    .setIcon(R.drawable.ic_add_participant)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        if(sharedPreferences1.getBoolean("ThisOwner::",false) ==true) {
            menu.add(0, 1, 0, "Add Situation")
                    .setIcon(R.drawable.ic_add)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
            menu.add(0, 2, 0, "Delete Album")
                    .setIcon(R.drawable.menu_icon)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        }


    }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            //startActivity(new Intent(CloudAlbum.this, QRCodeGenerator.class));
            QRCodeDialog.show();
        }
        if(item.getItemId()==1){
            createNewSituation.show();
            int countdef = SituationIDList.size()+1;
            SitEditName.setText(String.format("New Situation %s", String.valueOf(countdef)));
        }
        if(item.getItemId()==2){
            new BottomSheet.Builder(this).title("Cloud-Album Options").sheet(R.menu.cloud_album_menu).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.delete_cloud_album:
                            DeleteCurrentAlbum();
                            break;
                    }
                }
            }).show();

        }

        return true;
    }







    @Override
    protected void onStart() {
        super.onStart();





/////////////////////////////////////////////Creating Situation View///////////////////////////////|
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
                            SituationIDList,
                            databaseReference,
                            databaseReferencePhotoList,
                            CommunityID,cloudalbumcontext,
                 mBottomSheetDialog,
                 mBottomSheetDialogRecyclerView,
                 mBottomSheetDialogCloseBtn,
                 mBottomSheetDialogTitle,
                 mBottomSheetDialogProgressbar
);

                    recyclerView.setAdapter(adapter);

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        }


////////////////////////////Situation View Implemented/////////////////////////////////////////////|







    private void DeleteCurrentAlbum(){
                deleteDatabaseReference.removeValue().addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                             Toast.makeText(getApplicationContext(),"Album removed",Toast.LENGTH_SHORT).show();
                             finish();

                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Unable to remove album . Please check your internet connection.",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}


