package com.example.esmaeel.projectapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.eThread.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    Button connectbtn;
    EditText ported,iped ,lap_port , lap_ip;
    String ip="";
    int port=1111;
    MySQLiteHelper db;

    String lapipcheck ;
    int lapportcheck;
    boolean LaptopConnectionIsTrue = false;
    CheckBox checkBox;
    LinearLayout ipll,portll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        db = new MySQLiteHelper(this); // insialize the database class

        iped=(EditText) findViewById(R.id.ipedtext1);
        ported=(EditText)findViewById(R.id.portedtext1);
        lap_port=(EditText) findViewById(R.id.laptopportedtext);
        lap_ip=(EditText)findViewById(R.id.laptopipedtext);
        checkBox = (CheckBox)findViewById(R.id.checkBox);

        portll = (LinearLayout)findViewById(R.id.lapportLL);
        ipll = (LinearLayout) findViewById(R.id.lapipLL);
        portll.setVisibility(View.GONE);
        ipll.setVisibility(View.GONE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    portll.setVisibility(View.VISIBLE);
                    ipll.setVisibility(View.VISIBLE);
                    LaptopConnectionIsTrue = true;
                }else {
                    portll.setVisibility(View.GONE);
                    ipll.setVisibility(View.GONE);
                }
            }
        });
        connectbtn=(Button)findViewById(R.id.connectbtn);
        connectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String portcheck = ported.getText().toString();

                if (LaptopConnectionIsTrue)
                {
                    lapipcheck = iped.getText().toString();
                    lapportcheck = Integer.parseInt(ported.getText().toString().trim());
                    ip = iped.getText().toString();
                    port = Integer.parseInt(ported.getText().toString());
                    db.addInfo(new ConnectionInfo(portcheck,ip));
                    Intent intent = new Intent(FullscreenActivity.this,MainActivity.class);
                    intent.putExtra("PORT",port);
                    intent.putExtra("IP",ip);
                    intent.putExtra("LAPPORT",lapportcheck);
                    intent.putExtra("LAPIP",lapipcheck);
                    intent.putExtra("LAPState",true);
                    startActivity(intent);
                }else {
                    ip = iped.getText().toString();
                    port = Integer.parseInt(ported.getText().toString());
                    db.addInfo(new ConnectionInfo(portcheck,ip));
                    Intent intent = new Intent(FullscreenActivity.this,MainActivity.class);
                    intent.putExtra("PORT",port);
                    intent.putExtra("IP",ip);
                    intent.putExtra("LAPPORT",1111);
                    intent.putExtra("LAPIP"," ");
                    intent.putExtra("LAPState",false);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}


