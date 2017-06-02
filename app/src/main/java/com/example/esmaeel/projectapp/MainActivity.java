package com.example.esmaeel.projectapp;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

                                                        // Variables declaration
    Thread eThread;
    private static final int UDP_SERVER_PORT = 1111;     // udp server port
    private static final int MAX_UDP_DATAGRAM_LEN = 800; // udp packet size to receive
    String ip = "";     // nodemcu kitt ip
    int port = 0;      // nodemcu kitt port
    String LAPIP = ""; // laptop ip
    int LAPPORT = 0; // laptop port
    boolean LAPState ; // for laptop controles view
    int sbprogresss = 0;
    String heat = "Warning TEMP";
    String[] items = new String[3];
                                             // Components declaration
    Switch room1LightSwitch, room2LightSwitch, room3LightSwitch, bathroomLightSwitch, kitchenSwitch, tvSwitch, musicSwitch;
    ImageView lamp1, lamp2, lamp3, lamp4, lamp5, lamp6, lamp7, next, mute, unmute, CL_config, standimgv, powerimgv, cdimgv;
    TextView temptxv, humiditytxv, timetxv, applysoundtx;
    Button dialogbtn, listactivitybutton;
    FloatingActionButton fab;
    SeekBar volControl;
    LinearLayout musicControl , LaptopView;
                                            // Classes declaration
    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;
    private RunServerInThread runServer = null;
    MediaPlayer mp;
    MySQLiteHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);db = new MySQLiteHelper(this);
        items[0] = "10";

        // here we Get the ip and port from the FullscreenActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            port      =extras.getInt("PORT");
            ip        = extras.getString("IP");

            LAPPORT   =extras.getInt("LAPPORT");
            LAPIP     = extras.getString("LAPIP");
            LAPState  = extras.getBoolean("LAPState");
        }

        initialize(); // in this step we call the method initialize to initialize the buttons and variables
        OnClickListener();

        if (LAPState){
            LaptopView.setVisibility(View.VISIBLE);
        }else {
            LaptopView.setVisibility(View.GONE);
        }

        // here we start to Listen to the NodeMcu data
        try {
            runServer = new RunServerInThread();
            runServer.start();

                udpClientThread = new UdpClientThread(
                        ip, port, "iamhere", udpClientHandler);
                udpClientThread.start();

            GetdataThread();

        }catch (Exception e)
        {
            e.printStackTrace();
            runServer.start();
        }
        try {
            udpClientThread = new UdpClientThread(
                    ip, port, "sendTempAndHumi", udpClientHandler);
            udpClientThread.start();
            timetxv.setText(CurrentTime());
        }catch (Exception e){e.printStackTrace();}
    }


    private void GetdataThread(){

        eThread = new Thread() {

            @Override
            public void run() {
                if (interrupted()) {
                    return;
                }
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(300000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                udpClientThread = new UdpClientThread(
                                        ip, port, "sendTempAndHumi", udpClientHandler);
                                udpClientThread.start();
                                timetxv.setText(CurrentTime());
//                                Toast.makeText(getApplicationContext(),"Requesting Temp and Humidity from the Sensors",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        eThread.start();
    }

    private String CurrentTime() {
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = hours + ":" + minutes + ":" + seconds;
        return curTime;
    }

    private void initialize() {
        listactivitybutton  = (Button) findViewById(R.id.infobtn);
        lamp1               = (ImageView) findViewById(R.id.lamp1);
        lamp2               = (ImageView) findViewById(R.id.lamp2);
        lamp3               = (ImageView) findViewById(R.id.lamp3);
        lamp4               = (ImageView) findViewById(R.id.lamp4);
        lamp5               = (ImageView) findViewById(R.id.lamp5);
        lamp6               = (ImageView) findViewById(R.id.lamp6);
        lamp7               = (ImageView) findViewById(R.id.lamp7);
        standimgv           = (ImageView) findViewById(R.id.standimgv);
        powerimgv           = (ImageView) findViewById(R.id.powerimgv);
        cdimgv              = (ImageView) findViewById(R.id.cdimgv);
        next                = (ImageView) findViewById(R.id.nextimgv);
        mute                = (ImageView) findViewById(R.id.muteimgv);
        CL_config           = (ImageView) findViewById(R.id.computerlaptopcontroimg);
        unmute              = (ImageView) findViewById(R.id.unmuteimgv);
        applysoundtx        = (TextView) findViewById(R.id.applysoundtx);
        volControl          = (SeekBar) findViewById(R.id.seekBar);
        musicControl        = (LinearLayout) findViewById(R.id.musiccontrol);
        LaptopView          = (LinearLayout) findViewById(R.id.LaptopView);
        temptxv             = (TextView) findViewById(R.id.temptexview);
        humiditytxv         = (TextView) findViewById(R.id.humiditytx);
        timetxv             = (TextView) findViewById(R.id.timetx);
        room1LightSwitch    = (Switch) findViewById(R.id.rm1switch);
        room2LightSwitch    = (Switch) findViewById(R.id.rm2switch);
        room3LightSwitch    = (Switch) findViewById(R.id.rm3switch);
        bathroomLightSwitch = (Switch) findViewById(R.id.bathswitch);
        kitchenSwitch       = (Switch) findViewById(R.id.ktswitch);
        tvSwitch            = (Switch) findViewById(R.id.tvswitch);
        musicSwitch         = (Switch) findViewById(R.id.musicswitch);
        dialogbtn           = (Button) findViewById(R.id.button);
        fab                 = (FloatingActionButton) findViewById(R.id.fab);
        udpClientHandler    = new UdpClientHandler(this);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.clock2);

    }                  // initialize the view commponents
    private void OnClickListener(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "IP:" + PrintIP() + "\n" + "Listening port:" + UDP_SERVER_PORT, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mp.reset();
            }
        });

        CL_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogstarterforlaptop();
            }
        });


        dialogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogstarter();
                finish();
            }
        });

        listactivitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, listActivity.class);
                startActivity(intent);
            }
        });

        applysoundtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, String.valueOf(sbprogresss), udpClientHandler);
                    udpClientThread.start();
                    temptxv.setText(sbprogresss);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                sbprogresss = progressValue;
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, String.valueOf(sbprogresss), udpClientHandler);
                    udpClientThread.start();
                    temptxv.setText(sbprogresss);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        room1LightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r1on", udpClientHandler);
                        udpClientThread.start();
                        lamp1.setImageResource(R.drawable.lion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r1off", udpClientHandler);
                        udpClientThread.start();
                        lamp1.setImageResource(R.drawable.liof);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        room2LightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r2on", udpClientHandler);
                        udpClientThread.start();
                        lamp2.setImageResource(R.drawable.lion);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r2off", udpClientHandler);
                        udpClientThread.start();
                        lamp2.setImageResource(R.drawable.liof);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        room3LightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r3on", udpClientHandler);
                        udpClientThread.start();
                        lamp3.setImageResource(R.drawable.lion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "r3off", udpClientHandler);
                        udpClientThread.start();
                        lamp3.setImageResource(R.drawable.liof);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bathroomLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "bon", udpClientHandler);
                        udpClientThread.start();
                        lamp4.setImageResource(R.drawable.lion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "boff", udpClientHandler);
                        udpClientThread.start();
                        lamp4.setImageResource(R.drawable.liof);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        kitchenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "kon", udpClientHandler);
                        udpClientThread.start();
                        lamp5.setImageResource(R.drawable.lion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "koff", udpClientHandler);
                        udpClientThread.start();
                        lamp5.setImageResource(R.drawable.liof);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "tvon", udpClientHandler);
                        udpClientThread.start();
                        lamp6.setImageResource(R.drawable.tvon);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                     try {
                        udpClientThread = new UdpClientThread(
                                ip, port, "tvoff", udpClientHandler);
                        udpClientThread.start();
                        lamp6.setImageResource(R.drawable.tvoff);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tvSwitch.setText("OFF");
                }
            }
        });
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        udpClientThread = new UdpClientThread(
                                LAPIP, LAPPORT, "musicon", udpClientHandler);
                        udpClientThread.start();
                        lamp7.setImageResource(R.drawable.musicstop);
