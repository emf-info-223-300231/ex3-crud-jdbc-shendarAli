package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.DateTimeLib;
import app.helpers.SystemLib;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

    /**
     *
     * @return @throws MyDBException
     */
    @Override
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
                
                nom = rs.getString("Nom");
                prenom = rs.getString("Prenom");
                date = rs.getDate("Date_naissance");
                noRue = rs.getInt("No_rue");
                rue = rs.getString("Rue");
                npa = rs.getInt("NPA");
                locatlite = rs.getString("Ville");
                actif = rs.getBoolean("Actif");
                salaire = rs.getInt("Salaire");
                dateModif = rs.getDate("Date_modif");
                Personne pres = new Personne(rs.getInt("PK_PERS"),
                        nom, prenom, date, noRue, rue, npa, locatlite, actif, salaire, dateModif);
                listePersonnes.add(pres);
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return listePersonnes;
    }

    @Override
    public Personne precedentPersonne() throws MyDBException {
        if (listePersonnes == null) {
            lirePersonnes();
        }
        index--;
        if (index < 0) {
            index = 0;
        }
        Personne pres = listePersonnes.get(index);
        System.out.println("personne precedent" + index);
        return pres;
    }

    @Override
    public Personne suivantPersonne() throws MyDBException {
        if (listePersonnes == null) {
            lirePersonnes();
        }
        index++;
        if (index < 0 && listePersonnes.size() < index) {
            index = 0;
        }
        Personne pres = listePersonnes.get(index);

        System.out.println("personne precedent" + index);

        return pres;
    }

    public void creer(Personne personne) throws MyDBException {

        String prep = "INSERT INTO t_personne (PK_PERS, Nom, Prenom, Date_naissance, No_rue, Rue, NPA, Ville , Salaire, date_modif )"
                + "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?)";
        try ( PreparedStatement ps = dbConnexion.prepareStatement(prep)) {
            ps.setString(1, personne.getNom());
            ps.setString(2, personne.getPrenom());
            java.sql.Date sqlPackageDate
                    = new java.sql.Date(personne.getDateNaissance().getTime());
            ps.setDate(3, sqlPackageDate);
            ps.setInt(4, personne.getNoRue());
            ps.setString(5, personne.getRue());
            ps.setInt(6, personne.getNpa());
            ps.setString(7, personne.getLocalite());
            ps.setDouble(8, personne.getSalaire());
            java.sql.Date datesqlModif
                    = new java.sql.Date(personne.getDateModif().getTime());
            ps.setDate(9, datesqlModif);
            int test = ps.executeUpdate();
            if (test == 1) {
                lirePersonnes();
            } else {
                System.out.println("error de update");
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void effacer(Personne personne) throws MyDBException {
        String pres = "DELETE FROM `t_personne` WHERE PK_PERS=?";
        try ( PreparedStatement ps = dbConnexion.prepareStatement(pres)) {

            ps.setInt(1, personne.getPkPers());
             int tester = ps.executeUpdate();
             if(tester!=1){
                 System.out.println("error pour suppirmer");
             }
            lirePersonnes();
        } catch (SQLException ex) {

        }

    }

    @Override
    public Personne lire(int PK_PERS) throws MyDBException {
        Personne pres = null;
        try ( Statement st = dbConnexion.createStatement();  ResultSet rs = st.executeQuery("SELECT PK_PERS from t_personne")) {
            int pk = rs.getInt(PK_PERS);
            String nom = rs.getString("Nom");
            String prenom = rs.getString("Prenom");
            Date date = rs.getDate("Date_naissance");
            int noRue = rs.getInt("No_rue");
            String rue = rs.getString("Rue");
            int npa = rs.getInt("NPA");
            String locatlite = rs.getString("Ville");
            boolean actif = rs.getBoolean("Actif");
            int salaire = rs.getInt("Salaire");
            Date dateModif = rs.getDate("Date_modif");
            pres = new Personne(rs.getInt("PK_PERS"),
                    nom, prenom, date, noRue, rue, npa, locatlite, actif, salaire, dateModif);

        } catch (SQLException ex) {

        }
        return pres;

    }

    @Override
    public void modifier(Personne personne) throws MyDBException {
        String prep = "UPDATE t_personne SET PK_PERS=?, Prenom=?,Nom=?,Date_naissance=?,No_rue=?,Rue=?,NPA=?,Ville=?,Actif=?,Salaire=?,date_modif=?"
                + " WHERE PK_PERS=?";
        int test;
        try ( PreparedStatement rs = dbConnexion.prepareStatement(prep)) {
            rs.setInt(1, personne.getPkPers());
            rs.setString(2, personne.getPrenom());
            rs.setString(3, personne.getNom());
            Date date = personne.getDateNaissance();
            rs.setDate(4, new java.sql.Date(date.getTime()));
            rs.setInt(5, personne.getNoRue());
            rs.setString(6, personne.getRue());
            rs.setInt(7, personne.getNpa());
            rs.setString(8, personne.getLocalite());
            if (personne.isActif()) {
                test = 1;
            } else {
                test = 0;
            }
            rs.setInt(9, test);
            rs.setDouble(10, personne.getSalaire());
            rs.setDate(11, new java.sql.Date(personne.getDateModif().getTime()));
            rs.setInt(12, personne.getPkPers());
            int tester = rs.executeUpdate();
            if (tester != 1) {
                System.out.println("Erreur de mise à jours !!!!!!!!!!!!!!!!!!!");
            }
            lirePersonnes();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
