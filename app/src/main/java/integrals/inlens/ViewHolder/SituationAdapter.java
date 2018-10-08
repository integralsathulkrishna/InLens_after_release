package integrals.inlens.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
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

import integrals.inlens.Activities.CloudAlbum;
import integrals.inlens.R;
import integrals.inlens.Models.SituationModel;


public class SituationAdapter extends RecyclerView.Adapter<SituationAdapter.SituationViewHolder> {

    Context context;
    List<SituationModel> Situation;
    List<String> SIdList;
    int count;
    DatabaseReference databaseReference;
    BottomSheet.Builder builder;

    String CommunityID;
    DatabaseReference membersref;
    List MembersList = new ArrayList();
    Dialog  Renamesituation;



    private void RenameSituation(final String s) {

        Renamesituation = new Dialog(context);
        Renamesituation.setContentView(R.layout.create_new_situation_layout);
        Renamesituation.setCancelable(false);
        final EditText SituationName = Renamesituation.findViewById(R.id.situation_name);
        SituationName.requestFocus();
        Button Done ,Cancel;
        Done =   Renamesituation.findViewById(R.id.done_btn);
        Cancel = Renamesituation.findViewById(R.id.cancel_btn);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(SituationName.getText().toString()))
                {
                    databaseReference.child(s).child("name").setValue(SituationName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                Toast.makeText(context,"Situation renamed as : "+SituationName.getText().toString(),Toast.LENGTH_SHORT).show();
                                SituationName.setText("");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(e.toString().contains("FirebaseNetworkException"))
                                Toast.makeText(context,"Not Connected to Internet.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context,"Unable to rename new Situation.", Toast.LENGTH_SHORT).show();

                            SituationName.setText("");
                        }
                    });
                    Renamesituation.dismiss();
                }
                else
                {
                    Toast.makeText(context,"No name given",Toast.LENGTH_LONG).show();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Renamesituation.dismiss();
            }
        });

    }


    public SituationAdapter(Context context, List<SituationModel> situation,
                            List<String> SIdList,
                            DatabaseReference databaseReference,
                            String communityID, DatabaseReference membersref) {
        this.context = context;
        Situation = situation;
        this.SIdList = SIdList;
        this.databaseReference = databaseReference;
        CommunityID = communityID;
        this.membersref = membersref;
    }

    @NonNull
    @Override
    public SituationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.situation_layout,parent,false);
        return new SituationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SituationViewHolder holder, final int position) {


        membersref.child(SIdList.get(position)).child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(snapshot.hasChildren())
                       count++;
                }
                holder.Count.setText(String.format("%d Posts", count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Toast.makeText(context,Situation.get(position).getCreatedby(),Toast.LENGTH_SHORT).show();


        databaseReference.child(Situation.get(position).getCreatedby()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue().toString();
                holder.Name.setText(String.format("By : %s",name));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        final long t = Long.parseLong(Situation.get(position).getTime());
        CharSequence time = DateUtils.getRelativeDateTimeString(context,t,DateUtils.SECOND_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL);
        holder.Time.setText(String.format("@ %s", time.toString()));
        holder.Title.setText(String.format("%s", Situation.get(position).getTitle()));
        holder.SituationLogo.setText(String.format("%s", Situation.get(position).getTitle().charAt(0)));
        membersref.child(SIdList.get(position)).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MembersList.clear();

                for(DataSnapshot snapshot :dataSnapshot.getChildren())
                {
                    String memberid = snapshot.child("memid").getValue().toString();
                    if(!MembersList.contains(memberid))
                    {
                        MembersList.add(memberid);
                    }
                }

                if(MembersList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    holder.Join.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Map member = new HashMap();
        member.put("memid",FirebaseAuth.getInstance().getCurrentUser().getUid());
        holder.Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                membersref.child(SIdList.get(position)).child("members").push().setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                            Toast.makeText(context,"Successfully joined the new situation",Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        holder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ComID",CommunityID);
                intent.putExtra("SituationID",SIdList.get(position));
                intent.putExtra("title",Situation.get(position).getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                */

            }
        });


    }



    @Override
    public int getItemCount() {
        return Situation.size();
    }

    public class SituationViewHolder extends RecyclerView.ViewHolder {

        TextView Name , Count , Time , Title,SituationLogo;
        Button Join,View;
        ImageButton SituationEditBtn;

        public SituationViewHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.createdby);
            Count = itemView.findViewById(R.id.numberofpost);
            Time = itemView.findViewById(R.id.createdtime);
            Title = itemView.findViewById( R.id.situationtitle);
            Join = itemView.findViewById(R.id.situajoin);
            View = itemView.findViewById(R.id.situaview);
            SituationLogo=itemView.findViewById(R.id.SituationLogo);
        }
    }
}
