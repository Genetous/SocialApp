package com.mubo.socialapp.login_signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class loginSignUp extends AppCompatActivity {

    LinearLayout log_lin,sign_lin;
    FragmentManager fragmentManager;
    LinearLayout.LayoutParams p;
    Button log_sign;
    Activity act;
    Context cx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        int like=R.drawable.full_heart;
        int follow=R.drawable.notification_person;

        act=this;
        cx=this;
        fragmentManager = getSupportFragmentManager();
        login_fragment f= login_fragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.frag,f, null)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
        log_lin=findViewById(R.id.log_lin);
        sign_lin=findViewById(R.id.sign_lin);
        log_lin.setOnTouchListener(tl);
        sign_lin.setOnTouchListener(tl);
        relTab=findViewById(R.id.bottom_rel);
        log_sign=findViewById(R.id.log_sign);
        log_sign.setOnClickListener(log_sign_cl);
        p= (LinearLayout.LayoutParams) relTab.getLayoutParams();
        log_lin.postDelayed(new Runnable() {
            @Override
            public void run() {
                p.setMargins(20,0,0,0);
                p.width = (int) (log_lin.getWidth()*0.5);
                relTab.setLayoutParams(p);
            }
        },100);
    }
    TextView tw;
    RelativeLayout relTab;
    int selected_lin=R.id.log_lin;
    boolean notgo=false;
    Fragment f;
    View.OnTouchListener tl=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if(view.getId() == R.id.log_lin) {
                    tw=view.findViewById(R.id.login);
                } else {
                    tw=view.findViewById(R.id.signup);
                }
                if(selected_lin == view.getId())
                    notgo=true;
                else
                    notgo=false;
                selected_lin= view.getId();
                tw.setTextColor(getColor(R.color.white));
            }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if(!notgo) {
                    TextView t;
                    RelativeLayout rt;
                    if (selected_lin == R.id.log_lin) {
                        t = sign_lin.findViewById(R.id.signup);
                        login_fragment f = login_fragment.newInstance();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.frag, f, null)
                                .commit();
                    } else {
                        t = log_lin.findViewById(R.id.login);
                        signup_fragment f = signup_fragment.newInstance();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.frag, f, null)
                                .commit();
                    }
                    t.setTextColor(getColor(R.color.white_30));
                    tw.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_text_cap), act));
                    t.setTextSize(statics.pixelsToSp(getResources().getDimension(R.dimen.tab_text_low), act));
                    p = (LinearLayout.LayoutParams) relTab.getLayoutParams();
                    log_sign.setText(tw.getText());
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
    View.OnClickListener log_sign_cl=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            androidx.fragment.app.Fragment ff = fragmentManager.findFragmentById(R.id.frag);
            if(ff instanceof signup_fragment)
                ((signup_fragment) ff).bt_click();
            else if(ff instanceof login_fragment)
                ((login_fragment) ff).bt_click();
        }
    };
    Animation a = new Animation() {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            relTab.setLayoutParams(p);
        }
    };

}