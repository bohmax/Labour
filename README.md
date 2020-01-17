# Labour
Labour è un applicazione android per il progetto del corso di Sviluppo Applicazioni Mobili. 
E' pensato per simulare un esperienza lavorativa di un magazziniere Amazon. 
Si inizia timbrando il cartellino (NFC) o una tastiera esterna. Una volta loggato bisogna selezionare il pacco su cui si sta lavorando. 
Una volta selezionato si va nella sezione del lavoro, e si deve raggiungere la destinazione seguendo le istruzioni a schermo.
Arrivati a destinazione bisogna scansionare il qr code del pacco. E' possibile vedere lo storico dei pacchi consegnati nella sezione profilo.

## Come funziona
Per il login del telefono ho utilizzato il sensore nfc del telefono. Creo un db attraverso Sqlitehelper creando una tabella utenti e pacchi (pacchi ha una chiave esterna della key di utenti). Ogni utente può modificare i propri dati e impostare una immagine profilo(scattando una foto o selezionandola dalla galleria), nel caso della galleria utilizzo un content provider per accedere a essa e salvarla nella memoria interna del telefono. Per quanto riguarda passi e bussola utilizzo una combinazione di magnetometro e accelerometro e conta passi andandone a richiedere i sensori da android. La lettura del qr code avviene attraverso il lettore sviluppato da zxing. Una volta letto il qr code(che deve essere un link ad un immagine) scarica la foto del link attraverso DownloadManager, il risultato verrà poi letto attraverso chiamate base del sistema operativo, perchè per motivi universitari ho cercato di tenere tutto il più basso livello possibile.

## Tecniche utilizzate
Utilizzo due recycledView la prima nel fragment Pacchi la seconda in quella profilo. Gli elementi di pacchi vengono caricati statisticamente, mentre nel nel profilo vengono prima presi tutti i dati associati all'utente. Nella onBindView si controlla se è presente l'immagine associata al pacco, se non è presente viene scaricata altrimenti si accede alla memoria esterna per prendere il file. In entrambi i casi vengono richiesti i permessi. La foto profilo viene caricata in modo analogo ma è salvata nella memoria interna. Ovviamente quando si accede allo storage viene usato un AsyncTask. Per l'esecuzione corretta sono necessari i permessi di memorizzazione e di camera. Sono inoltre utilizzati i sensori di nfc, camera, accelerometro, contapassi e magnetometro. Per scorrere i db dopo la query si usa un CursorAdapter. Il percorso che un dipendente deve eseguire viene generato con criteri specifici. Attualmente a casa dell'imprecisione del sensore dei passi ogni pacco inizialmente viene completato con un solo passo, ad ogni passo tutte le direzioni dei pacchi viene aggiornata.

## Screenshots
<img src="https://i.ibb.co/BP2nstb/Screenshot-1578619207.png" width="220"> <img src="https://i.ibb.co/N67D8wB/Screenshot-1578619215.png" width="220"> <img src="https://i.ibb.co/6wMk04p/Screenshot-1578619231.png" width="220"> <img src="https://i.ibb.co/pQ8JjDv/Condividi.png" width="220">
