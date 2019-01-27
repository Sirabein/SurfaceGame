package ru.pavlenty.surfacegame2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // play image button
    private ImageButton buttonPlay;
    //high score button
    private ImageButton buttonScore;

    private Switch aSwitch;

    private boolean hitBoxes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        buttonScore = (ImageButton) findViewById(R.id.buttonScore);
        aSwitch = (Switch) findViewById(R.id.switch1);

        if (getIntent().hasExtra("hitBoxes")) {
            hitBoxes = (boolean) getIntent().getExtras().get("hitBoxes");
            aSwitch.setChecked(hitBoxes);
        }

        buttonPlay.setOnClickListener(this);
        buttonScore.setOnClickListener(this);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    hitBoxes = true;
                } else hitBoxes = false;
            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v == buttonPlay) {
            Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
            gameIntent.putExtra("hitBoxes", hitBoxes);
            startActivity(gameIntent);
        }

        if (v == buttonScore) {
            startActivity(new Intent(MainActivity.this, HightScore.class));
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы хотите выйти?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        GameView.stopMusic();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
