package com.example.contactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.contactapp.databinding.ActivityEditContactBinding;

import java.util.ArrayList;

public class Edit_contact_activity extends AppCompatActivity {
    ActivityEditContactBinding binding;

    //set up variable
    private AppDatabase appDatabase;
    private ContactDAO contactDAO;
    private ArrayList<Contact> contactList;
    private Contact newContact;

    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private byte [] avt;

    // create request_code camera
    private static final int CAMERA_INTENT=100;
    ImageView ivAddContactEdit;
    Uri imageUri;
    Bitmap bitmap;

    // set up initial
    private void initial(Contact contact){
        binding.ivAddContactEdit.setImageBitmap(DataConvert.convertByteArrayToImage(contact.getAvt()));
        binding.editTextFirstNameEdit.setText(contact.getFirstName());
        binding.editTextLastNameEdit.setText(contact.getLastName());
        binding.editTextMobileEdit.setText(contact.getMobile());
        binding.editTextEmailEdit.setText(contact.getEmail());
    }

    // get data item
    private void getEditText(){
        firstName = binding.editTextFirstNameEdit.getText().toString();
        lastName = binding.editTextLastNameEdit.getText().toString();
        mobile = binding.editTextMobileEdit.getText().toString();
        email = binding.editTextEmailEdit.getText().toString();
        try{
            avt = DataConvert.convertImageToByteArray(binding.ivAddContactEdit);
        }catch (Exception e){
        }
    }

    // valid
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

    // open picture on store
    public void openPicture(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, CAMERA_INTENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                // put data for image
                ivAddContactEdit.setImageBitmap(bitmap);
            } else
                ivAddContactEdit.setImageURI(imageUri);

        }

    }

    //on create start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.edit_bar);
        setSupportActionBar(myToolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        newContact = (Contact) bundle.get("contactEdit");

        initial(newContact);

        ivAddContactEdit = (ImageView) findViewById(R.id.iv_add_contactEdit);

        // click open camera store
        ImageView ivCameraEdit = (ImageView) findViewById(R.id.iv_cameraEdit);
        ivCameraEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPicture(view);
            }
        });

        // save new edit data
        Button btnSaveEdit = (Button) findViewById(R.id.btn_SaveEdit);
        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditText();

                if(validate(firstName, lastName, mobile, email)){
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase = AppDatabase.getInstance(getApplicationContext());
                            contactDAO = appDatabase.contactDAO();
                            contactList = (ArrayList<Contact>) contactDAO.getAll();

                            getEditText();
                            newContact.setFirstName(firstName);
                            newContact.setLastName(lastName);
                            newContact.setMobile(mobile);
                            newContact.setEmail(email);
                            newContact.setAvt(avt);
                            newContact.setColor(newContact.getColor());
                            contactDAO.update(newContact);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        // close edit screen
        ImageButton btnCloseEdit = (ImageButton) findViewById(R.id.btn_CloseEdit);
        btnCloseEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contact",newContact);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}