package pipegame.comp3717.bcit.ca.pipegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LevelSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
