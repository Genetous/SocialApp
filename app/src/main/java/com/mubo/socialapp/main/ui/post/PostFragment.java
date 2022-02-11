package com.mubo.socialapp.main.ui.post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mubo.socialapp.R;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.login_signup.login_fragment;
import com.mubo.socialapp.login_signup.signup_fragment;

public class PostFragment extends Fragment {

    LinearLayout text_lin,image_lin;
    FragmentManager fragmentManager;
    LinearLayout.LayoutParams p;
    View mainView;
    Button log_sign;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        log_sign=v.findViewById(R.id.log_sign);
        log_sign.setText("Create Text Post");
        log_sign.setOnClickListener(post_cl);
        text_lin=v.findViewById(R.id.text_lin);
        image_lin=v.findViewById(R.id.image_lin);
        text_lin.setOnTouchListener(tl);
        image_lin.setOnTouchListener(tl);
        relTab=v.findViewById(R.id.bottom_rel);
        fragmentManager = getActivity().getSupportFragmentManager();
        TextPostFragment f =TextPostFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.frag_post,f, null)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
        p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
        text_lin.postDelayed(new Runnable() {
            @Override
            public void run() {
                p.setMargins(20,0,0,0);
                p.width = (int) (text_lin.getWidth()*0.5);
                relTab.setLayoutParams(p);
            }
        },100);
        mainView=v;
        return v;
    }
    View.OnClickListener post_cl =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            androidx.fragment.app.Fragment ff = fragmentManager.findFragmentById(R.id.frag_post);
            if(ff instanceof ImagePostFragment)
                ((ImagePostFragment) ff).send();
            else  if(ff instanceof TextPostFragment)
                ((TextPostFragment) ff).send();
        }
    };
    TextView tw;
    RelativeLayout relTab;
    int selected_lin=R.id.text_lin;
    boolean notgo=false;
    View.OnTouchListener tl=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if(view.getId() == R.id.text_lin) {
                    tw=view.findViewById(R.id.text);
                } else {
                    tw=view.findViewById(R.id.image);
                }
                if(selected_lin == view.getId())
                    notgo=true;
                else
                    notgo=false;
                selected_lin= view.getId();
                tw.setTextColor(getActivity().getColor(R.color.white));
            }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if(!notgo) {
                    TextView t;
                    RelativeLayout rt;
                    if (selected_lin == R.id.text_lin) {
                        t = image_lin.findViewById(R.id.image);
                        TextPostFragment f = TextPostFragment.newInstance();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.frag_post, f, null)
                                .commit();
                    } else {
                        t = text_lin.findViewById(R.id.text);
                        ImagePostFragment f = ImagePostFragment.newInstance();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frag_post, f, null)
                                .commit();
                    }
                    t.setTextColor(getActivity().getColor(R.color.white_30));
                    tw.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_cap), getActivity()));
                    t.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_profile_text_low), getActivity()));
                    log_sign.setText(tw.getText());
                    p = (LinearLayout.LayoutParams) relTab.getLayoutParams();
                    tw.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View parent = (View) tw.getParent();
                            p.setMargins(((int) parent.getX() + 20), 0, 0, 0);
                            p.width = (int) (parent.getWidth() * 0.5);
                            a.setDuration(500);
                            relTab.startAnimation(a);

                        }
                    }, 150);

                }
            }
            return false;
        }
    };
    Animation a = new Animation() {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            relTab.setLayoutParams(p);
        }
    };
    public void setImagePath(String path){
        androidx.fragment.app.Fragment ff = fragmentManager.findFragmentById(R.id.frag_post);
        if(ff instanceof ImagePostFragment)
            ((ImagePostFragment) ff).setImage(path);
    }
}