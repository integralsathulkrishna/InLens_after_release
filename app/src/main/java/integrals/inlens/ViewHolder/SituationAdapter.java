package integrals.inlens.ViewHolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;
import java.util.ArrayList;
import java.util.List;
import integrals.inlens.Helper.PhotoListHelper;
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
    PhotoListHelper photoListHelper;
    DatabaseReference databaseReferencePhotoList;
    private Dialog mBottomSheetDialog;
    private RecyclerView mBottomSheetDialogRecyclerView;
    private ImageButton mBottomSheetDialogCloseBtn;
    private TextView mBottomSheetDialogTitle;
    private ProgressBar mBottomSheetDialogProgressbar;


    public SituationAdapter(         Context context,
                                     List<SituationModel> situation,
                                     List<String> SIdList,
                                     DatabaseReference databaseReference,
                                     DatabaseReference db,
                                     String communityID,
                                     Activity cloudAlbum,
                                     Dialog mBottomSheetDialog,
                                     RecyclerView mBottomSheetDialogRecyclerView,
                                     ImageButton mBottomSheetDialogCloseBtn,
                                     TextView mBottomSheetDialogTitle,
                                     ProgressBar mBottomSheetDialogProgressbar) {
        this.context = context;
        Situation = situation;
        this.SIdList = SIdList;
        this.databaseReference = databaseReference;
        CommunityID = communityID;
        CloudAlbum = cloudAlbum;
        this.databaseReferencePhotoList=db;
        this.mBottomSheetDialog=mBottomSheetDialog;
        this.mBottomSheetDialogCloseBtn=mBottomSheetDialogCloseBtn;
        this.mBottomSheetDialogRecyclerView=mBottomSheetDialogRecyclerView;
        this.mBottomSheetDialogProgressbar=mBottomSheetDialogProgressbar;
        this.mBottomSheetDialogTitle=mBottomSheetDialogTitle;

    }



    @NonNull
    @Override
    public SituationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cloud_album_layout,parent,false);
        return new SituationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SituationViewHolder holder, final int position) {


        holder.Time.setText(String.format("@ %s", Situation.get(position).getTime()));
        holder.Title.setText(String.format("%s", Situation.get(position).getTitle()));
        holder.SituationLogo.setText(String.format("%s", Situation.get(position).getTitle().charAt(0)));


       holder.ExpandButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try{
               photoListHelper=new PhotoListHelper(context,CloudAlbum,databaseReferencePhotoList);
                   photoListHelper.DisplayBottomSheet(
                           mBottomSheetDialog,
                           mBottomSheetDialogRecyclerView,
                           mBottomSheetDialogCloseBtn,
                           mBottomSheetDialogTitle,
                           mBottomSheetDialogProgressbar,
                           Situation.get(position).getSituationTime(),
                           Situation.get(position+1).getSituationTime(),
                           CommunityID,
                           Situation.get(position).getTitle(),
                           false);

               }catch (IndexOutOfBoundsException e){
               photoListHelper=new PhotoListHelper(context,CloudAlbum,databaseReferencePhotoList);
                   photoListHelper.DisplayBottomSheet(
                           mBottomSheetDialog,
                           mBottomSheetDialogRecyclerView,
                           mBottomSheetDialogCloseBtn,
                           mBottomSheetDialogTitle,
                           mBottomSheetDialogProgressbar,
                           Situation.get(position).getSituationTime(),
                           Situation.get(position).getSituationTime(),
                           CommunityID,
                           Situation.get(position).getTitle(),
                           true);

           }


                }
       });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShowDialog(position);
                return false;
            }
        });

        holder.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog(position);
            }
        });



            try {
                photoListHelper=new PhotoListHelper(context,CloudAlbum,databaseReferencePhotoList);
                photoListHelper.SetRecyclerView(Situation.get(position).getSituationTime(),
                        Situation.get(position + 1).getSituationTime(),
                        CommunityID,
                        false,
                        Situation.get(position).getTitle(), true,
                        holder.recyclerView);

            }catch (IndexOutOfBoundsException e){
                photoListHelper=new PhotoListHelper(context,CloudAlbum,databaseReferencePhotoList);
                photoListHelper.SetRecyclerView(Situation.get(position).getSituationTime(),
                        Situation.get(position).getSituationTime(),
                        CommunityID,
                        true,
                        Situation.get(position).getTitle(), true,
                        holder.recyclerView);


            }












    }


    @Override
    public int getItemCount() {
        return Situation.size();
    }




    private void ShowDialog(final int position) {

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













    public class SituationViewHolder extends RecyclerView.ViewHolder {

        TextView Name , Count , Time , Title,SituationLogo;
        Button Join,View;
        ImageButton SituationEditBtn;
        public Button EditButton;
        public Button ExpandButton;

        public RecyclerView recyclerView;
        public SituationViewHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.createdby);
            EditButton=(Button)itemView.findViewById(R.id.EditSituationCard);
            Time = itemView.findViewById(R.id.SituationTimeCL);
            Title = itemView.findViewById( R.id.SituationNametextViewCloud_Layout);
            SituationLogo=itemView.findViewById(R.id.SituationLogoCL);
            ExpandButton=itemView.findViewById(R.id.ExpandPhotoListCL);
            recyclerView=itemView.findViewById(R.id.PhotoListRecyclerViewCL);
            CardSliderLayoutManager cardSliderLayoutManager=new CardSliderLayoutManager(context);
            recyclerView.setLayoutManager(cardSliderLayoutManager);
        }
    }
}
