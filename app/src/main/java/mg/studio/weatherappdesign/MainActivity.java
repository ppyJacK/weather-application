package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
        Toast.makeText(this, "Update data succeed",Toast.LENGTH_LONG ).show();
    }

    public void setWeather(int id,String s){
        switch (s) {
            case "晴":((ImageView) findViewById(id)).setImageResource(R.drawable.sunny_small);break;
            case "小雨":((ImageView) findViewById(id)).setImageResource(R.drawable.rainy_small);break;
            case "多云":((ImageView) findViewById(id)).setImageResource(R.drawable.partly_sunny_small);break;
            case "阴":((ImageView) findViewById(id)).setImageResource(R.drawable.windy_small);break;
            default:break;
        }
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://apis.juhe.cn/simpleWeather/query?city=%E9%87%8D%E5%BA%86&key=680fde858ff5aafd4c4534948e10963f";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String info) {
            String[] date_list = new String[5];
            String[] temp_list = new String[5];
            String[] weather_list = new String[5];
            //解析JSON文件
            JSONObject info_obj = JSON.parseObject(info);
            JSONObject res_obj = JSON.parseObject(info_obj.getString("result"));
            //城市
            String city = res_obj.getString("city");
            //温度
            JSONObject real_obj = JSON.parseObject(res_obj.getString("realtime"));
            String temperature = real_obj.getString("temperature");

            //天气图标
            String real_weather = real_obj.getString("info");
            setWeather(R.id.img_weather_condition,real_weather);

            //后4天的天
            JSONArray future_arr = res_obj.getJSONArray("future");
            for(int i=0;i<future_arr.size();i++)
            {
                JSONObject each_day = future_arr.getJSONObject(i);
                date_list[i] = each_day.getString("date");
                temp_list[i] = each_day.getString("temperature");
                weather_list[i] = each_day.getString("weather");
            }

            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
            ((TextView) findViewById(R.id.tv_location)).setText(city);
            ((TextView) findViewById(R.id.tv_date)).setText(date_list[0]);
            ((TextView) findViewById(R.id.first_date)).setText(date_list[1]);
            ((TextView) findViewById(R.id.second_date)).setText(date_list[2]);
            ((TextView) findViewById(R.id.third_date)).setText(date_list[3]);
            ((TextView) findViewById(R.id.fourth_date)).setText(date_list[4]);
            ((TextView) findViewById(R.id.first_temp)).setText(temp_list[1]);
            ((TextView) findViewById(R.id.second_temp)).setText(temp_list[2]);
            ((TextView) findViewById(R.id.third_temp)).setText(temp_list[3]);
            ((TextView) findViewById(R.id.fourth_temp)).setText(temp_list[4]);
            setWeather(R.id.first_img,weather_list[1]);
            setWeather(R.id.second_img,weather_list[2]);
            setWeather(R.id.third_img,weather_list[3]);
            setWeather(R.id.fourth_img,weather_list[4]);
        }
    }
}
