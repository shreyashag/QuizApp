package in.shreyashagarwal.userinput_3_quizapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.lang.reflect.Array;

import static in.shreyashagarwal.userinput_3_quizapp.R.id.parent;

public class MainActivity extends AppCompatActivity {
    public String url="";
    public int openTriviaID;
    public String diffLevel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner quizCategorySpinner = (Spinner)findViewById(R.id.quiz_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,R.array.quiz_category,R.layout.support_simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        quizCategorySpinner.setAdapter(categoryAdapter);

        final RadioGroup diffLevelGroup = (RadioGroup) findViewById(R.id.diff_radio);
        Button startQuiz =(Button) findViewById(R.id.start_quiz_button);

        quizCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                String selected = (String) parent.getItemAtPosition(pos).toString();
                Toast.makeText(MainActivity.this,selected+" selected as category", Toast.LENGTH_SHORT).show();
                switch (selected){
                    case "General Knowledge":
                        openTriviaID=9;

                        break;

                    case "Music":
                        openTriviaID=12;

                        break;

                    case "Movies":
                        openTriviaID=11;

                        break;

                    case "Sports":
                        openTriviaID=21;

                        break;

                    case "TV":
                        openTriviaID=14;

                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        diffLevelGroup.clearCheck();
        diffLevelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = diffLevelGroup.getCheckedRadioButtonId();
                RadioButton selectedDiff = (RadioButton)findViewById(selectedId);
                diffLevel = (String) selectedDiff.getText().toString().toLowerCase();
                Toast.makeText(MainActivity.this,diffLevel, Toast.LENGTH_SHORT).show();
            }
        });

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (diffLevel.equals("any")){
                    url="https://opentdb.com/api.php?amount=10&category="+openTriviaID;
                }
                else{
                    url="https://opentdb.com/api.php?amount=10&category="+openTriviaID+"&difficulty="+diffLevel;
                }
//                Toast.makeText(MainActivity.this,url, Toast.LENGTH_SHORT).show();
                //Show loading screen which should load the quizActivity in background
                Intent intent = new Intent(MainActivity.this,QuizActivity.class);
                intent.putExtra("URL",url);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //ask the user if he really wants to quit or go by android guidelines?
    }

}
