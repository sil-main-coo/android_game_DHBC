package vn.edu.topica.game_dhbc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import vn.edu.topica.model.Question;

public class MainActivity extends AppCompatActivity {
    private String TAG= "MainActivity";

    private TextView txt_coin;    // txt điểm
    private TextView txt_heart;  // txt mạng
    private ImageView imgAvt;   // avt


    private int coin = 1000;   // biến lưu điểm
    private int heart = 5;  // biến lưu mạng
    private int i = 0;     //
    private int count=0;
    private int temp=-1;
    private int posHelp=0;
    private HashMap<Integer, Integer> hmCode;


    private LinearLayout layout_ShowAlphabet_Top;
    private LinearLayout layout_ShowAlphabet_Bottom;
    private LinearLayout layout_AnswerAlphabet_Top;
    private LinearLayout layout_AnswerAlphabet_Bottom;
    private ImageView imgPicture;


    private Random random=new Random();
    private StringBuilder ketQua;   // Chuỗi kết quả
    private Button[] btnKq;    // Mảng button trả lời
    private Button[] btnGoiY;
    private Question questionRD;   // Biến lưu question đã random

    HashMap<Integer, Question> questions;    // Chứa tổng số câu hỏi từ sv
    HashMap<Integer, Question> hmQuestionRD;   // Chứa những câu hỏi đã random
    Integer[] arrIdQuestion;   // Chứa id kiểu dữ liệu int để random. nextInt();
    Set<Integer> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (questions==null)
            loadQuestions();

