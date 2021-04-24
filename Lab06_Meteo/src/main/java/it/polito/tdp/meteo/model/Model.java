package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.time.*;
import java.util.Date;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private MeteoDAO dao;
	private List<Rilevamento> rilevamenti;
	private Map<String, List<Rilevamento>> mappa;
	private Map<Citta, Double> mappaU;
	private List<Citta> listaCitta;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private List<Citta> soluzioneM;
	private double costoTot;
	private double costoM;
	private int meseRicorsione;
	private List<Integer> cittaTrascorsa;
	private int[] cittaGiorni;

	public Model() {
		dao = new MeteoDAO();
		mappa = new HashMap<>();
		listaCitta = new ArrayList<>();
	}
	
	public void setAllRilevamenti() {
		rilevamenti = new ArrayList<>(dao.getAllRilevamenti());
		for(Rilevamento r : dao.getAllRilevamenti()) {
			if(mappa.keySet().contains(r.getLocalita())) {
				mappa.get(r.getLocalita()).add(r);
			}
			else {
				mappa.put(r.getLocalita(), new ArrayList());
			}
		}
		for(String s : mappa.keySet()) {
			Citta c = new Citta(s, mappa.get(s), mappa.get(s).size());
			listaCitta.add(c);
		}
	}
	
	// of course you can change the String output with what you think works best
	public Map<Citta, Double> getUmiditaMedia(int mese) {
		
		this.mappaU = new HashMap<>();
		for(Citta c : listaCitta) {
			double uTot = 0;
			int cont = 0;
			for(Rilevamento r : c.getRilevamenti()) {
				if(r.getData().getMonthValue() == mese){
					uTot += r.getUmidita();
					cont++;
				}
			}
			mappaU.put(c, (uTot/cont));
		}
		return mappaU;
	}
	
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		
		this.meseRicorsione=mese;
		costoTot = 0;
		costoM = 0;
		List<Citta> parziale = new ArrayList<Citta>();
		soluzioneM = new ArrayList<Citta>();
		cittaTrascorsa = new LinkedList<Integer>();
		cittaGiorni = new int[this.listaCitta.size()];
		
		cerca(parziale, 0);
		return soluzioneM;
	}
	
	private void cerca(List<Citta> parziale, int livello) {
		if(livello == 15) {
			if(cittaTrascorsa.size()==this.listaCitta.size()) {
				if(costoM == 0 || costoTot < costoM) {
					costoM = costoTot;
					soluzioneM = new ArrayList<Citta>(parziale);
				}
			}
			return;
		}
		
		for(Citta c : listaCitta) {
			int i = listaCitta.indexOf(c);
			if( cittaValidaMax(c, parziale) && cittaValidaConsecutivi(c, parziale) ) {
				if(!parziale.contains(c))
					cittaTrascorsa.add(i);
				parziale.add(c);
				cittaGiorni[i]++;
				costoTot += this.getUmiditaPerGiorno(c.getNome(), meseRicorsione, livello+1).getUmidita();
				if( siSposta(c, parziale) )
					costoTot += COST;
				
				cerca(parziale, livello+1);
				
				i = listaCitta.indexOf(c);
				if( siSposta(c, parziale) )
					costoTot -= COST;
				costoTot -= this.getUmiditaPerGiorno(c.getNome(), meseRicorsione, livello+1).getUmidita();
				parziale.remove(parziale.size()-1);
				if(!parziale.contains(c))
					cittaTrascorsa.remove(cittaTrascorsa.size()-1);
				cittaGiorni[i]--;
			}
		}
		
	}
	
	public Rilevamento getUmiditaPerGiorno(String localita, int mese, int giorno) {
		LocalDate d = LocalDate.of(2013, mese, giorno);
		for(Citta c : listaCitta) {
			if(c.getNome().equals(localita)) {
				for(Rilevamento r : c.getRilevamenti()) {
					if(r.getData().equals(d)) {
						return r;
					}
				}
			}
		}
		return null;
	}
	
	private boolean cittaValidaConsecutivi(Citta c, List<Citta> parziale ) {
		int i = this.listaCitta.indexOf(c);
		
		if(parziale.size()==0)
			return true;
		if((parziale.size()<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)) {
			if(!c.equals(parziale.get(parziale.size()-1)))
				return false;
			else
				return true;
		}
		if( !(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-3))) && !c.equals(parziale.get(parziale.size()-1)))
			return false;
		return true;
	}
	
	
	private boolean cittaValidaMax(Citta c, List<Citta> parziale ) {
		int i = this.listaCitta.indexOf(c);
		
		if(cittaGiorni[i]==NUMERO_GIORNI_CITTA_MAX)
			return false;
		else
			return true;
	}
	
	
	public boolean siSposta(Citta c, List<Citta> parziale) {
		if(parziale.size()==1)
			return false;
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)))
			return false;
		else
			return true;
	}

	public double getCostoM() {
		return costoM;
	}

	
}
