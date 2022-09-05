package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import java.util.List;

public interface DbWorkerItf {

  void connecterBdMySQL( String nomDB ) throws MyDBException;
  void connecterBdHSQLDB( String nomDB ) throws MyDBException;
  void connecterBdAccess( String nomDB ) throws MyDBException;
  void deconnecter() throws MyDBException; 
  void creer(Personne personne) throws MyDBException;
  void effacer(Personne personne) throws MyDBException;
  Personne lire(int PK_PERS) throws MyDBException;
  List<Personne> lirePersonnes() throws MyDBException;
  void modifier(Personne personne) throws MyDBException;
  Personne precedentPersonne() throws MyDBException;
  Personne suivantPersonne() throws MyDBException; 

}
