package in.shreyashagarwal.userinput_3_quizapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public String url="";
    public int openTriviaID;
    public String diffLevel = "";
    public String token;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        token= sharedPref.getString(getString(R.string.quiz_token),"");
        if (token.equals("")) {
            Log.d("TOKEN from Resources", "EMPTY");
        }
        else {

            Log.d("TOKEN from Resources", token);
        }

        if(token.equals("")){
            JsonObjectRequest getToken = new JsonObjectRequest(Request.Method.GET, "https://opentdb.com/api_token.php?command=request", null, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response){
                    try {
                        if (response.getString("response_code").equals("0")) {
                            //do something here
                            token= response.getString("token");
                            Log.d("Token Fetched from web", token);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.quiz_token), token);
                            editor.commit();
                            Log.d("WRITTEN token", "to preferences ");

                        }
                        else if (response.getString("response_code").equals("3")) {
                            //Token not found, request the token?
                            String requestTokenUrl="https://opentdb.com/api_token.php?command=request";
                            JsonObjectRequest requestToken = new JsonObjectRequest(requestTokenUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                        }
                        else if (response.getString("response_code").equals("4")) {
                            //resetting the token is necessary
                            String resetTokenUrl = "https://opentdb.com/api_token.php?command=reset&token"+token;
                            JsonObjectRequest resetToken= new JsonObjectRequest(resetTokenUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("TOKEN", "Reset! ");

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                        }

                    } catch (Exception e){

                    }

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub

                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(getToken);
        }
        else{
            //check Token Validity as it already exists, or reset it
            Log.d("Checking token validity","True");
            final RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest checkToken = new JsonObjectRequest(Request.Method.GET, "https://opentdb.com/api.php?amount=10&token="+token, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", response.toString());
                    try{
                        if (response.getString("response_code").equals("0")){

                        }
                        else{
                            Log.d("Fetching new Token","True");
                            String requestTokenUrl="https://opentdb.com/api_token.php?command=request";
                            JsonObjectRequest requestToken = new JsonObjectRequest(requestTokenUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        token=response.getString("token");
                                        Log.d("Got new token ",token);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString(getString(R.string.quiz_token), token);
                                        editor.commit();
                                        Log.d("WRITTEN token", "to preferences ");
                                    } catch (Exception e){
                                        Log.e("Error", e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            queue.add(requestToken);
                        }
                    } catch (Exception e){

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(checkToken);
        }

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
            }
        });

        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diffLevel.equals("any")){
                    url="https://opentdb.com/api.php?amount=10&category="+openTriviaID+"&token="+token;
                }
                else{
                    url="https://opentdb.com/api.php?amount=10&category="+openTriviaID+"&difficulty="+diffLevel+"&token="+token;
                }

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
