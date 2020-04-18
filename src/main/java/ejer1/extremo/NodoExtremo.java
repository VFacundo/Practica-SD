package ejer1.extremo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.google.gson.Gson;


public class NodoExtremo {
	private final static Logger log = LoggerFactory.getLogger(NodoExtremo.class);
	static BufferedReader inputChannel;
	static PrintWriter outputChannel;
	static ObjectOutputStream outputOb;
	String mensaje;
	Scanner sc = new Scanner(System.in);
	static ArrayList<String> recursos;
	String carpetaCompartida;
	Gson gson;
	static ArrayList<String> listaServers;
	static Socket s = null;
	static int portServerSide;

public NodoExtremo(ArrayList<String> listaServers) {
	this.listaServers = listaServers;
	int thread = (int) Thread.currentThread().getId();
	String logName = NodoExtremo.class.getSimpleName().toString()+thread;
	MDC.put("log.name",logName);
}
	
public static boolean conectarAlMaestro(int nroServer) {
	
	int port;
	String serverIp;
	boolean conectado = false;
	while((conectado!=true) && (nroServer<=listaServers.size()-1)) {
		try {
			///Luego de leer el config, intento la conexion
			String parts[] = listaServers.get(nroServer).split(":");//Obtengo IP:PORT
			serverIp = parts[0];
			port = Integer.parseInt(parts[1]);
			s = new Socket(serverIp,port);
			log.info("[Cliente] Connected to SV MAESTRO");
			inputChannel = new BufferedReader (new InputStreamReader (s.getInputStream()));
			outputChannel = new PrintWriter (s.getOutputStream(), true);
			outputOb = new ObjectOutputStream(s.getOutputStream());
			s.setSoTimeout(1000);//timeout read
			conectado = true;
				}catch (IOException e){
					log.info("Intento conectar con el Sv "+nroServer+" "+e.getMessage());	
					++nroServer;//Si la conexion falla, intento nuevamente
			}
	}
return conectado;
}

public ArrayList<String> listaRecursos(String ruta){//Recupero todos los files del directorio compartido
	carpetaCompartida = ruta;
	File dir = new File(ruta);
	ArrayList<String> recursos = new ArrayList<String>();
	String[] ficheros = dir.list();
	if(ficheros==null) {
		System.out.println("Sin ficheros");
	}else {
		for (String recu : ficheros) {
			File dir2 = new File(ruta+"/"+recu);
				if(!dir2.isDirectory()) {
					recursos.add(recu);
				}	
		}
	}
return recursos;
}

public static void anunciarAlSv(){//Anuncio al master mis archivos
	outputChannel.println("1");
	outputChannel.println(portServerSide);
	try {
		Thread.sleep(3000);
		outputOb.writeObject(recursos);
		outputOb.flush();
		log.info("[Cliente] Recursos Enviados Al Servidor Maestro!");
	} catch (IOException | InterruptedException e) {
		log.info("[Cliente] Error los Recursos No fueron Enviados");
		conectarAlMaestro(0);
	}
}

public void consultarRecurso() {//Consulta al servidor Maestro por un recurso
	System.out.println("Ingrese el nombre del Recurso (con Extension!)");
	String nombreRecurso = sc.nextLine().trim();
	try {
		nombreRecurso = nombreRecurso.trim().toLowerCase();
		outputChannel.println("2");
		outputChannel.println(nombreRecurso);
		String direccion = inputChannel.readLine();
		if(!direccion.equals("0")) {
			String parts[] = direccion.split(":");
			descargarRecurso(parts[0], Integer.parseInt(parts[1]), nombreRecurso);
		}else {
			System.out.println("El Recurso No fue Encontrado!");
		}	
	} catch (IOException | NullPointerException e) {
		log.info("[Cliente] Se perdio la conexion con el Server Maestro.. "+e.getMessage());
		if(conectarAlMaestro(0)) {
			log.info("[Cliente] Conexion Restablecida!!");
		}else {
			log.info("[Cliente] No fue Posible Recuperar la Conexion..");
		}
	}
}

public void descargarRecurso(String ip, int puerto, String nombreArchivo){//Conexion entre Extremos para descargar el Archivo
	LeecherExtremo le = new LeecherExtremo(ip, puerto,nombreArchivo);
	if(le.socketStatus()) {
		Thread threadLe = new Thread(le);
		threadLe.start();
	}else
		log.info("[Cliente] No fue posible conectar con :"+ip+":"+puerto+" para descargar el Archivo");
}

public void inicio(int nroServer) {

	if (conectarAlMaestro(nroServer)){
		////////Iniciar Server-side
		String parts[] = listaServers.get(listaServers.size()-1).split(":");
		Random rnd = new Random();
		portServerSide =  Integer.parseInt(parts[1])+rnd.nextInt(100);
		//Como todos los sv estan en localhost, para iniciar el sv-side del cliente le asigno
		//como numero de puerto el ultimo puerto de la lista de maestros + un nro random
		System.out.println("");
		System.out.println("Nodo Extremo Iniciado!");
		System.out.println("Ingrese la Ruta de la Carpeta Contenedora de Recursos Compartidos: ");
		mensaje = sc.nextLine();
		ArrayList<String> rr = listaRecursos(mensaje);
		if(!rr.isEmpty()) {
			System.out.print("Recursos Disponibles: ");
			for (String string : rr) {
				System.out.print(string+", ");
			}
			System.out.println("");
			recursos = rr;
			anunciarAlSv();
		}
		iniciarServerSide(portServerSide,recursos);
		initPing();
		menuCliente();
	}else {
		log.info("[Cliente] No fue posible la conexion con el Maestro!");
	}
}

public static void initPing() {
	PingMaestro p = new PingMaestro();
	Thread thPing = new Thread(p);
	thPing.start();
}

public void menuCliente() {
	int opcion = 0;
	
	while(opcion != 9) {
		System.out.println("------------MENU-CLIENTE-------------");
		System.out.println("1- Descargar un Archivo             -");
		System.out.println("2- Ver Archivos Locales Compartidos -");
		System.out.println("9- Salir!                           -");
		System.out.println("-------------------------------------");
		System.out.println("Opcion:");
		try {
			opcion = Integer.parseInt(sc.nextLine().trim());
			
			switch (opcion) {
			case 1:
				consultarRecurso();
			break;
			
			case 2:
				System.out.println(recursos);
				break;
			case 9:
				outputChannel.println("9");
				break;
			default:
				System.out.println("Opcion Incorrecta!");
				break;
			}
		} catch (NumberFormatException e) {
			System.out.println("Error, Ingrese una opcion valida!");
		}
	}
}

public void iniciarServerSide(int port,ArrayList<String> recursos) {
	ServerExtremo se = new ServerExtremo(port, recursos, carpetaCompartida);
	Thread seThread = new Thread(se);
	seThread.start();
}
}

