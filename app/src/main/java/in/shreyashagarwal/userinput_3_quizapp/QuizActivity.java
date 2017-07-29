package in.shreyashagarwal.userinput_3_quizapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;


public class QuizActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    TextView questionTV;
    TextView questionNumberTV;
    TextView scoreTV;
    JSONArray results;
    String correctString;
    int correctPos;
    int selectedOption;
    int questionNumber=0;
    int correct=0;
    MediaPlayer correctSound,incorrectSound,gameloop;
    Vibrator vibrator;

    public void checkAnswer(){
//        if (selectedString.equals(correctString)){
        if (selectedOption==correctPos){
            correct++;
            gameloop.pause();
            correctSound.start();
            long[] correctVibrate= {0, 200, 100, 200};
            vibrator.vibrate(correctVibrate,-1);
            gameloop.start();
//            Toast.makeText(QuizActivity.this,"Correct!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            gameloop.pause();
            incorrectSound.start();
            long[] incorrectVibrate= {0, 500};
            vibrator.vibrate(incorrectVibrate,-1);
            gameloop.start();
//            Toast.makeText(QuizActivity.this,"Wrong!", Toast.LENGTH_SHORT).show();
        }
        if (questionNumber<9){
            questionNumber++;
            loadNextQuestion();
        }
        else {
            Intent scoreActivity=new Intent(QuizActivity.this,ScoreActivity.class);
            gameloop.pause();
            scoreActivity.putExtra("correct",correct);
            startActivity(scoreActivity);
        }

    }

    public void loadNextQuestion(){
        JSONObject question_data;
        // load first question into view
        try{
            question_data=results.getJSONObject(questionNumber);
            String questionString=question_data.getString("question");
            correctString=question_data.getString("correct_answer");

            questionTV.setText(fromHtml(questionString).toString());

            Random r = new Random();
            int correct_position = 0;

            ArrayList<Integer> positionsList=new ArrayList<>();
            positionsList.add(0);
            positionsList.add(1);
            positionsList.add(2);
            positionsList.add(3);

            Collections.shuffle(positionsList);

            //Need a method to randomize correct answer location
            correct_position=positionsList.get(0);
            correctPos=correct_position;
            ((RadioButton)radioGroup.getChildAt(correct_position)).setText(correctString);

            if (question_data.getString("type").equals("multiple")) {

                //load mcq incorrect options
                String incorrect_answers_string[]= new String[3];
                (radioGroup.getChildAt(positionsList.get(0))).setVisibility(View.VISIBLE);
                (radioGroup.getChildAt(positionsList.get(1))).setVisibility(View.VISIBLE);
                (radioGroup.getChildAt(positionsList.get(2))).setVisibility(View.VISIBLE);
                (radioGroup.getChildAt(positionsList.get(3))).setVisibility(View.VISIBLE);

                JSONArray incorrect_answers=question_data.getJSONArray("incorrect_answers");
                incorrect_answers_string[0]=incorrect_answers.getString(0);
                incorrect_answers_string[1]=incorrect_answers.getString(1);
                incorrect_answers_string[2]=incorrect_answers.getString(2);

                for(int j=0; j<3;j++){
                    ((RadioButton)radioGroup.getChildAt(positionsList.get(j+1))).setText(fromHtml(incorrect_answers_string[j]).toString());
                }
                ((RadioButton)radioGroup.getChildAt(0)).setText(fromHtml("A. "+ ((RadioButton) radioGroup.getChildAt(0)).getText().toString()).toString());
                ((RadioButton)radioGroup.getChildAt(1)).setText(fromHtml("B. "+ ((RadioButton) radioGroup.getChildAt(1)).getText().toString()).toString());
                ((RadioButton)radioGroup.getChildAt(2)).setText(fromHtml("C. "+ ((RadioButton) radioGroup.getChildAt(2)).getText().toString()).toString());
                ((RadioButton)radioGroup.getChildAt(3)).setText(fromHtml("D. "+ ((RadioButton) radioGroup.getChildAt(3)).getText().toString()).toString());
            }
            else if (question_data.getString("type").equals("boolean")){
                //Hide the other two buttons
                (radioGroup.getChildAt(positionsList.get(0))).setVisibility(View.VISIBLE);
                (radioGroup.getChildAt(positionsList.get(1))).setVisibility(View.VISIBLE);
                (radioGroup.getChildAt(positionsList.get(2))).setVisibility(View.GONE);
                (radioGroup.getChildAt(positionsList.get(3))).setVisibility(View.GONE);

                String incorrect_answers_string= new String();
                JSONArray incorrect_answers=question_data.getJSONArray("incorrect_answers");
                incorrect_answers_string=incorrect_answers.getString(0);

                ((RadioButton)radioGroup.getChildAt(positionsList.get(1))).setText(fromHtml(incorrect_answers_string).toString());

                //Not needed for two options
//                ((RadioButton)radioGroup.getChildAt(0)).setText("A. "+ ((RadioButton) radioGroup.getChildAt(0)).getText().toString());
//                ((RadioButton)radioGroup.getChildAt(positionsList.get(1))).setText("B. "+ ((RadioButton) radioGroup.getChildAt(positionsList.get(1))).getText().toString());
            }
            questionNumberTV.setText("Question Number: "+(questionNumber+1)+"/10");
            scoreTV.setText("Score: "+ correct+"/"+(questionNumber));


        } catch(JSONException e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        correctSound=MediaPlayer.create(QuizActivity.this,R.raw.correct);
        incorrectSound=MediaPlayer.create(QuizActivity.this,R.raw.incorrect);
        gameloop= MediaPlayer.create(QuizActivity.this,R.raw.game_music);
        vibrator=(Vibrator) QuizActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        gameloop.start();
        gameloop.setLooping(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        radioGroup= (RadioGroup)findViewById(R.id.radiogroup);
        questionTV= (TextView)findViewById(R.id.questionTV);
        questionTV.setText("");
        questionNumberTV = (TextView)findViewById(R.id.questionNumberTV);
        scoreTV =(TextView)findViewById(R.id.scoreTV);
        Button nextButton = (Button)findViewById(R.id.nextButton);

        Intent recievedIntent = getIntent();
        Bundle bd = recievedIntent.getExtras();
        String url="";
        if(bd != null)
        {
            url = (String) bd.get("URL");
            Log.d("URL", "url recieved"+ url);

        }
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest getQuiz = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            // Show loading screen while the quiz loads
            @Override
            public void onResponse(JSONObject response) {
                //what to do after getting the response
                Log.d("Response",response.toString());
                try{
                    if (response.getString("response_code").equals("0")){
                        //do something here
                        results=response.getJSONArray("results");
                        loadNextQuestion();
                    }
                } catch(JSONException e){
                    throw new RuntimeException(e);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                selectedOption = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
                Log.d("selected", "selected option: "+ selectedOption);
            }
        });

        queue.add(getQuiz);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        if (gameloop!= null) {
            gameloop.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        gameloop.start();
    }

    @Override
    public void onBackPressed() {
        if (gameloop!= null) {
            gameloop.pause();
        }
        Toast.makeText(QuizActivity.this,"Can't go back now! Finish the quiz!",Toast.LENGTH_SHORT).show();
    }

}
