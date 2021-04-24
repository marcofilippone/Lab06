package it.polito.tdp.meteo.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model m = new Model();
		
		m.setAllRilevamenti();
		
		System.out.println(m.getUmiditaMedia(12));
		
		System.out.println(m.getUmiditaPerGiorno("Genova", 5, 3));
	
		
		String str = "";
		for(Citta c : m.trovaSequenza(11)) {
			str += c.getNome()+"\n";
		}
		System.out.println(str);
		
		System.out.println(m.getCostoM());

	}

}
