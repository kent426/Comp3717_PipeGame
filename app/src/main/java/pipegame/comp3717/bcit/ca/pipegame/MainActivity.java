package pipegame.comp3717.bcit.ca.pipegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ScoreOnClick(final View view) {
        startActivity(new Intent("android.intent.action.SCORE"));
    }

    public void GameOnClick(final View view) {
        startActivity(new Intent("android.intent.action.GAME"));
    }

    public void LevelClick(final View view) {
        startActivity(new Intent("android.intent.action.LevelSelect"));
    }


}
