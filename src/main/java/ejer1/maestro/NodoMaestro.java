package ejer1.maestro;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;

public class NodoMaestro{
	int port;
	ArrayList<String> listaServers;
	//Socket
	BufferedReader inputChannel;
	PrintWriter outputChannel;
	HashMap<String, String> listaRecursos = new HashMap<String, String>();
	//Socket
	private final Logger log = LoggerFactory.getLogger(NodoMaestro.class);//var log
	
	public NodoMaestro(ArrayList<String> listaServers){
		this.listaServers = listaServers;
		int thread = (int) Thread.currentThread().getId();
		String logName = NodoMaestro.class.getSimpleName().toString()+"-"+thread;
		MDC.put("log.name",logName);
	}
		
	public void iniciarSv(int nro) {
		int nroServer = nro;
		try {
			//Server Socket
			if(nroServer<=listaServers.size()) {
				String parts[] = listaServers.get(nroServer).split(":");
				this.port = Integer.parseInt(parts[1]);
				ServerSocket serverMaestro = new ServerSocket(this.port);
				log.info("Server is Running on port: "+this.port);
			
			while(true) {
				Socket client = serverMaestro.accept();
				///////THREAD/////
				ThreadServerMaestro ts = new ThreadServerMaestro(client,listaServers,port,listaRecursos);
				Thread tsThread = new Thread(ts);
				tsThread.start();
				//////////////////
				}
			}
			
			}catch (Exception e) {
				iniciarSv(++nroServer);
				log.info("Socket on port \"+this.port+\" is used "+e.getMessage());
		}
	}
}
