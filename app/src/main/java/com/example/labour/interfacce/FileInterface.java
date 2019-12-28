package com.example.labour.interfacce;

import java.io.File;

public interface FileInterface {

    /**
     * chiamata quando l'asynctask dedicato alla creazione dei tempfile finisce, restituisce il File creato dall'async
     * @param file Indica il path del nuovo file, null se l'operazione non è andata a buon fine
     */
    void getTempPath(File file);

    /**
     * chiamata da PhotoLoader per notificare se bisogna aggiornare la possibile futura nuova foto o meno
     * @param result true se ha avuto esito positivo, false altrimenti
     */
    void saveResult(Boolean result);

}
