package pipegame.comp3717.bcit.ca.pipegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LevelSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void levelSelect(final View view) {
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        Intent tolevel = new Intent(this, GameActivity.class);
        Log.d("hhhh", buttonText);
        int le = Integer.parseInt(buttonText);
        tolevel.putExtra("level",le);
        startActivity(tolevel);
    }


}
