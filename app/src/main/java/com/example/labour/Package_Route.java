package com.example.labour;

import com.example.labour.utility.Orientation_utility;

import java.util.LinkedList;

public class Package_Route {

    /**
     * Se un utente sbaglia strada o sta seguendo un altro pacco devo essere pronto a riservagli
     * lo spazio per potergli permettere di scannerizzare il pacco senza farlo tornare al punto di partenza
     */
    private LinkedList<Route> routes;

    public Package_Route() {
        routes = new LinkedList<>();
        routes.add(new Route());
    }

    /**
     * aggiorna il percorso che l'utente deve seguire, questo metodo va chiamato per ogni passo preso
     * dall'utente, attenzione la direzione viene presa corretta con un certo intervallo di errore!
     * @param direzione la direzione che l'utente ha preso quando ha eseguito il passo in gradi
     * @return true se bisogna abilitare la scansione, false altrimenti
     */
    public boolean passo(float direzione){
        Route route = routes.getLast();
        switch (route.passo(direzione)){
            case -1:{ //spazio insufficiente
                routes.add(new Route(Orientation_utility.getDirection(direzione)));
                break;
            }
            case 1:{ //abilita scansione o elimina la testa
                if (routes.size() == 1)
                    return true;
                else routes.removeLast();
            }
        }
        return false;
    }

    public int getCurrenteSteps(){
        Route route = routes.getLast();
        return route.passi[route.last_insert];
    }

    public Direction getCurrenteDirection(){
        Route route = routes.getLast();
        return route.direction[route.last_insert];
    }

    private class Route {
        /**
         * Indica la strada per raggiumgere una direzione finale, che è rappresentato dal primo elemento
         * dell'array.
         */

        private final int MAX_LENGTH = 8; //Dimensione massima dell'array
        private int last_insert = -1;
        private Direction[] direction = new Direction[MAX_LENGTH];
        private int[] passi = new int[MAX_LENGTH];

        /**
         * genera casualmente direction e passi con un limite fissato sul numero di direzioni e passi
         */
        Route() {
            final int max_passi = 1; //massimo numero di passi iniziali, sommando tutti i passi dell'array passi
            final int max_direction = 1; //massimo numero di inserimenti iniziali in direction
            int numero_di_direzioni = ((int)(Math.random() * max_direction))+1;
            final int random_max = (max_passi/numero_di_direzioni); //intero superiore per generare i numeri di passi randomici
            for (int i = 0; i < numero_di_direzioni; i++){
                last_insert = i;
                Direction[] dir = Direction.values();
                direction[last_insert] = dir[((int)(Math.random() * dir.length))];
                int rand = ((int)(Math.random() * random_max));
                if (rand == 0) rand++;
                passi[last_insert] = rand;
            }
        }

        /**
         * usato quando si crea una nuova direzione
         * @param dir la nuova direzione
         */
        Route(Direction dir){
            last_insert = 0;
            direction[last_insert] = dir;
            passi[last_insert] = 1;
        }

        /**
         * decrementa nella posizione corrente di passi se segue la direzione, mentre li aumenta se fa la direzione opposta
         * @param direzione la direzione intrapresa dall'utente
         * @return 1 è possibile scansionare, -1 spazio finito e nuova direzione non registrata, 0 nessuna novità
         */
        int passo(float direzione) {
            Direction correct_direction = direction[last_insert];
            switch (Orientation_utility.closeDirection(correct_direction, direzione)){
                case 0:{ //nuova direzione
                    if (last_insert == (MAX_LENGTH - 1)){
                        return -1;
                    }
                    else {
                        last_insert++;
                        direction[last_insert] = Orientation_utility.getDirection(direzione);
                        passi[last_insert]++;
                    }
                    break;
                }
                case 1:{ //stessa direzione
                    if (last_insert != 0) {
                        passi[last_insert]--;
                        if (passi[last_insert] == 0) //ultimo passo in questa direzione compiuto
                            last_insert--;
                    }
                    else { //last insert == 0
                        if (passi[last_insert] == 0){ //cambia direzione e metti l'opposta
                            passi[last_insert]++;
                            direction[last_insert] = Orientation_utility.getDirection( (direzione + 360 - 180) % 360 );
                        }
                        else {
                            passi[last_insert]--;
                            if (passi[last_insert] == 0){
                                return 1;
                            }
                        }

                    }
                    break;
                }
                case 2:{ //direzioni opposte
                    passi[last_insert]++;
                    break;
                }
            }
            return 0;
        }
    }
}
