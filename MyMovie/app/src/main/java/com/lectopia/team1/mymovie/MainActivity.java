package com.lectopia.team1.mymovie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    TextView tvDirector, tvGenre, tvActors, tvReleased, tvPlot;
    Button btnSearch;
    ImageView ivPoster;
    EditText edTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDirector = (TextView) findViewById(R.id.tvDirector);
        tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvActors = (TextView) findViewById(R.id.tvActors);
        tvReleased = (TextView) findViewById(R.id.tvReleased);
        tvPlot = (TextView) findViewById(R.id.tvPlot);

        btnSearch = (Button) findViewById(R.id.btnSearch);

        ivPoster = (ImageView) findViewById(R.id.ivPoster);

        edTitle = (EditText) findViewById(R.id.etTitle);

        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(edTitle.getText() != null && !edTitle.getText().toString().isEmpty()){
                    MySearchTask searchTask = new MySearchTask("http://www.omdbapi.com/");
                    searchTask.execute(edTitle.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "타이틀을 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class MySearchTask extends AsyncTask<String, Void, String>{
        private String str;

        public MySearchTask(String str){
            this.str = str;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... values) {
            String title = values[0];
            StringBuilder sb = null;

            try{
                Log.d("Movie Title", title);
                String params = "?apikey=88579aa5&t=" + title;
                /*byte[] postData = params.getBytes("UTF-8");*/
                sb = new StringBuilder();

                URL url = new URL(str + params);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(10000);
                con.setRequestMethod("GET");

                con.setDoInput(true);

                int responseCode = con.getResponseCode();
                Log.d("responseCode", "응답 결과 : " + responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStreamReader isr = new InputStreamReader(con.getInputStream(), "UTF-8");
                    BufferedReader br = new BufferedReader(isr);

                    String line = null;

                    while((line = br.readLine()) != null){
                        sb.append(line);
                    }
                }

                con.disconnect();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            /*Log.d("json data", s);*/
            super.onPostExecute(s);
            try{
                JSONObject obj = new JSONObject(s);

                String director = obj.getString("Director");
                String genre = obj.getString("Genre");
                String actors = obj.getString("Actors");
                String released = obj.getString("Released");
                String plot = obj.getString("Plot");
                String poster = obj.getString("Poster");

                tvDirector.setText(director);
                tvGenre.setText(genre);
                tvActors.setText(actors);
                tvReleased.setText(released);
                tvPlot.setText(plot);

                MyImageTask image = new MyImageTask(poster);
                image.execute();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    class MyImageTask extends AsyncTask<Void, Void, Void>{
        private String str;
        private Bitmap bm;

        public MyImageTask(String str){
            this.str = str;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL(str);
                InputStream is = url.openStream();
                bm = BitmapFactory.decodeStream(is);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ivPoster.setImageBitmap(bm);
        }
    }
}
