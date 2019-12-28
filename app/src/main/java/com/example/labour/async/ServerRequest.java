package com.example.labour.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.labour.interfacce.TaskListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerRequest extends AsyncTask<String, Void, Boolean> {

    private TaskListener activity;
    private String id;

    public ServerRequest(Activity activity) throws NullPointerException{
        if (activity==null) throw new NullPointerException();
        this.activity = (TaskListener) activity;
    }

    /**
     * Si occupa di connettersi al server e ricevere una risposta, per poi disconettersi immediatamente
     * @param nome va passato contenendo solo l'id della persona
     * @return true se la richiesta è andata a buon fine, falso altrimenti
     */
    @Override
    protected Boolean doInBackground(String... nome) {
        boolean answer;
        try {
            String ip = "192.168.1.125";
            Socket server = new Socket(ip, 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String hope = in.readLine();
            Log.i("Messaggio", hope);
            server.close();
            id = nome[0];
            answer = true;
        } catch (IOException e){
            answer = false;
        }
        return answer;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        activity.serverTask(result, id);
    }


}
