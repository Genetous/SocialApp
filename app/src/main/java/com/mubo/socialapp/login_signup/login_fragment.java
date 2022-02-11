package com.mubo.socialapp.login_signup;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mubo.socialapp.R;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.TokenOp;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.interfaces;
import com.mubo.socialapp.helpers.statics;
import com.mubo.socialapp.main.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login_fragment extends Fragment implements interfaces.button_click, interfaces.checkToken {


    public login_fragment() {

    }

    public static login_fragment newInstance() {
        login_fragment fragment = new login_fragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    EditText email,password;
    FrameLayout frm;
    TokenOp to;
    interfaces.checkToken ct;
    Ayarlar ayarlar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_fragment, container, false);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.pass);
        frm=v.findViewById(R.id.frm);
        ct=this;
        return v;
    }

    @Override
    public void bt_click() {
        String e,p;

        e=email.getText().toString().trim();
        p=password.getText().toString().trim();
        String[] ets={e,p};
        boolean ok=true;
        for(String s: ets){
            if(s.isEmpty()){
                ok=false;
                Toast.makeText(getContext(), "Please enter your information completely!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(ok){
            login(ets);
        }
    }
    //The Login Process consists of 3 steps.
    //1- Creating a guest token for using methods.
    //2- Finding the user to log in with "getCollection".
    //3- Creating tokens with user ID.
    void login(String[] ets){
        ayarlar=new Ayarlar(getContext());
        ayarlar.remove_pref("token");
        ayarlar.remove_pref("token_time");
        JSONObject jo=new JSONObject();
        try {
            jo.put("user_username",ets[0]);
            jo.put("user_pass",ets[1]);
            JSONArray ja =new JSONArray();
            ja.put("user_pass");
            jo.put("remove_fields",ja);
            jo.put("type","login");
            ApiClass ac=new ApiClass();
            ac.getCollection(jo,frm,hnd,getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Handler hnd=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    String sonuc=(String)msg.obj;
                    try {
                        JSONObject jo = new JSONObject(sonuc);
                        String id =jo.getString("id");
                        Ayarlar ayarlar=new Ayarlar(getContext());
                        ayarlar.set_pref_string("id",id);
                        ayarlar.remove_pref("token");
                        ayarlar.remove_pref("token_time");
                        to=new TokenOp(getContext(),ct);
                        to.verify();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void getToken(String token) {
        Intent i=new Intent(getContext(), Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(i);
    }
}