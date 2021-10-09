package com.example.contactapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.contactapp.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    // create initial
    private void initial(Contact contact) {
        binding.tvFullname.setText(contact.getFullname());
        binding.tvFullname.setTextColor(contact.getColor());
        binding.tvPhonenumber.setText(contact.getMobile());
        if (contact.getAvt() != null) {
            binding.ivAvatarDetails.setImageBitmap(DataConvert.convertByteArrayToImage(contact.getAvt()));
        }
        binding.ivAvatarDetails.setBackgroundColor(contact.getColor());
        binding.ivIconMessage.setColorFilter(contact.getColor());
        binding.ivIconPhone.setColorFilter(contact.getColor());
        if (contact.getEmail() != null) {
            binding.tvEmailDetail.setText(contact.getEmail());
        }
        binding.tvAboutname.setText("About " + contact.getFullname());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setup data binding
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ImageView btnEdit = findViewById(R.id.btn_Edit);
        ImageButton btnCloseDetail = findViewById(R.id.btn_CloseDetail);

      //set actionBar
        Toolbar myToolbar = findViewById(R.id.detail_bar);
        setSupportActionBar(myToolbar);

        // get intent from main acti
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        Contact contact = (Contact) bundle.get("contact");

        // put data into inital
        initial(contact);

        // redirect to edit
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Edit_contact_activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contactEdit", contact);
                System.out.println(contact.toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnCloseDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }


}