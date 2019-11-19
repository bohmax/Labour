package com.example.labour;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * Classe che si occupa di gestire l'acquisizione dell'id
 */
class GetLogin_ID {

    private static StringBuilder sb = new StringBuilder();

    /**
     *
     * @param intent l'intent nel quale il messaggio nfc è stato registrato
     * @return array di NFC Data Exchange Format se ci sono messaggi pendenti, null altrimenti
     */
    static NdefMessage[] getNdefMessages(Intent intent){
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }

    /**
     *
     * @param messag NFC Data Exchange Format deve contenere il pacchetto con header e payload
     * @return Stringa contenente il payload di messag
     */
    static String getNFCPayload(NdefMessage messag){
        resetSb();
        for (NdefRecord r: messag.getRecords()) {
            if (r.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
                    byte[] payloadBytes = r.getPayload();

                    //-----------------

                    //bisogna leggere l'header del pacchetto
                    //questa operazione si poteva anche fare bit a bit con payloadBytes[0] & 0x080: nota 0x080 equivale a 1000000 infatti si deve fare un & bit a bit fino al settimo e valutare questo
                    Charset charset = ((payloadBytes[0] & 0x080) == 0) ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16; //status byte: bit 7 indicates encoding (0 = UTF-8, 1 = UTF-16)
                    //0x03F i primi 5 bit che contengono la lunghezza infatti sono a 1
                    int languageLength = payloadBytes[0] & 0x03F - 1; //status byte: bits 5..0 indicate length of language code

                    //-----------------

                    sb.append(new String(payloadBytes, languageLength + 1, payloadBytes.length - 1 - languageLength,charset)).append(" \n");
                    Log.d("READING", new String(payloadBytes, StandardCharsets.UTF_8));
                }
            }
        }
        String salva = sb.deleteCharAt(sb.length()-1).toString();
        Log.d("TAG", sb.toString());
        resetSb();
        return salva;
    }

    /**
     * Si concatena character alla stringa internaquando si usa il lettore esterno
     * @param character carattere da concatenare se il suo valore è diverso da 0
     */
    static void concatToString(int character){
        if (character != 0 && sb.length()<10) { //non è un carattere non speciale
            sb.append((char) character);
            //progress.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ritorna il valore momentaneo dell'id costruito tramite il lettore
     * @return la stringa che rappresenta l'id
     */
    static String get_ID(){
        return sb.toString();
    }

    /**
     * si usa per resettare sb.
     */
    private static void resetSb(){
        sb.setLength(0);
    }
}
