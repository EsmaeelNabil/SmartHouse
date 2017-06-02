package com.example.esmaeel.projectapp;

/**
 * Created by esmaeel on 1/19/2017.
 */

import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread{
    String dstAddress;
    String msgS;
    int dstPort;
    private boolean running;
    MainActivity.UdpClientHandler handler;
    DatagramSocket socket;
    InetAddress address;



    public UdpClientThread(String addr, int port,String mmsg, MainActivity.UdpClientHandler handler) {
        super();
        msgS =mmsg;
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }


    public void setRunning(boolean running){
        this.running = running;
    }
    @Override
    public void run() {
        running = true;
        try {socket = new DatagramSocket();
            address = InetAddress.getByName(dstAddress);
            byte[] buf = new byte[256];
            buf= msgS.getBytes();
            DatagramPacket packet =
                    new DatagramPacket(buf, buf.length, address, dstPort);
            socket.send(packet);
            // get response
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String line = new String(packet.getData(), 0, packet.getLength());
            handler.sendMessage(
                    Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                socket.close();
                handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
            }
        }
    }
}