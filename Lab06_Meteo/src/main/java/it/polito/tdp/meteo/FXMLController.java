/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	txtResult.clear();
    	Integer mese = boxMese.getValue();
    	if(mese==null) {
    		txtResult.setText("Scegliere un mese dal menu");
    		return;
    	}
    	List<Citta> lista = model.trovaSequenza(mese);
    	String str = "";
		for(Citta c : lista) {
			str += c.getNome()+"\n";
		}
    	txtResult.setText(str);
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	Integer mese = boxMese.getValue();
    	if(mese==null) {
    		txtResult.setText("Scegliere un mese dal menu");
    		return;
    	}
    	Map<Citta, Double> mappaU = model.getUmiditaMedia(mese);
    	String str = "";
    	for(Citta c : mappaU.keySet()) {
    		str += c.getNome() + ": " + mappaU.get(c) + " gradi\n";
    	}
    	txtResult.setText(str);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model=model;
    	List<Integer> mesi = new ArrayList<Integer>();
    	for(int i=1; i<=12; i++)
    		mesi.add(i);
    	boxMese.getItems().addAll(mesi);
    	model.setAllRilevamenti();
    }
}

