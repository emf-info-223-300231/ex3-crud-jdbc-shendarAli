/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.workers;

import app.beans.Personne;
import java.util.List;

/**
 *
 * @author alis
 */
public class PersonneManager {

    private int index = 0;
    private List<Personne> listePersonne;

    public PersonneManager() {
    }

    public Personne courantPersonne() {
        Personne pers = listePersonne.get(index);
        return pers;
    }

    public Personne debutPersonne() {
        Personne pers = listePersonne.get(0);
        return pers;

    }

    public Personne finPersonne() {
        int g= listePersonne.size();
        Personne pers= listePersonne.get(g-1);
        
        return pers;

    }

    public Personne precedentPersonne(List<Personne> listPersonne) {
        Personne pers;
        if (index == 0) {

            pers = listePersonne.get(index);
        } else {
            index--;
            pers = listePersonne.get(index);
        }
        return pers;
    }

    public Personne setPersonne(List<Personne> listePers) {
        listePersonne = listePers;
        return listePers.get(0);
    }

    public Personne suivantPersonne() {
        Personne pers;
        if (index == listePersonne.size()) {
            pers = listePersonne.get(index);
        } else {
            index++;
            pers = listePersonne.get(index);

        }

        return pers;

    }

}
