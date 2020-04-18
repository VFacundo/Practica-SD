package ejer1.extremo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingMaestro implements Runnable{

BufferedReader inputChannel;
PrintWriter outputChannel;
private final static Logger log = LoggerFactory.getLogger(PingMaestro.class);
	@Override
	public void run() {
		String aux;
		while (true) {
			try {
				Thread.sleep(10000);
				NodoExtremo.outputChannel.println("4");
				aux = NodoExtremo.inputChannel.readLine();
				//System.out.println("Ping OK"+aux);
					if(aux==null) {
						log.info("[ALERT] Se perdio la Conexion con el SV Maestro!");
						if(NodoExtremo.conectarAlMaestro(0)) {
							NodoExtremo.anunciarAlSv();
						}
					}
			} catch (IOException | InterruptedException e) {
				NodoExtremo.conectarAlMaestro(0);
			}
		}	
	}
	
		
	
} 

