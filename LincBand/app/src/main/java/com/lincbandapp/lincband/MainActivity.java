package com.lincbandapp.lincband;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // initialize buttons
        ImageButton viewChainButton = (ImageButton) findViewById(R.id.viewChainButton);
        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetoothButton);
        ImageButton tutorialButton = (ImageButton) findViewById(R.id.tutorialButton);

        //set up button listeners
        viewChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewChain = new Intent(getApplicationContext(),ChainListActivity.class);
                startActivity(viewChain);
            }
        });
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bluetooth = new Intent(getApplicationContext(),EnableBTActivity.class);
                startActivity(bluetooth);
            }
        });
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tutorial = new Intent(getApplicationContext(),Tutorial_1_Activity.class);
                startActivity(tutorial);
            }
        });
    }
}
