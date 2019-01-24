package integrals.inlens.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import integrals.inlens.Activities.IntroActivity;
import integrals.inlens.Activities.LoginActivity;
import integrals.inlens.Activities.RegisterUser;
import integrals.inlens.R;

public class IntroSlide5Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro_slide5, container, false);

       TextView AppTagLine =  view.findViewById(R.id.apptagline);
       AppTagLine.setText("\" Event memories with your loved ones in a single Cloud-Album \"");

       view.findViewById(R.id.introsignin).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getContext(), LoginActivity.class));
               getActivity().overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
               getActivity().finish();
           }
       });

        view.findViewById(R.id.introsignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RegisterUser.class));
                getActivity().overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
                getActivity().finish();
            }
        });

        return view;
    }
}
