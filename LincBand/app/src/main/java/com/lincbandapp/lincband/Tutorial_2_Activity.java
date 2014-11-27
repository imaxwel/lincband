package com.lincbandapp.lincband;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;


public class Tutorial_2_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial_2_);

        RelativeLayout r1 = (RelativeLayout)findViewById(R.id.tutorial2layout);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Tutorial_3_Activity.class);
                startActivity(i);
            }
        });
    }
}
