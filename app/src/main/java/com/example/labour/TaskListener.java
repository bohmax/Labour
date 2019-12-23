package com.example.labour;

public interface TaskListener {

    /**
     * Interfaccia per far comunicare l'AsyncTask e un activity
     * @param answer l'esito della richiesta
     * @param id che ha fatto partire la richiesta
     */
    void serverTask(boolean answer, String id);

}