        addControls();
        addEvents();
        createGame();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Tôi đang trong resume");
        if (questionRD.getDapAnTam().equalsIgnoreCase(String.valueOf(ketQua))) {
            createGame();
        }
    }

    // Cập nhật câu hỏi (bổ sung tính năng khi có internet...)
    private void loadQuestions() {
        QuestionTask task= new QuestionTask();
        task.execute();
        try {
             questions= task.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (questions==null)
            makeToast(this,"Chưa có dữ liệu game");
        else {
            keys = questions.keySet();  // Lấy toàn bộ id của câu hỏi

            // Thêm id của question vào arr id để random
            arrIdQuestion = keys.toArray(new Integer[keys.size()]);
        }
    }

    private void addControls() {
        txt_coin = findViewById(R.id.txt_coin);
        txt_heart =  findViewById(R.id.txt_avatar);
        txt_heart.setText(heart+"");
        txt_coin.setText(""+coin);
        hmQuestionRD=new HashMap<>();
        hmCode = new HashMap<>();
        ketQua = new StringBuilder();

        layout_AnswerAlphabet_Top = findViewById(R.id.layout_AnswerAlphabet_Top);
        layout_AnswerAlphabet_Bottom= findViewById(R.id.layout_AnswerAlphabet_Bottom);
        imgPicture =  findViewById(R.id.imgPicture);
        layout_ShowAlphabet_Top= findViewById(R.id.layout_ShowAlphabet_Top);
        layout_ShowAlphabet_Bottom= findViewById(R.id.layout_ShowAlphabet_Bottom);

    }



    private void addEvents() {
        txt_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xyLyKhiClickTroGiup();
            }
        });
    }


    private void createGame() {
        layout_ShowAlphabet_Top.removeAllViews();
        layout_ShowAlphabet_Bottom.removeAllViews();
        layout_AnswerAlphabet_Top.removeAllViews();
        layout_AnswerAlphabet_Bottom.removeAllViews();

        questionRD = randomID();

        if(questionRD==null)
            makeToast(MainActivity.this,"Hết câu hỏi");

        else {
            Log.e("KQ RANDOM: ", questionRD.getLink() + questionRD.getDapAn());
            creatImage();
            creatButtonPick();
            creatButton();

            hmCode.clear();
            ketQua = new StringBuilder();
            count = 0;
            i = 0;
            posHelp=0;
        }
    }

    //Hàm random lấy hình ảnh/câu hỏi khong bị trùng
    public Question randomID(){
        Question rdQuestion=null;
        while (!check(hmQuestionRD, rdQuestion)) {
            if (hmQuestionRD.size()==arrIdQuestion.length)
                break;
            rdQuestion=questions.get(arrIdQuestion[random.nextInt(arrIdQuestion.length)]);
        }
        return rdQuestion;
    }

    // Hàm check loại bỏ những phần tử bị trùng khi random
    boolean check(HashMap<Integer,Question> hmQuestionRD,Question rdQuestion){
        if(rdQuestion== null)
            return false;
        else if (hmQuestionRD.containsKey(rdQuestion.getId()))
            return false;

        hmQuestionRD.put(rdQuestion.getId(), rdQuestion);
        return true;
    }


    // Hàm Random trộn chữ cái đáp án
    public ArrayList randomQuestions(){
        ArrayList<String> arrS =new ArrayList<>();
        int tm=random.nextInt(25)+65;

        for (int i=0;i<questionRD.getDapAnTam().length();i++){
            arrS.add(questionRD.getDapAnTam().charAt(i)+"");
        }
        for (int i=0;i<14-questionRD.getDapAnTam().length();i++){
            arrS.add((char)tm+"");
        }
        return arrS;
    }



    // Hiển thị hình ảnh (Câu hỏi)
    public void creatImage() {
        imgPicture.setImageBitmap(questionRD.getBitmap());
}

    // Tạo ô điền chữ cái trả lời
    public void creatButton() {
        btnKq = new Button[questionRD.getDapAnTam().length()];

        if (questionRD.getDapAnTam().length() <=6) {
            layout_AnswerAlphabet(layout_AnswerAlphabet_Top, 0,questionRD.getDapAnTam().length());
        }
        else{
            layout_AnswerAlphabet(layout_AnswerAlphabet_Top,0, 6);
            layout_AnswerAlphabet(layout_AnswerAlphabet_Bottom, 6,questionRD.getDapAnTam().length());

        }
    }

    private void layout_AnswerAlphabet(LinearLayout layout_AnswerAlphabet,int start, int length) {
        for (int i = start ; i < length; i++) {
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            btn.setId(i);
            btn.setBackgroundResource(R.drawable.button_xam);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xyLyKhiClickXoaOTraLoi(v);
                }});
            layout_AnswerAlphabet.addView(btn);
            btnKq[i] = findViewById(btn.getId());
        }
    }

    private void xyLyKhiClickXoaOTraLoi(View v) {
        Button btn = (Button) v;

        // Nếu hết lượt chơi
        if (heart==0){
            Toast.makeText(this,"Bạn đã thua",Toast.LENGTH_SHORT).show();
        }

        else if (!btn.getText().toString().isEmpty()
                && hmCode.containsKey(btn.getId())
                && heart>0) {
            if (count == questionRD.getDapAnTam().length()) {
                for (int i = 0; i < btnKq.length; i++)
                    btnKq[i].setBackgroundResource(R.drawable.button_xam);
            }

            temp = btn.getId();   // Lưu địa chỉ của ô mới xóa trong ô chữ cái trả lời
            count--;    // Giảm đếm
            btn.setText("");
            btnGoiY[hmCode.get(temp)].setVisibility(View.VISIBLE);
        }

    }

    // Hàm check loại bỏ những chữ cái bị trùng khi random
    boolean checkCharArt(ArrayList<Integer> arrSo,int n){
        for (int i=0;i<arrSo.size();i++){
            if (n==arrSo.get(i)){
                return false;
            }
        }
        return true;
    }


    // Tạo ô chữ cái ngẫu nhiên
    public void creatButtonPick(){

        btnGoiY= new Button[14];

        ArrayList<Integer> arrSo=new ArrayList<>();
        for (int i=0;i<7;i++){
            Button btn= new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(95,100));
            btn.setId(i);
            btn.setBackgroundResource(R.drawable.tile_hover);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xyLyKhiClickChuCaiGoiY(v);
                }
            });
            while (btn.getText()=="") {
                int tmp=random.nextInt(14);
                if (checkCharArt(arrSo, tmp)) {
                    btn.setText((CharSequence) randomQuestions().get(tmp));
                    randomQuestions().remove(tmp);
                    arrSo.add(tmp);
                }
            }
            layout_ShowAlphabet_Top.addView(btn);
            btnGoiY[i]= findViewById(btn.getId());
        }
        for (int i=7;i<14;i++){
            Button btn= new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(95,100));
            btn.setBackgroundResource(R.drawable.tile_hover);
            btn.setId(i);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xyLyKhiClickChuCaiGoiY(v);
                }
            });
            while (btn.getText()=="") {
                int tmp=random.nextInt(14);
                if (checkCharArt(arrSo, tmp)) {
                    btn.setText((CharSequence) randomQuestions().get(tmp));
                    randomQuestions().remove(tmp);
                    arrSo.add(tmp);
                }
            }
            layout_ShowAlphabet_Bottom.addView(btn);
            btnGoiY[i]= findViewById(btn.getId());
        }
    }

    public void xyLyKhiClickChuCaiGoiY(View v) {
        Button button = (Button) v;



        // Nếu số chữ cái trong ô < đáp án
        if ( count < questionRD.getDapAnTam().length()) {
            if (temp != -1) {
                for (int j = 0; j <= temp; j++)
                    if (btnKq[j].getText().toString().equals("")) {
                        i = j;
                        break;
                    }
                temp = -1;
            }

            for (int k=0; k<btnKq.length; k++) {
                if (!btnKq[i].getText().toString().isEmpty())
                    i++;
            }

            btnKq[i].setText(button.getText());
            hmCode.put(btnKq[i].getId(), button.getId()); // Hm lưu địa chỉ của ô trả lời - ô gợi ý đã ấn . ( tương ứng )
            i++;
            count++;   // đếm chữ cái đã có trong ô

            if (button.getVisibility()==View.VISIBLE)
                button.setVisibility(View.INVISIBLE);  // Ẩn ô chữ cái gợi ý

        }

        // Nếu số chữ cái == đáp án
        if (count == questionRD.getDapAnTam().length()) {
            for (int k=0; k<btnKq.length; k++)
                ketQua.append(btnKq[k].getText().toString());

            // Nếu trả lời đúng
            if (questionRD.getDapAnTam().equalsIgnoreCase(String.valueOf(ketQua))) {
                for (int i = 0; i < btnKq.length; i++)
                    btnKq[i].setBackgroundResource(R.drawable.tile_true);
                //TableLayout tableLayoutMain= findViewById(R.id.tabLayoutMain);
                Toast.makeText(MainActivity.this,"Bạn đã trả lời đúng !!!",Toast.LENGTH_LONG).show();
                Intent intent= new Intent(MainActivity.this, DapAnActivity.class);
                startActivity(intent);

                coin += 100;
                txt_coin.setText(coin + "");

                //createGame();

            }

            // Nếu trả lời sai
            else {

                for (int i = 0; i < questionRD.getDapAnTam().length(); i++)
                    btnKq[i].setBackgroundResource(R.drawable.tile_false);

                if (heart==0)
                    Toast.makeText(this,"Bạn đã thua",Toast.LENGTH_SHORT).show();
                else {
                    heart --;
                    txt_heart.setText(heart + "");
                    Toast.makeText(MainActivity.this,"Bạn đã trả lời sai !!!",Toast.LENGTH_LONG).show();
                }

                ketQua.delete(0,ketQua.length());
            }
        }

    }

    private void xyLyKhiClickTroGiup() {
        if (!questionRD.getDapAnTam().equalsIgnoreCase(String.valueOf(ketQua)) && coin>=100) {
            if (count==questionRD.getDapAnTam().length()){
                for (int i = 0; i < btnKq.length; i++)
                    btnKq[i].setBackgroundResource(R.drawable.button_xam);
            }
            for (int j =btnKq.length-1 ; j >= posHelp; j--) {
                if (btnKq[j].getText().toString().isEmpty())
                    continue;
                else {
                    btnKq[j].setText("");
                    Log.e("KẾT QUẢ HMCODE", hmCode.toString());
                    if (hmCode.size() != 0) {
                        temp = btnKq[j].getId();
                        btnGoiY[hmCode.get(temp)].setVisibility(View.VISIBLE);

                    }
                }
            }


            btnKq[posHelp].setText("" + questionRD.getDapAnTam().charAt(posHelp));
            for (int j = 0; j < btnGoiY.length; j++) {
                if (btnGoiY[j].getVisibility() == View.VISIBLE
                        && btnGoiY[j].getText().charAt(0)== questionRD.getDapAnTam().charAt(posHelp))
                {
                    btnGoiY[j].setVisibility(View.INVISIBLE);
                    break;
                }
            }

            coin -= 100;
            txt_coin.setText("" + coin);
            count=posHelp+1;
            i=posHelp+1;
            if (posHelp<questionRD.getDapAnTam().length()-1)
                posHelp++;





            if (count == questionRD.getDapAnTam().length()) {
                for (int k = 0; k < btnKq.length; k++)
                    ketQua.append(btnKq[k].getText().toString());

                // Nếu trả lời đúng
                if (questionRD.getDapAnTam().equalsIgnoreCase(String.valueOf(ketQua))) {
                    for (int i = 0; i < btnKq.length; i++)
                        btnKq[i].setBackgroundResource(R.drawable.tile_true);

                    Toast.makeText(MainActivity.this, "Bạn đã trả lời đúng !!!", Toast.LENGTH_LONG).show();

                    coin += 100;
                    txt_coin.setText(coin + "");

                    Intent intent= new Intent(MainActivity.this, DapAnActivity.class);
                    startActivity(intent);

                    //createGame();

                }
            }

        } else Toast.makeText(this, "Chưa đủ tiền !", Toast.LENGTH_LONG).show();

    }


    public void makeToast(Context context, String s){
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
    }



}
