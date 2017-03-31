package pipegame.comp3717.bcit.ca.pipegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pipegame.comp3717.bcit.ca.pipegame.BFS.IntersectionMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(!Singleton.isCreated()) {
            Singleton.getInstance(this);
            Singleton.getInstance(this).readAndConstruct();
        }

    }

    public void ScoreOnClick(final View view) {
        startActivity(new Intent("android.intent.action.SCORE"));
    }

    public void GameOnClick(final View view) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("level",1);
        startActivity(i);
    }

    public void LevelClick(final View view) {
        Intent l = new Intent("android.intent.action.LevelSelect");
        startActivity(l);
    }


}
