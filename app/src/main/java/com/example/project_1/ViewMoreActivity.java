package com.example.project_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewMoreActivity extends AppCompatActivity {

    ArrayList<Data> datas= new ArrayList<Data>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);

        datas.add(new Data("김수정", "뿌링클 반반 하실분??",
                R.drawable.default_1));
        datas.add(new Data("홍길동", "지코바 나눠 먹어요",
                R.drawable.default_1));

        listView=(ListView)findViewById(R.id.lv_1);

        DataAdapter adapter = new DataAdapter(getLayoutInflater(), datas);

        listView.setAdapter(adapter);

    }
}