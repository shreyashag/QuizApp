package in.shreyashagarwal.userinput_3_quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Button restart=(Button)findViewById(R.id.restartButton);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMainActivity=new Intent(ScoreActivity.this,MainActivity.class);
                startActivity(startMainActivity);
            }
        });

        Intent recievedIntent = getIntent();
        Bundle bd = recievedIntent.getExtras();
        int score=0;
        if(bd != null)
        {
            score = (int) bd.get("correct");
        }
        TextView ScoreView=(TextView)findViewById(R.id.scoreView);
        ScoreView.setText( (score+"/10").toString());
    }

    @Override
    public void onBackPressed() {
        Intent startMainActivity=new Intent(ScoreActivity.this,MainActivity.class);
        startActivity(startMainActivity);
    }


}
