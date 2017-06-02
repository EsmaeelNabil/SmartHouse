package com.example.esmaeel.projectapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class listActivity extends AppCompatActivity {


    Button btnRead,btnClear;
    EditText etPort, etIP;
    TextView datatv;
    MySQLiteHelper MySQLiteHelper;
    SQLiteDatabase database;
    ListView listView;
    ArrayAdapter<ConnectionInfo> Adapter;
    MySQLiteHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new MySQLiteHelper(this);

//        db.addInfo(new ConnectionInfo("8080", "192.168.1.3"));


        List<ConnectionInfo> list = null;
        try {
            list = db.getAllInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnRead=(Button)findViewById(R.id.read);
        btnClear=(Button)findViewById(R.id.delete);
        listView=(ListView)findViewById(R.id.list);


        Adapter =new ArrayAdapter<ConnectionInfo>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(Adapter);

        /*
         // add Books
        db.addInfo(new ConnectionInfo("Android Application Development Cookbook", "Wei Meng Lee"));
        db.addInfo(new ConnectionInfo("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
        db.addInfo(new ConnectionInfo("Learn Android App Development", "Wallace Jackson"));

        // get all books
        List<ConnectionInfo> list = db.getAllInfo();

        // delete one book
        db.deleteInfo(list.get(0));

        // get all books
        db.getAllInfo();
         */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.addInfo(new ConnectionInfo("Android Application Development Cookbook", "Wei Meng Lee"));
                db.addInfo(new ConnectionInfo("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
                db.addInfo(new ConnectionInfo("Learn Android App Development", "Wallace Jackson"));

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ConnectionInfo> list = db.getAllInfo();

            }
        });
    }







}









