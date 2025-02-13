package com.example.therapistbluelock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MuscleStrength extends AppCompatActivity {

    private View clickableLeftArm;
    private View rrc1, rrc2, rrc3, rrc4, rrc5, rrc6, rrc7, rrc8, rrc9;
    private View rlc1, rlc2, rlc3, rlc4, rlc5;
    private View lc1, lc2, lc3, lc4, lc5, lc6, lc7, lc8, lc9;
    private View llc1, llc2, llc3, llc4, llc5;

    private ImageView rr1, rr2, rr3, rr4, rr5, rr6, rr7, rr8, rr9;
    private ImageView rrl1, rrl2, rrl3, rrl4, rrl5;
    private ImageView ll1, ll2, ll3, ll4, ll5, ll6, ll7, ll8, ll9;
    private ImageView lll1, lll2, lll3, lll4, lll5;

    private ImageView bodyOutline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_strength);
        hideSystemUI();
        // Initialize views
        Button submitButton = findViewById(R.id.complete_button);

        rrc1 = findViewById(R.id.clickable_r1);
        rrc2 = findViewById(R.id.clickable_r2);
        rrc3 = findViewById(R.id.clickable_r3);
        rrc4 = findViewById(R.id.clickable_r4);
        rrc5 = findViewById(R.id.clickable_r5);
        rrc6 = findViewById(R.id.clickable_r6);
        rrc7 = findViewById(R.id.clickable_r7);
        rrc8 = findViewById(R.id.clickable_r8);
        rrc9 = findViewById(R.id.clickable_r9);

        rlc1 = findViewById(R.id.clickable_rl1);
        rlc2 = findViewById(R.id.clickable_rl2);
        rlc3 = findViewById(R.id.clickable_rl3);
        rlc4 = findViewById(R.id.clickable_rl4);
        rlc5 = findViewById(R.id.clickable_rl5);

        lc1 = findViewById(R.id.clickable_l1);
        lc2 = findViewById(R.id.clickable_l2);
        lc3 = findViewById(R.id.clickable_l3);
        lc4 = findViewById(R.id.clickable_l4);
        lc5 = findViewById(R.id.clickable_l5);
        lc6 = findViewById(R.id.clickable_l6);
        lc7 = findViewById(R.id.clickable_l7);
        lc8 = findViewById(R.id.clickable_l8);
        lc9 = findViewById(R.id.clickable_l9);

        llc1 = findViewById(R.id.clickable_ll1);
        llc2 = findViewById(R.id.clickable_ll2);
        llc3 = findViewById(R.id.clickable_ll3);
        llc4 = findViewById(R.id.clickable_ll4);
        llc5 = findViewById(R.id.clickable_ll5);

        rr1 = findViewById(R.id.r1);
        rr2 = findViewById(R.id.r2);
        rr3 = findViewById(R.id.r3);
        rr4 = findViewById(R.id.r4);
        rr5 = findViewById(R.id.r5);
        rr6 = findViewById(R.id.r6);
        rr7 = findViewById(R.id.r7);
        rr8 = findViewById(R.id.r8);
        rr9 = findViewById(R.id.r9);

        rrl1 = findViewById(R.id.rl1);
        rrl2 = findViewById(R.id.rl2);
        rrl3 = findViewById(R.id.rl3);
        rrl4 = findViewById(R.id.rl4);
        rrl5 = findViewById(R.id.rl5);

        ll1 = findViewById(R.id.l1);
        ll2 = findViewById(R.id.l2);
        ll3 = findViewById(R.id.l3);
        ll4 = findViewById(R.id.l4);
        ll5 = findViewById(R.id.l5);
        ll6 = findViewById(R.id.l6);
        ll7 = findViewById(R.id.l7);
        ll8 = findViewById(R.id.l8);
        ll9 = findViewById(R.id.l9);

        lll1 = findViewById(R.id.ll1);
        lll2 = findViewById(R.id.ll2);
        lll3 = findViewById(R.id.ll3);
        lll4 = findViewById(R.id.ll4);
        lll5 = findViewById(R.id.ll5);

        bodyOutline = findViewById(R.id.image_body);
        String itemType = getIntent().getStringExtra("itemType");
        String itemTitle = getIntent().getStringExtra("itemTitle");
        // Handle clickable items
        setOnClickListeners();

        // Set up click listener for SUBMIT button to navigate to DetailFrag_5
        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MuscleStrength.this, DetailFrag_5.class);
            // Pass the updated values back
            intent.putExtra("itemTitle", itemTitle);
            intent.putExtra("itemStatus", "Completed");
            intent.putExtra("itemColor", Color.GREEN);

            // Start the AssessmentList activity with the updated data
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void setOnClickListeners() {
        rrc1.setOnClickListener(v -> rr1.setVisibility(View.VISIBLE));
        rrc2.setOnClickListener(v -> rr2.setVisibility(View.VISIBLE));
        rrc3.setOnClickListener(v -> rr3.setVisibility(View.VISIBLE));
        rrc4.setOnClickListener(v -> rr4.setVisibility(View.VISIBLE));
        rrc5.setOnClickListener(v -> rr5.setVisibility(View.VISIBLE));
        rrc6.setOnClickListener(v -> rr6.setVisibility(View.VISIBLE));
        rrc7.setOnClickListener(v -> rr7.setVisibility(View.VISIBLE));
        rrc8.setOnClickListener(v -> rr8.setVisibility(View.VISIBLE));
        rrc9.setOnClickListener(v -> rr9.setVisibility(View.VISIBLE));

        rlc1.setOnClickListener(v -> rrl1.setVisibility(View.VISIBLE));
        rlc2.setOnClickListener(v -> rrl2.setVisibility(View.VISIBLE));
        rlc3.setOnClickListener(v -> rrl3.setVisibility(View.VISIBLE));
        rlc4.setOnClickListener(v -> rrl4.setVisibility(View.VISIBLE));
        rlc5.setOnClickListener(v -> rrl5.setVisibility(View.VISIBLE));

        lc1.setOnClickListener(v -> ll1.setVisibility(View.VISIBLE));
        lc2.setOnClickListener(v -> ll2.setVisibility(View.VISIBLE));
        lc3.setOnClickListener(v -> ll3.setVisibility(View.VISIBLE));
        lc4.setOnClickListener(v -> ll4.setVisibility(View.VISIBLE));
        lc5.setOnClickListener(v -> ll5.setVisibility(View.VISIBLE));
        lc6.setOnClickListener(v -> ll6.setVisibility(View.VISIBLE));
        lc7.setOnClickListener(v -> ll7.setVisibility(View.VISIBLE));
        lc8.setOnClickListener(v -> ll8.setVisibility(View.VISIBLE));
        lc9.setOnClickListener(v -> ll9.setVisibility(View.VISIBLE));

        llc1.setOnClickListener(v -> lll1.setVisibility(View.VISIBLE));
        llc2.setOnClickListener(v -> lll2.setVisibility(View.VISIBLE));
        llc3.setOnClickListener(v -> lll3.setVisibility(View.VISIBLE));
        llc4.setOnClickListener(v -> lll4.setVisibility(View.VISIBLE));
        llc5.setOnClickListener(v -> lll5.setVisibility(View.VISIBLE));
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
    }
}
