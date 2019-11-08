package com.example.demorest_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * 1- on Go button click
 *       make a call acc to 'httpMethodToCall' and URL specified in 'edittext_url'
 *           apply switch on 'httpMethodToCall'
 *       display the result in 'edittext_input_body'
 *  */
public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private Context context;

    Spinner spinner;
    EditText editText_url;
    EditText editText_input_body;
    EditText editText_outpput_body;

    //    RequestQueue requestQueue;
    Api api;
    String httpMethodToCall = "GET"; //default
    String[] httpMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        httpMethods = getResources().getStringArray(R.array.http_methods);
        initializeSpinner();
        editText_url = findViewById(R.id.edittext_url);
        editText_input_body = findViewById(R.id.edittext_input_body);
        editText_outpput_body = findViewById(R.id.edittext_output_body);

//        requestQueue = Volley.newRequestQueue(this);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);


        Button buttonGo = findViewById(R.id.button_go);
        buttonGo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = editText_url.getText().toString();
                Log.d(TAG, "Method : " + httpMethodToCall + "\n" + "URL : " + editText_url.getText().toString());
                Toast.makeText(context, "Method : " + httpMethodToCall + "\n" + "URL : " + url, Toast.LENGTH_LONG).show();

                switch (httpMethodToCall)
                {
                    case "GET":
                        makeGetRequest(url);
                        break;
                    case "POST":
                        makePostrequest(url);
                        break;
                    case "PUT":
                        makePutRequest(url);
                        break;
                    case "DELETE":
                        makeDeleteRequest(url);
                        break;
                }
            }
        });
    }

    public void makeGetRequest(final String url)
    {
        Call<List<Student>> getStudentsCall = api.getAll(url);


        getStudentsCall.enqueue(new Callback<List<Student>>()
        {
            @Override
            public void onResponse(Call<List<Student>> call, retrofit2.Response<List<Student>> response)
            {
                List<Student> students = response.body();
                Log.d(TAG, "------STUDENTS-------");
                String result = "";
                for (Student student : students)
                {
                    Log.d(TAG, student.toString());
                    result += student.toString() + '\n';

                }

                editText_outpput_body.setText(result);
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t)
            {
                Log.e(TAG, "Failed to get response using \"Call<List<Student>> getStudentsCall = api.getAll(url);\" url=" + url);
                Log.e(TAG, "Attempting to use \"Call<Student> getStudentCall = api.get(url);\"");

                final Call<Student> getStudentCall = api.get(url);

                getStudentCall.enqueue(new Callback<Student>()
                {
                    @Override
                    public void onResponse(Call<Student> call, Response<Student> response)
                    {
                        Student student = response.body();
                        Log.d(TAG, "------STUDENT-------");

                        Log.d(TAG, student.toString());

                        editText_outpput_body.setText(student.toString());
                    }

                    @Override
                    public void onFailure(Call<Student> call, Throwable t)
                    {
                        Log.e(TAG, "Failed to get response using \"Call<Student> getStudentCall = api.get(url);\" url=" + url);
                    }
                });
            }
        });
    }

//    //assumes the response to be an array of json objects. If it fails then a json object request is made.
//    private void makeGETrequest(final String url)
//    {
//
//        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONArray>()
//                {
//                    @Override
//                    public void onResponse(JSONArray response)
//                    {
//                        String studentsAsString = "";
//                        String responseString = response.toString();
//                        Gson gson = new Gson();
//
//                        Log.d(TAG, "onResponse in makeGetRequest");
//                        Log.d(TAG, "RESPONSE STRING" + responseString);
//
//                        for (int i = 0; i < response.length(); i++)
//                        {
//                            Student student = null;
//                            try
//                            {
//                                student = gson.fromJson(response.getJSONObject(i).toString(), Student.class);
//                            } catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                            }
//                            studentsAsString += student.toString() + "\n\n";
//
//                        }
//
//                        editText_outpput_body.setText(studentsAsString);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        Log.d(TAG, "JsonArrayRequest Failed : " + error.getMessage());
//                        error.printStackTrace();
//
//                        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                                new Response.Listener<JSONObject>()
//                                {
//                                    @Override
//                                    public void onResponse(JSONObject response)
//                                    {
//                                        Gson gson = new Gson();
//                                        String responseString = response.toString();
//                                        Student student = gson.fromJson(responseString, Student.class);
//                                        editText_outpput_body.setText(student.toString());
//                                    }
//                                },
//                                new Response.ErrorListener()
//                                {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error)
//                                    {
//                                        Log.d(TAG, "JsonObjectRequest Failed : " + error.getMessage());
//                                        error.printStackTrace();
//                                    }
//                                }
//                        );
//
//                        requestQueue.add(jsonObjectRequest);
//                    }
//                }
//        );
//
//
//        requestQueue.add(jsonArrayRequest);
//    }

    public void makePostrequest(String url)
    {
        String studentAsJson = editText_input_body.getText().toString();
        final Student student = new Gson().fromJson(studentAsJson, Student.class);
        Call<Void> addNewStudent = api.add(url, student);

        addNewStudent.enqueue(new Callback()
        {
            @Override
            public void onResponse(Call call, Response response)
            {
                Log.d(TAG, "successfull in adding new Student" + student.toString() + "  using Call addNewStudent = api.add(url,student);");
            }

            @Override
            public void onFailure(Call call, Throwable t)
            {
                Log.e(TAG, "failed to add new Student" + student.toString() + "  using Call addNewStudent = api.add(url,student);");
            }
        });
    }

    public void makePutRequest(final String url)
    {
        String studentAsJson = editText_input_body.getText().toString();
        final Student student = new Gson().fromJson(studentAsJson, Student.class);
        Call<Void> updateStudent = api.update(url, student);

        updateStudent.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                Log.d(TAG, "successfull in updating Student id : " + url.substring(url.lastIndexOf('\\')+1) + " with : " + student.toString() + "  using Call<Void> updateStudent = api.update(url, student);");

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.e(TAG, "failed to update Student id : " + url.substring(url.lastIndexOf('\\')+1) + " with : " + student.toString() + "  using Call<Void> updateStudent = api.update(url, student);");

            }
        });
    }

    public void makeDeleteRequest(final String url)
    {
        Call<Void> removeStudent = api.remove(url);
        removeStudent.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                Log.d(TAG, "successfull in deleting Student id : " + url.substring(url.lastIndexOf('\\')+1) + "  using Call<Void> removeStudent = api.remove(url);");

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.d(TAG, "failed to delete Student id : " + url.substring(url.lastIndexOf('\\')+1) + "  using Call<Void> removeStudent = api.remove(url);");
            }
        });
    }

    public void initializeSpinner()
    {
        spinner = findViewById(R.id.spinner_httpMethods);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.http_methods, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                httpMethodToCall = httpMethods[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
}
