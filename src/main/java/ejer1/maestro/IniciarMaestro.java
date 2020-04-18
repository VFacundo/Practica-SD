package ejer1.maestro;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ejer1.extremo.ServerExtremo;

public class IniciarMaestro {

	public static void main(String[] args){
		NodoMaestro nMaestro;
		Gson gson = new Gson();
		HashMap<String, ArrayList<String>> config;
			try {
				config = gson.fromJson(new FileReader("dataConexion.json"), HashMap.class);
				ArrayList<String> listaServers = config.get("servers");
					if (listaServers.size()>=2) {
						nMaestro = new NodoMaestro(listaServers);
						nMaestro.iniciarSv(0);
					}else {
							System.out.println("Error se requieren 2 servidores!");
						}
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				System.out.println("Error al Cargar el Archivo "+e.getMessage());
			} 
	}
}