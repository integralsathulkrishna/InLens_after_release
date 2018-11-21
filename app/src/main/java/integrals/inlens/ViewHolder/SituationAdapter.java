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
    DatabaseReference databaseReference;
    String CommunityID;
    Activity CloudAlbum;
    List MembersList = new ArrayList();
    Dialog  Renamesituation;


    public SituationAdapter(Context context, List<SituationModel> situation, List<String> SIdList, DatabaseReference databaseReference, String communityID,  Activity cloudAlbum) {
        this.context = context;
        Situation = situation;
        this.SIdList = SIdList;
        this.databaseReference = databaseReference;
        CommunityID = communityID;
        CloudAlbum = cloudAlbum;
    }

    private void RenameSituation(final String s) {

        Renamesituation = new Dialog(CloudAlbum);
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




    @NonNull
    @Override
    public SituationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.situation_layout_f,parent,false);
        return new SituationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SituationViewHolder holder, final int position) {


        holder.Time.setText(String.format("@ %s", Situation.get(position).getTime()));
        holder.Title.setText(String.format("%s", Situation.get(position).getTitle()));
        holder.SituationLogo.setText(String.format("%s", Situation.get(position).getTitle().charAt(0)));




        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                BottomSheet.Builder BottomS =  new BottomSheet.Builder(CloudAlbum);
                BottomS.title("Edit Situation : "+Situation.get(position).getTitle())
                        .sheet(R.menu.situationmenu).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i)
                        {
                            case R.id.renamesituation :
                                RenameSituation(SIdList.get(position));
                                Renamesituation.show();
                                break;
                            case R.id.deletesituation:
                            {
                                if(SIdList.size()<=1)
                                {
                                    Toast.makeText(context,"Unable to perform deletion. Album should have at least one situation.",Toast.LENGTH_LONG).show();
                                }
                                else
                                {

                                    final android.app.AlertDialog.Builder alertbuilder = new android.app.AlertDialog.Builder(CloudAlbum);
                                    alertbuilder.setTitle("Delete Situation "+Situation.get(position).getTitle())
                                            .setMessage("You are about to delete the situation. Are you sure you want to continue ?")
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialogInterface, int i) {



                                                    databaseReference.child(SIdList.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            Toast.makeText(context,"Successfully deleted the situation",Toast.LENGTH_LONG).show();
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(context,"Failed to delete the situation",Toast.LENGTH_LONG).show();
                                                            dialogInterface.dismiss();
                                                        }
                                                    });


                                                }
                                            })
                                            .create()
                                            .show();

                                }

                            }

                            break;
                        }

                    }
                }).show();

                return false;
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
            Time = itemView.findViewById(R.id.createdtime);
            Title = itemView.findViewById( R.id.situationtitle);
            SituationLogo=itemView.findViewById(R.id.SituationLogo);
        }
    }
}
