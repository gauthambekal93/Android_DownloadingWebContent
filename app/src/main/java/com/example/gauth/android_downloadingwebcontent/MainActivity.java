package com.example.gauth.android_downloadingwebcontent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView downloadedImg;
    ArrayList<String> content = new ArrayList<String>(); //To store all animals
    List<Button> allButton=new ArrayList<Button>();
    String imageName="";

    public void checkAnswer(View view)
    {
      Button b=(Button)view;
       Log.i("Selected Option ", b.getText().toString());
        Log.i("Actual Answer",imageName);
        if(b.getText().toString()==imageName) //CHECK IF SELECTED OPTION IS CORRECT
        {
            Toast.makeText(MainActivity.this,"CORRECT!!",Toast.LENGTH_SHORT).show();
        }
else{
            Toast.makeText(MainActivity.this,"INCORRECT!!",Toast.LENGTH_SHORT).show();
        }
    }

    public void imageDownload(View view) {
        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;
        Random rand = new Random();
        int[] x = {rand.nextInt(37) + 2, rand.nextInt(37) + 2, rand.nextInt(37) + 2};

        Log.i("Interaction", "Button pressed");
        //STARTS HERE FOR IMAGE DISPLAY
        try {
            //https://www.wildcatsanctuary.org/jpegs/smaller%20pics/canadianlynx.jpg
            myImage = task.execute(content.get(x[0])).get();
            downloadedImg.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }//ENDS HERE FOR IMAGE DISPLAY

        //STARTS HERE CODE FOR RANDOM TEXT ON THE BUTTON
        Log.i("Random URL is!!", content.get(x[0]));

        Collections.shuffle(allButton);
        for (int i = 0; i <= 2; i++) {
            Pattern p1 = Pattern.compile("pics/(.*?).jpg");
            Matcher m1 = p1.matcher(content.get(x[i]));
            while (m1.find()) {
            String catName = m1.group(1).replaceAll("[\\d.]", "");
            catName.replaceAll("-", "");
            catName.replaceAll("%", "");
            catName.replaceAll("_", "");
            Log.i("Cat name is !!! ", catName);  //running correctly till here

                if(i==0){imageName=catName;  //used for later comparison with option
                    (allButton.get(i)).setText(catName);}
                if(i==1){(allButton.get(i)).setText(catName);}
                if (i==2){(allButton.get(i)).setText(catName);}
        }                 //while loop ends here

    }    //ENDS HERE CODE FOR RANDOM TEXT ON THE BUTTON
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;//This a special type of string which should be in the specific URL format and type URL
            HttpURLConnection urlConnection = null; //This is bit like a browser
            try {
                url = new URL(urls[0]); //The try catch is here because there can be an exception if url is not correct
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream(); //this is for holding the incomming data
                InputStreamReader reader = new InputStreamReader(in); //this is for reading data
                int data = reader.read(); //this keeps track of current data we are currently on
                while (data != -1)  //the dat will be read and once done it becomes -1
                {

                    char current = (char) data; //this will convert the value data is pointing to a charectar
                    result += current;
                    data = reader.read();
                }

                return result; //result back to task to be printed out
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadedImg = (ImageView) findViewById(R.id.imageView);
        DownloadTask task = new DownloadTask();

        allButton.add((Button) findViewById(R.id.button0));
        allButton.add((Button) findViewById(R.id.button1));
        allButton.add((Button) findViewById(R.id.button2));
        String result = null;
        try {
            result = task.execute("https://www.wildcatsanctuary.org/education/species/").get();
        } catch (InterruptedException e) {

            e.printStackTrace(); //prints all information of the error
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        Log.i("Contents of URL", result);
        Pattern p = Pattern.compile("\"og:image\" content=\"(.*?)\""); //get URL
        Matcher m = p.matcher(result);
        while (m.find()) {
            content.add(m.group(1)); //adds all the URLs to ArrayList
        }
    }
}