//                        musicControl.setVisibility(View.VISIBLE);
                        next.setClickable(true);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicSwitch.setText("ON ");
                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                LAPIP, LAPPORT, "musicoff", udpClientHandler);
                        udpClientThread.start();

                        next.setClickable(false);

                        lamp7.setImageResource(R.drawable.musicstart);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicSwitch.setText("OFF");
                }
            }
        });

        lamp7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (musicSwitch.isChecked()) {
                    try {
                        udpClientThread = new UdpClientThread(
                                LAPIP, LAPPORT, "musicon", udpClientHandler);
                        udpClientThread.start();
                        lamp7.setImageResource(R.drawable.musicstop);
//                       musicControl.setVisibility(View.VISIBLE);
                        next.setClickable(true);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicSwitch.setText("ON ");
                } else {
                    try {
                        udpClientThread = new UdpClientThread(
                                LAPIP, LAPPORT, "musicoff", udpClientHandler);
                        udpClientThread.start();

                        next.setClickable(false);

                        lamp7.setImageResource(R.drawable.musicstart);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicSwitch.setText("OFF");
                }


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "next", udpClientHandler);
                    udpClientThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "mute", udpClientHandler);
                    udpClientThread.start();
                    mute.setImageResource(R.drawable.muteon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        unmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "unmute", udpClientHandler);
                    udpClientThread.start();
                    mute.setImageResource(R.drawable.mute);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        standimgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "standby", udpClientHandler);
                    udpClientThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        powerimgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "poweroff", udpClientHandler);
                    udpClientThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cdimgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    udpClientThread = new UdpClientThread(
                            LAPIP, LAPPORT, "cdon", udpClientHandler);
                    udpClientThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }              // Do the work for OnclickListener for the view components
    private String PrintIP() {  // Return mobile IP address
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }                   // This Method Return The Mobile phone IP in String Type
    private void dialogstarter() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.infodialog);
        dialog.setTitle("CONFIGURE IP&PORT");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final Button okdialog = (Button) dialog.findViewById(R.id.dialogOk);
        final EditText iped = (EditText) dialog.findViewById(R.id.ipedtext);
        final EditText ported = (EditText) dialog.findViewById(R.id.portedtext);
        okdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipcheck = iped.getText().toString();
                String portcheck = ported.getText().toString();
                int i = Integer.parseInt(String.valueOf(ipcheck.length()));
                if (ipcheck != "" || portcheck != "") {
                    if (i >= 11) {
                        if (i <= 15) {
                            ip = iped.getText().toString();
                            port = Integer.parseInt(ported.getText().toString());
                            //                             we start listinng here if we opend the dialog here
//                            runServer = new RunServerInThread();
//                            runServer.start();
                            db.addInfo(new ConnectionInfo(portcheck, ipcheck));
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Data saved for this session " + "\n" + " IP : " + ip + "\n Port : " + port, Toast.LENGTH_SHORT).show();
                        } else {Toast.makeText(getApplicationContext(), "Wrong IP : \n type it like this \n 192.168.1.1", Toast.LENGTH_SHORT).show();}
                    } else {Toast.makeText(getApplicationContext(), "Wrong IP : \n type it like this \n 192.168.1.1", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(getApplicationContext(), "You left it empty!!", Toast.LENGTH_SHORT).show();}
            }
        });dialog.show();
    }               // for Starting the dialoge and making the logic process on its info
    private void dialogstarterforlaptop() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.infodialog);
        dialog.setTitle("LAP/PC PORT-IP");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final Button okdialog = (Button) dialog.findViewById(R.id.dialogOk);
        final EditText iped = (EditText) dialog.findViewById(R.id.ipedtext);
        final EditText ported = (EditText) dialog.findViewById(R.id.portedtext);
        okdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipcheck = iped.getText().toString();
                String portcheck = ported.getText().toString();
                int i = Integer.parseInt(String.valueOf(ipcheck.length()));
                if (ipcheck != "" || portcheck != "") {
                    if (i >= 11) {
                        if (i <= 15) {
                            LAPIP = iped.getText().toString();
                            LAPPORT = Integer.parseInt(ported.getText().toString());
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Data saved for this session " +
                                    "\n" + " IP : " + ip +
                                    "\n Port : " + port, Toast.LENGTH_SHORT).show();
                        } else {Toast.makeText(getApplicationContext(), "Wrong IP : \n type it like this \n 192.168.1.1", Toast.LENGTH_SHORT).show();}
                    } else {Toast.makeText(getApplicationContext(), "Wrong IP : \n type it like this \n 192.168.1.1", Toast.LENGTH_SHORT).show();}
                } else {Toast.makeText(getApplicationContext(), "You left it empty!!", Toast.LENGTH_SHORT).show();}}
        });dialog.show();
    }    // for Starting the dialoge and making the logic process on its info
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }           // for the menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }   // OnclickListeners for menu options

    private void updateState(String state) {
    }   // this method Retrieve messages from the Private inner class

    private void updateRxMsg(String rxmsg) {
    }   // this method Retrieve messages from the Private inner class

    private void clientEnd() {
        udpClientThread = null;
    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;
        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_STATE:
                    parent.updateState((String) msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String) msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void addNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.heat);
        mBuilder.setContentTitle(heat);
        mBuilder.setContentText(heat + "check the place out for possible fire!");
        mBuilder.setPriority(2);
        mBuilder.setLights(Color.RED, 500, 500);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        mBuilder.setVibrate(pattern);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.clock2);
        mBuilder.setSound(sound);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
        mp.start();
    } // this Method we use it to Fire a Notification

    private Runnable updateUIwithRecievedData = new Runnable() {
        public void run() {
            if (runServer == null) return;
            try {
                String Workon = runServer.getLastMessage(); // here we get the last data the app recieved
                String Message = Workon.substring(0, 1);
                if (Message.equals("r") || Message.equals("b") || Message.equals("k") ||Message.equals("t")){
                    //-------------------------------------- acknowledge
                    if (Workon.equals("r1on")){room1LightSwitch.setChecked(true);}
                    if (Workon.equals("r1off")){room1LightSwitch.setChecked(false);}
                    if (Workon.equals("r2on")){room2LightSwitch.setChecked(true);}
                    if (Workon.equals("r2off")){room2LightSwitch.setChecked(false);}
                    if (Workon.equals("r3on")){room3LightSwitch.setChecked(true);}
                    if (Workon.equals("r3off")){room3LightSwitch.setChecked(false);}
                    if (Workon.equals("bon")){bathroomLightSwitch.setChecked(true);}
                    if (Workon.equals("boff")){bathroomLightSwitch.setChecked(false);}
                    if (Workon.equals("kon")){kitchenSwitch.setChecked(true);}
                    if (Workon.equals("koff")){kitchenSwitch.setChecked(false);}
                    if (Workon.equals("tvon")){tvSwitch.setChecked(true);}
                    if (Workon.equals("tvoff")){tvSwitch.setChecked(false);}
                }else {
                    items = Workon.split(",");
                    temptxv.setText(items[0]);
                    humiditytxv.setText(items[1]); //substring the data to show it
                    if (Integer.parseInt(items[0]) > 30) {heat.concat(items[0]);addNotification();} //we check if Temp is more than 30 degree we fire notification
                }

                //-------------------------------------------

            }catch (Exception e){e.printStackTrace();}
        }
    };

    private class RunServerInThread extends Thread {
        private boolean keepRunning = true;
        private String lastmessage = "";
        @Override
        public void run() {
            String message;
            byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(UDP_SERVER_PORT);

                while (keepRunning) {
                    socket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    lastmessage = message;
                    runOnUiThread(updateUIwithRecievedData);
                }

            } catch (Throwable e){e.printStackTrace();}

            if (socket != null) {socket.close();}

        }

        public String getLastMessage() {
            return lastmessage;
        } // This method get the message value and return it
    } // Private Inner Class used for Listenning to the data comming from the NodeMcu (KIT)

    @Override
    protected void onResume() {
        super.onResume();
        mp.pause();
        if (runServer == null){
            try {
                runServer = new RunServerInThread();
                runServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//     backbutton to finish the activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            eThread.interrupt();
        }
        return super.onKeyDown(keyCode, event);
    }
}