package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;
    private int index = 0;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://LAPEMFB37-21.edu.net.fr.ch:3306/" + nomDB;
        final String user = "root";
        final String password = "emf1234";

        System.out.println("url:" + url_local);
        try {
            dbConnexion = DriverManager.getConnection(url_local, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    public List<Personne> lirePersonnes() throws MyDBException {
        listePersonnes = new ArrayList<>();
        String nom;
        String prenom;
        java.sql.Date date;
        int noRue;
        String rue;
        int npa;
        String locatlite;
        boolean actif;
        int salaire;
        java.sql.Date dateModif;
        try {
            Statement st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("SELECT * from t_personne");
            while (rs.next()) {
                nom=rs.getString("Nom");
                prenom= rs.getString("Prenom");
                date=rs.getDate("Date_naissance");
                noRue=rs.getInt("No_rue");
                rue= rs.getString("Rue");
                npa=rs.getInt("NPA");
                locatlite=rs.getString("Ville");
                actif=rs.getBoolean("Actif");
                salaire=rs.getInt("Salaire");
                dateModif= rs.getDate("Date_modif");
                Personne pres = new Personne(rs.getInt("PK_PERS"),
                        nom, prenom, date,noRue ,rue,npa, locatlite,actif ,salaire ,dateModif);
                listePersonnes.add(pres);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listePersonnes;
    }

    @Override
    public Personne precedentPersonne() throws MyDBException {

        int newIndex = 0;
        if (listePersonnes == null) {
            lirePersonnes();
        }
        if (index < 0) {
            index = 0;
        }
        Personne pres;
        if (index == 0) {
            pres = listePersonnes.get(index);
            newIndex = index - 1;
        } else {
            pres = listePersonnes.get(newIndex);
            newIndex = index - 1;
        }
        System.out.println("personne precedent" + index);
        index = newIndex;
        return pres;

    }

    @Override
    public Personne suivantPersonne() throws MyDBException {

        if (listePersonnes == null) {
            lirePersonnes();
        }

        lirePersonnes();
        int newIndex;
        Personne pres;
        if (index > 16) {
            index = 0;
            newIndex = index + 1;
            pres = listePersonnes.get(newIndex);

        } else {
            newIndex = index + 1;
            pres = listePersonnes.get(newIndex);

        }
        System.out.println("personne suivant" + index);
        index = newIndex;
        return pres;

    }

}
