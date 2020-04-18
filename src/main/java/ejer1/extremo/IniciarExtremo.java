package ejer1.extremo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class IniciarExtremo {

	public static void main(String[] args) {
		NodoExtremo nExtremo;
		Gson gson = new Gson();
		HashMap<String, ArrayList<String>> config;
		try {
			config = gson.fromJson(new FileReader("dataConexion.json"), HashMap.class);
			ArrayList<String> listaServers = config.get("servers");
				if(listaServers.size()>0) {
					nExtremo = new NodoExtremo(listaServers);
					nExtremo.inicio(0);
				}
				else {
					System.out.println("Sin SV en el Archivo Config!");
				}
			
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		} 
		System.exit(0);
	}

}
