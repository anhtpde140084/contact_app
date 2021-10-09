package com.example.contactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contactapp.databinding.ActivityAddContactBinding;
import com.example.contactapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddContactActivity extends AppCompatActivity {
    private ActivityAddContactBinding binding;

    private AppDatabase appDatabase;
    private ContactDAO contactDAO;
    private ArrayList<Contact> contactList;

    // declare variable
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private byte [] avt;
    private int color;

    private static final int CAMERA_INTENT=100;
    ImageView ivAddContact;
    Uri imageUri;
    Bitmap bitmap;

    private int randomColor(){
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void resetEditText(){
        binding.editTextFirstName.setText("");
        binding.editTextLastName.setText("");
        binding.editTextMobile.setText("");
        binding.editTextEmail.setText("");
    }

    private void getEditText(){
        firstName = binding.editTextFirstName.getText().toString();
        lastName = binding.editTextLastName.getText().toString();
        mobile = binding.editTextMobile.getText().toString();
        email = binding.editTextEmail.getText().toString();
    }

    private boolean validate(String firstName, String lastName, String mobile, String Email){
        if(firstName.matches("")){
            Toast.makeText(getApplicationContext(), "First name is required",Toast.LENGTH_LONG).show();
            return false;
        }if(lastName.matches("")){
            Toast.makeText(getApplicationContext(), "Last name is required",Toast.LENGTH_LONG).show();
            return false;
        }if(mobile.matches("")){
            Toast.makeText(getApplicationContext(), "Phone number is required",Toast.LENGTH_LONG).show();
            return false;
        }if(!email.matches("")&&!email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            Toast.makeText(getApplicationContext(), "Email is wrong format",Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    public void goToMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void takePicture(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, CAMERA_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                ivAddContact.setImageBitmap(bitmap);

            } else
                ivAddContact.setImageURI(imageUri);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Button btnSave = (Button) findViewById(R.id.btn_Save);
        ImageButton btnClose = (ImageButton) findViewById(R.id.btn_Close);
        ImageView ivCamera = (ImageView) findViewById(R.id.iv_camera);
        ivAddContact = (ImageView) findViewById(R.id.iv_add_contact);

        //set actionBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(view);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getEditText();
                color = randomColor();
                try{
                    avt = DataConvert.convertImageToByteArray(ivAddContact);
                }catch (Exception e){

                }

                if(validate(firstName, lastName, mobile, email)){
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase = AppDatabase.getInstance(getApplicationContext());
                            contactDAO = appDatabase.contactDAO();
                            contactList = (ArrayList<Contact>) contactDAO.getAll();

                            contactDAO.insert(new Contact(firstName, lastName, mobile, email, avt, color));
                            resetEditText();
                            goToMain();
                        }

                    });
                }
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });

    }
}