package integrals.inlens.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import integrals.inlens.MainActivity;
import integrals.inlens.Models.IssueModel;
import integrals.inlens.R;

public class IssueActivity extends AppCompatActivity {

    private RecyclerView IssueRecyclerView;
    private FloatingActionButton CreateissueBtn;
    private DatabaseReference IssueRef;
    private List<IssueModel> IssueModelList = new ArrayList<>();
    private Dialog IssueDialog;
    private List<String> IssueIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        IssueRecyclerView = findViewById(R.id.issuelist);
        IssueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IssueRecyclerView.setHasFixedSize(true);
        CreateissueBtn = findViewById(R.id.create_issue_btn);
        IssueRef = FirebaseDatabase.getInstance().getReference().child("Issues");
        
        InitIssueDialog();
        ShowIssues();

        CreateissueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                IssueDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IssueModelList.clear();
        IssueIdList.clear();
        IssueRecyclerView.removeAllViews();
        ShowIssues();
    }

    private void InitIssueDialog() {

        IssueDialog = new Dialog(IssueActivity.this,android.R.style.Theme_Light_NoTitleBar);
        IssueDialog.setContentView(R.layout.issue_create_layout);
        IssueDialog.setCancelable(false);
        IssueDialog.setCanceledOnTouchOutside(true);
        IssueDialog.getWindow().getAttributes().windowAnimations = R.style.UpBottomSlideDialogAnimation;

        Window IssueDialogWindow = IssueDialog.getWindow();
        IssueDialogWindow.setGravity(Gravity.TOP);
        IssueDialogWindow.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        IssueDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        IssueDialogWindow.setDimAmount(0.75f);
        IssueDialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        final EditText Title = IssueDialog.findViewById(R.id.issue_create_title);
        final EditText Desc = IssueDialog.findViewById(R.id.issue_create_desc);
        Button Create = IssueDialog.findViewById(R.id.done_issue_btn);

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isDigitsOnly(Title.getText().toString()) && !TextUtils.isEmpty(Desc.getText().toString()))
                {
                    String IssueId = IssueRef.push().getKey();
                    IssueRef.child(IssueId).child("Title").setValue(Title.getText().toString());
                    IssueRef.child(IssueId).child("Desc").setValue(Desc.getText().toString());
                    IssueRef.child(IssueId).child("Status").setValue("nope");
                    IssueRef.child(IssueId).child("By").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    IssueRef.child(IssueId).child("TimeStamp").setValue(ServerValue.TIMESTAMP);
                    IssueDialog.dismiss();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Complete all fields",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void ShowIssues()
    {
        IssueModelList.clear();
        IssueIdList.clear();
        IssueRecyclerView.removeAllViews();

        Query IssueQuery = IssueRef.orderByChild("TimeStamp");
        IssueQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                IssueModelList.clear();
                IssueIdList.clear();
                IssueRecyclerView.removeAllViews();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String title="-NA-",desc="-NA-",by="-NA-",status="-NA-";

                    IssueIdList.add(snapshot.getKey());
                    if(snapshot.hasChild("Title"))
                    {
                        title = snapshot.child("Title").getValue().toString();
                    }
                    else
                    {
                        title = "-NA-";
                    }
                    if(snapshot.hasChild("Desc"))
                    {
                        desc = snapshot.child("Desc").getValue().toString();
                    }
                    else
                    {
                        desc = "-NA-";
                    }
                    if(snapshot.hasChild("By"))
                    {
                        by = snapshot.child("By").getValue().toString();
                    }
                    else
                    {
                        by = "-NA-";
                    }
                    if(snapshot.hasChild("Status"))
                    {
                        status = snapshot.child("Status").getValue().toString();
                    }
                    else
                    {
                        status = "-NA-";
                    }

                    IssueModel model =  new IssueModel(title,desc,status,by);
                    IssueModelList.add(model);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Collections.reverse(IssueModelList);
        Collections.reverse(IssueIdList);

        IssueAdapter adapter = new IssueAdapter(getApplicationContext(),IssueModelList,IssueRef,IssueIdList);
        IssueRecyclerView.setAdapter(adapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            startActivity(new Intent(IssueActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(this,MainActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    
    private class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

        Context ctx;
        List<IssueModel> IssueList;
        DatabaseReference Ref;
        List<String> IdList;

        public IssueAdapter(Context ctx, List<IssueModel> issueList, DatabaseReference ref, List<String> idList) {
            this.ctx = ctx;
            IssueList = issueList;
            Ref = ref;
            IdList = idList;
        }

        @NonNull
        @Override
        public IssueAdapter.IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View IssueView = LayoutInflater.from(ctx).inflate(R.layout.issue_item,parent,false);
            return new IssueAdapter.IssueViewHolder(IssueView);
        }

        @Override
        public void onBindViewHolder(@NonNull IssueAdapter.IssueViewHolder holder, final int position) {

            holder.IssueCount.setText("#"+position);
            holder.IssueTitle.setText(IssueList.get(position).getTitle());
            holder.IssueDesc.setText(IssueList.get(position).getDesc());
            holder.IssueBy.setText(String.format("@ %s", IssueList.get(position).getBy()));
            if(IssueList.get(position).getStatus().equals("solved"))
            {
                holder.IssueStatus.setVisibility(View.VISIBLE);
            }


        }

        @Override
        public int getItemCount() {
            return IssueList.size();
        }

        public class IssueViewHolder extends RecyclerView.ViewHolder {

            TextView IssueCount , IssueTitle ,IssueDesc ,IssueBy ;
            ImageButton IssueStatus;
            
            public IssueViewHolder(View itemView) {
                super(itemView);

                IssueCount = itemView.findViewById(R.id.issue_count);
                IssueTitle = itemView.findViewById(R.id.issue_title);
                IssueDesc = itemView.findViewById(R.id.issue_desc);
                IssueBy = itemView.findViewById(R.id.issue_by);
                IssueStatus = itemView.findViewById(R.id.issue_status);
            }
        }
    }
}
