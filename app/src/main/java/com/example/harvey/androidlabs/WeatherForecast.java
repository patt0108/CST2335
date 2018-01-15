package com.example.harvey.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends Activity {
    protected static final String ACTIVITY_NAME = "WeatherForecast";
    Bitmap image = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new ForecastQuery().execute("");
    }

    protected class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String min;
        private String max;
        private String current;
        HttpURLConnection conn;
        final TextView txtCurrent = (TextView) findViewById(R.id.txtCurrentTemperature);
        final TextView txtMin = (TextView) findViewById(R.id.txtMinTemperature);
        final TextView txtMax = (TextView) findViewById(R.id.txtMaxTemperature);
        final ImageView imageView = (ImageView) findViewById(R.id.imgWeather);

        protected void setMinTemp(String min) {
            this.min = min;
        }

        protected String getMinTemp() {
            return min;
        }

        protected void setMaxTemp(String max) {
            this.max = max;
        }

        protected String getMaxTemp() {
            return max;
        }

        protected void setCurrentTemp(String current) {
            this.current = current;
        }

        protected String getCurrentTemp() {
            return current;
        }

        @Override
        protected String doInBackground(String... args) {
            String returnString = "Something went wrong.";

            try {
                final URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                parseXML(conn.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }

            return returnString;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            txtCurrent.setText("Current temperature: " + getCurrentTemp());
            txtMin.setText("Minimum temperature: " + getMinTemp());
            txtMax.setText("Maximum temperature: " + getMaxTemp());
            imageView.setImageBitmap(image);
        }

        private void parseXML(InputStream is) throws XmlPullParserException, IOException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "UTF-8");

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG)
                    continue;

                String name = parser.getName();

                if (name.equals("temperature")) {
                    setCurrentTemp(parser.getAttributeValue(null, "value"));
                    publishProgress(25);
                    setMinTemp(parser.getAttributeValue(null, "min"));
                    publishProgress(50);
                    setMaxTemp(parser.getAttributeValue(null, "max"));
                    publishProgress(75);
                }
                else if (name.equals("weather")) {
                    String iconName = parser.getAttributeValue(null, "icon") + ".png";
                    Log.i(ACTIVITY_NAME,"Looking for bitmap file " + iconName);

                    if (fileExistence(iconName)) {
                        FileInputStream fis = null;
                        try {
                            fis = openFileInput(iconName);
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        image = BitmapFactory.decodeStream(fis);
                        Log.i(ACTIVITY_NAME, "Found the image locally");
                    }
                    else {
                        image = HttpUtils.getImage("http://openweathermap.org/img/w/" + iconName);
                        FileOutputStream outputStream = openFileOutput(iconName, Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Log.i(ACTIVITY_NAME, "Needed to download the image");
                    }
                    publishProgress(100);
                }
            }
        }

        public boolean fileExistence(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }
    }
}