package com.infobk.carpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SaveActivity extends AppCompatActivity {

   public ListView listView ;
    SQLiteDatabase database;
    public Adapter customAdapter;
    public ArrayList<Model> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView=findViewById(R.id.listview);

        Toast.makeText(getApplicationContext(),"Önce aracınızı kayıt edin!",Toast.LENGTH_LONG).show();


        getData();

    }
    public  void getData(){

        customAdapter=new Adapter(this,modelList);


        try {

            database = this.openOrCreateDatabase("Konumlar",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM konumlar",null);

            int nameIx = cursor.getColumnIndex("name");
            int latitudeIx =cursor.getColumnIndex("latitude");
            int longitudeIx=cursor.getColumnIndex("longitude");

            while (cursor.moveToNext()){

                String nameFromDatabase = cursor.getString(nameIx);
                String latitudeFromDatabase = cursor.getString(latitudeIx);
                String longitudeFromDatabase = cursor.getString(longitudeIx);

                Double latitude = Double.parseDouble(latitudeFromDatabase);
                Double longitude = Double.parseDouble(longitudeFromDatabase);

                Model model = new Model(nameFromDatabase,latitude,longitude);

                System.out.println(model.name);
                modelList.add(model);
            }
            customAdapter.notifyDataSetChanged();
            cursor.close();




        }catch (Exception e){
            e.printStackTrace();
        }
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SaveActivity.this,MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("model",modelList.get(position));
                startActivity(intent);
            }
        });


    }


}