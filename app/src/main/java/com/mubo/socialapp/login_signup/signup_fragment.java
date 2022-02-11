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

import com.mubo.socialapp.R;
import com.mubo.socialapp.api.ApiClass;
import com.mubo.socialapp.api.TokenOp;
import com.mubo.socialapp.helpers.Ayarlar;
import com.mubo.socialapp.helpers.interfaces;
import com.mubo.socialapp.main.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signup_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signup_fragment extends Fragment implements interfaces.button_click,interfaces.checkToken {

    public signup_fragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static signup_fragment newInstance() {
        signup_fragment fragment = new signup_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText username,email,password,repassword;
    FrameLayout frm;
    TokenOp to;
    interfaces.checkToken ct;
    Ayarlar ayarlar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_signup_fragment, container, false);
        username = v.findViewById(R.id.username);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.pass);
        repassword = v.findViewById(R.id.re_pass);
        frm=v.findViewById(R.id.frm);
        ct=this;
        return v;
    }

    @Override
    public void bt_click() {
       String u,e,p,r;
       u=username.getText().toString().trim();
       e=email.getText().toString().trim();
       p=password.getText().toString().trim();
       r=repassword.getText().toString().trim();
       String[] ets={u,e,p,r};
       boolean ok=true;
       for(String s: ets){
           if(s.isEmpty()){
               ok=false;
               Toast.makeText(getContext(), "Please enter your information completely!", Toast.LENGTH_SHORT).show();
                break;
           }
       }
       if(!p.equals(r)){
           ok=false;
           Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
       }else if(p.length()<8){
           ok=false;
           Toast.makeText(getContext(), "Password must be 8 character at least!", Toast.LENGTH_SHORT).show();
       }
       if(ok){
           signup(ets);
       }
    }
    //You should use the "addUniqueCollection" method for the registration process.
    // Of course, if you do not want users with the same username or e-mail address to be created. :)


    // Genetous is a generic method-based generic application and enables a method to be used
    // for more than one purpose.
    // Exceptional operations should be defined to escape token check inside method permissions.
    // The type field in this operation is used to specify the defined exceptional
    // situation to escape the token check.
    void signup(String[] ets){
        ayarlar=new Ayarlar(getContext());
        ayarlar.remove_pref("id");
        JSONObject jo=new JSONObject();
        long createdDate=System.currentTimeMillis();
        try {
            jo.put("collectionName","user");
            JSONObject content=new JSONObject();
            content.put("user_username",ets[0]);
            content.put("user_email",ets[1]);
            content.put("user_pass",ets[2]);
            content.put("user_image"," ");
            content.put("user_coverimage"," ");
            content.put("user_devicetoken"," ");
            content.put("user_isActive",true);
            content.put("user_createdDate",createdDate);
            content.put("role","user");
            JSONArray ufs=new JSONArray();
            ufs.put("user_username");
            ufs.put("user_email");
            content.put("uniqueFields",ufs);
            jo.put("content",content);
            jo.put("type","register");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiClass ac=new ApiClass();
        ac.addUniqueCollection(jo,frm,hnd,getActivity());
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
                        to=new TokenOp(getContext(),ct);
                        to.verify();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   /* Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getContext(),Home.class);
                    getActivity().startActivity(i);*/
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