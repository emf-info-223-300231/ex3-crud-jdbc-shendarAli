package app.presentation;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.DateTimeLib;
import static app.helpers.DateTimeLib.dateToLocalDate;
import app.helpers.JfxPopup;
import app.workers.DbWorker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.io.File;
import app.workers.DbWorkerItf;
import app.workers.PersonneManager;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author PA/STT
 */
public class MainCtrl implements Initializable {
    // DBs à tester

    private enum TypesDB {
        MYSQL, HSQLDB, ACCESS
    };
    // DB par défaut
    final static private TypesDB DB_TYPE = TypesDB.MYSQL;
    private DbWorkerItf dbWrk;
    private PersonneManager persMan;
    private boolean modeAjout;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtPK;
    @FXML
    private TextField txtNo;
    @FXML
    private TextField txtRue;
    @FXML
    private TextField txtNPA;
    @FXML
    private TextField txtLocalite;
    @FXML
    private TextField txtSalaire;
    @FXML
    private CheckBox ckbActif;
    @FXML
    private Button btnDebut;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnEnd;
    @FXML
    private Button btnSauver;
    @FXML
    private Button btnAnnuler;
    @FXML
    private DatePicker dateNaissance;

    /*
   * METHODES NECESSAIRES A LA VUE
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbWrk = new DbWorker();
        persMan = new PersonneManager();
        ouvrirDB();
    }

    @FXML
    public void actionPrevious(ActionEvent event) {
        try {
            afficherPersonne(dbWrk.precedentPersonne());
        } catch (MyDBException ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
        }
    }

    @FXML
    public void actionNext(ActionEvent event) {
        try {
            afficherPersonne(dbWrk.suivantPersonne());
        } catch (MyDBException ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
        }
    }

    @FXML
    private void actionEnd(ActionEvent event) throws MyDBException {
        persMan.finPersonne();
        persMan.setPersonne(dbWrk.lirePersonnes());
        afficherPersonne(persMan.finPersonne());
    }

    @FXML
    private void debut(ActionEvent event) throws MyDBException {
        persMan.debutPersonne();
        persMan.setPersonne(dbWrk.lirePersonnes());
        afficherPersonne(persMan.debutPersonne());
    }

    @FXML
    private void menuAjouter(ActionEvent event) throws MyDBException {
        rendreVisibleBoutonsDepl(false);
        txtLocalite.clear();
        txtNPA.clear();
        txtNo.clear();
        txtNom.clear();
        txtPK.clear();
        txtPrenom.clear();
        txtRue.clear();
        txtSalaire.clear();
        dateNaissance.setValue(null);
        txtPK.setEditable(false);
        modeAjout = true;
    }

    @FXML
    private void menuModifier(ActionEvent event) throws MyDBException {
        rendreVisibleBoutonsDepl(false);
        txtPK.setEditable(false);
        modeAjout = false;

    }

    @FXML
    private void menuEffacer(ActionEvent event) throws MyDBException {
        rendreVisibleBoutonsDepl(false);
    }

    @FXML
    private void menuQuitter(ActionEvent event) throws MyDBException {
        dbWrk.deconnecter();
    }

    @FXML
    private void annulerPersonne(ActionEvent event) throws MyDBException {
        if (persMan.courantPersonne() != null) {
            dbWrk.effacer(persMan.courantPersonne());
            persMan.setPersonne(dbWrk.lirePersonnes());
        } else {
            System.out.println("personne est null chez menu effacer");
        }
    }

    @FXML
    private void sauverPersonne(ActionEvent event) throws MyDBException {
        int pk = Integer.valueOf(txtPK.getText());
        String nom = txtNom.getText().toString();
        String prenom = txtPrenom.getText().toString();
        int noRue = Integer.parseInt(txtNo.getText());
        String rue = txtRue.getText().toString();
        int npa = Integer.parseInt(txtNPA.getText());
        String local = txtLocalite.getText().toString();
        double salaire = Double.valueOf(txtSalaire.getText());
        boolean actif = ckbActif.isSelected();
        LocalDate dateNee = dateNaissance.getValue();
        Date today = new Date();
        LocalDate tojour = DateTimeLib.dateToLocalDate(today);
        Personne pers = new Personne(pk, nom, prenom, today, noRue, rue, npa, local, actif, salaire, today);
        if (modeAjout) {
            dbWrk.creer(pers);
        } else {
            Personne person = persMan.courantPersonne();
            pers.setNom(nom);
            pers.setPrenom(prenom);
            pers.setNoRue(noRue);
            pers.setRue(rue);
            pers.setNpa(npa);
            pers.setLocalite(local);
            pers.setSalaire(salaire);
            pers.setActif(actif);
            pers.setDateNaissance(DateTimeLib.localDateToDate(dateNee));
            pers.setDateModif(today);
            dbWrk.modifier(pers);
        }

    }

    public void quitter() {
        try {
            dbWrk.deconnecter(); // ne pas oublier !!!
        } catch (MyDBException ex) {
            System.out.println(ex.getMessage());
        }
        Platform.exit();
    }

    /*
   * METHODES PRIVEES 
     */
    private void afficherPersonne(Personne p) {
        if (p != null) {
            String pk = String.valueOf(p.getPkPers());
            txtPK.setText(pk);
            txtPrenom.setText(p.getPrenom());
            txtNom.setText(p.getNom());
            txtNo.setText(String.valueOf(p.getNoRue()));
            txtRue.setText(p.getRue());
            txtNPA.setText(String.valueOf(p.getNpa()));
            txtLocalite.setText(p.getLocalite());
            txtSalaire.setText(String.valueOf(p.getSalaire()));
            Date date = p.getDateNaissance();
            LocalDate dateFin = DateTimeLib.dateToLocalDate(date);
            dateNaissance.setValue(dateFin);

        }
    }

    private void ouvrirDB() {
        try {
            switch (DB_TYPE) {
                case MYSQL:
                    dbWrk.connecterBdMySQL("223_personne_1table");
                    break;
                case HSQLDB:
                    dbWrk.connecterBdHSQLDB("../data" + File.separator + "223_personne_1table");
                    break;
                case ACCESS:
                    dbWrk.connecterBdAccess("../data" + File.separator + "223_Personne_1table.accdb");
                    break;
                default:
                    System.out.println("Base de données pas définie");
            }
            System.out.println("------- DB OK ----------");
            List<Personne> setlist = dbWrk.lirePersonnes();
            Personne pres = persMan.setPersonne(setlist);
            afficherPersonne(pres);
        } catch (MyDBException ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
            System.exit(1);
        }
    }

    private void rendreVisibleBoutonsDepl(boolean b) {
        btnDebut.setVisible(b);
        btnPrevious.setVisible(b);
        btnNext.setVisible(b);
        btnEnd.setVisible(b);
        btnAnnuler.setVisible(!b);
        btnSauver.setVisible(!b);
    }

    private void effacerContenuChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtPK.setText("");
        txtNo.setText("");
        txtRue.setText("");
        txtNPA.setText("");
        txtLocalite.setText("");
        txtSalaire.setText("");
        ckbActif.setSelected(false);
    }

}
