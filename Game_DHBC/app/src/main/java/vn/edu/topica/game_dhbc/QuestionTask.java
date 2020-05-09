package vn.edu.topica.game_dhbc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.topica.model.Question;

public class QuestionTask extends AsyncTask<Void, Void, HashMap<Integer, Question>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Trước khi đổi câu hỏi
    }

    @Override
    protected void onPostExecute(HashMap<Integer, Question> questions) {
        super.onPostExecute(questions);


    }

    @Override
    protected HashMap<Integer, Question> doInBackground(Void... voids) {
        HashMap<Integer, Question> questions= new HashMap<>();

        try{
            URL url= new URL("http://192.168.1.104/duoihinhbatchu/question.php"); // link
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
            // Add thêm 2 dòng dưới
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "*/*");

            // Lấy dữ liệu mà sv trả về
            InputStream is = connection.getInputStream();
            InputStreamReader inputStreamReader= new InputStreamReader(is, "UTF-8");
            BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
            String line= bufferedReader.readLine();
            StringBuilder builder= new StringBuilder();
            while (line!=null){
                builder.append(line);
                line= bufferedReader.readLine();
            }
            String json= builder.toString();

            // Sử dụng JSON OJB để đọc dữ liệu
            JSONObject jsonObject= new JSONObject(json);
            JSONArray jsonArray= jsonObject.getJSONArray("questions");  // Trả về JSON ARRAY / Trong items có JS Arr
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject item= jsonArray.getJSONObject(i);
                Question question= new Question();

                if (item.has("id"))
                    question.setId(item.getInt("id"));
                if (item.has("link")) {
                    question.setLink(item.getString("link"));

                     // Kết nối url để bitmap hình ảnh
                    url= new URL(question.getLink());
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    // Add thêm 2 dòng dưới
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                    connection.setRequestProperty("Accept", "*/*");

                    Bitmap bitmap= BitmapFactory.decodeStream(connection.getInputStream());

                    question.setBitmap(bitmap);
                }
                if (item.has("dapAnTam"))
                    question.setDapAnTam(item.getString("dapAnTam").trim());
                if (item.has("dapAn"))
                    question.setDapAn((item.getString("dapAn")).trim());

                questions.put(question.getId(),question);
                Log.e("KETQUA", "Lấy dữ liệu thành công");
                Log.e("KQ: ",question.getLink().trim());
            }

        }catch (Exception ex){
            Log.e("LOI", ex.toString());
        }

        return questions;
    }
}