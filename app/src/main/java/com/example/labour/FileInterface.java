package com.example.labour;

import android.graphics.Bitmap;

public interface FileInterface {

    /**
     * chiamata quando l'asynctask finisce e l'async è stato creato se l'utente ha selezionato la galleria,
     * restituisce il path del nuovo file creato dall'async
     * @param path Indica il path del nuovo file, null se l'operazione non è andata a buon fine
     */
    void getNewFilePath(String path);

}
