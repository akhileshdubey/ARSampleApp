package com.example.intelligrape.arsampeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ((Button) findViewById(R.id.arECommerce)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, VirtualAssistantHome.class));

            }
        });

        ((Button) findViewById(R.id.arVSponser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, VirtualSponsorHome.class));

            }
        });

        ((Button) findViewById(R.id.aboutUs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, AboutUs.class));

            }
        });

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}