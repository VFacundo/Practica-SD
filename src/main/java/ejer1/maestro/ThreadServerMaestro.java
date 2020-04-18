package ejer1.maestro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadServerMaestro implements Runnable{
	Socket client;
	PrintWriter outputChannel;
	BufferedReader inputChannel;
	ObjectInputStream inputOb;
	String msgIn;
	private final Logger log = LoggerFactory.getLogger(ThreadServerMaestro.class);
	ArrayList<String> listaServers; 
	HashMap<String, String> recursosNodos;
	int localPort;
	String direccionSvCli;
	
	public ThreadServerMaestro(Socket client,ArrayList<String> listaServers,int localPort,HashMap<String, String> lista) {
		this.localPort = localPort;
		this.client = client;
		this.listaServers = listaServers;
		this.recursosNodos = lista;
		try {
			this.outputChannel = new PrintWriter (client.getOutputStream(), true);//Flujo de Salida del Cliente
			this.inputChannel = new BufferedReader (new InputStreamReader (client.getInputStream()));//Flujo Entrante
			this.inputOb = new ObjectInputStream(client.getInputStream());
		} catch (Exception e) {
		
		}
	}
	
	public void registrarExtremosArchivos(){//Recibo los nombres d archivos del nodo extremo
		String ip = client.getInetAddress().toString();
		String port;
		ip = ip.replace("/", "");
			try {
				try {
					port = this.inputChannel.readLine();
					ArrayList<String> arr = (ArrayList<String>) this.inputOb.readObject();//Leo la lista de recursos del Extremo
							for (String recurso : arr) {
								recursosNodos.put(recurso.trim().toLowerCase(), ip+":"+port);//Recorro y guardo local
						}//Borro espacios y paso a minusc.
							log.info("[SV MAESTRO] Recursos DISPONIBLES "+recursosNodos);
							direccionSvCli = ip+":"+port;
							replicar();
					} catch (IOException e) {}
				} catch (ClassNotFoundException e) {e.printStackTrace();}
	}
	
	public void replicar() {//Envio mi lista al SV de replicas
		if(listaServers.size()>=2) {
			String parts[] = listaServers.get(1).split(":");
			if((Integer.parseInt(parts[1]))!=localPort) {
				try {//Tengo respaldo
					Socket s = new Socket(parts[0],Integer.parseInt(parts[1]));
					PrintWriter outputChannel = new PrintWriter (s.getOutputStream(), true);
					ObjectOutputStream outputOb = new ObjectOutputStream(s.getOutputStream());
					outputChannel.println("3");
					Thread.sleep(3000);
					outputOb.writeObject(recursosNodos);
					outputOb.flush();
					log.info("[SV MAESTRO] Se Replico la Lista de Recursos a: "+parts[0]+":"+parts[1]);
				} catch (NumberFormatException | IOException | InterruptedException e) {
					log.info("[SV MAESTRO] No fue posible Replicar la Informacion "+e.getMessage());
				}
			}else{log.info("[SV MAESTRO] Trabajando sin SV de Replica!");}//Estoy solo
			
		}else{log.info("[SV MAESTRO] Trabajando sin SV de Replica!");}//Estoy solo
	}
	
	public void guardarReplica() {
		try {
			log.info("[SV MAESTRO] Obteniendo la Replica..");
			HashMap<String, String> listaReplica = (HashMap<String, String>)this.inputOb.readObject();
				if(listaReplica.size()<recursosNodos.size()) {
					recursosNodos.clear();
				}
			listaReplica.forEach((k,v)->recursosNodos.put(k, v));
			log.info("[SV SECUNDARIO]  Se Replico la Lista del SV 1");
		} catch (ClassNotFoundException | IOException e) {
			log.info("[SV SECUNDARIO] No se pudo recibir la Informacion de Replica!"+e.getMessage());
		} 
	}
	
	public void atenderConsulta() {
		try {
			String nombreRecurso = inputChannel.readLine();
			if(recursosNodos.containsKey(nombreRecurso)) {
				outputChannel.println(recursosNodos.get(nombreRecurso));
			}else {
				outputChannel.println("0");
			}
		} catch (IOException e) {
			log.info("El Cliente no Responde.. "+e.getMessage());
		}
	}
	
	public void eliminarArchivosRegistrados() {
		Iterator it = recursosNodos.entrySet().iterator();
		while(recursosNodos.containsValue(direccionSvCli) && it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			if(e.getValue().equals(direccionSvCli)) {
				it.remove();
			}
		}
		replicar();
	}
	
	public void run() {
		int thread = (int) Thread.currentThread().getId();
		String logName = ThreadServerMaestro.class.getSimpleName().toString()+"-"+"port:"+this.localPort;
		MDC.put("log.name",logName);
		log.info("Client Connect! from: "+client.getInetAddress()+":"+client.getPort());//Conexion
		//System.out.println(recursosNodos);
		try {
			int option = 0;
				while(option!=9) {
					msgIn = this.inputChannel.readLine();//Leo el mensaje del client
					msgIn.trim();//Elimino los espacios
					option = Integer.parseInt(msgIn);//Transformo a INT 

					switch (option) {
						case 1:
							registrarExtremosArchivos();
							break;
						case 2:
							atenderConsulta();
							break;
						case 3:
							guardarReplica();
							break;
						case 4:
							this.outputChannel.println("OK");
							break;
						case 9:
							eliminarArchivosRegistrados();
							log.info("El Cliente se desconecto! :"+client.getInetAddress()+":"+client.getPort());
							client.close();
						break;
					}
				}
			
		} catch (Exception e) {
	
		}
		
	}

}
