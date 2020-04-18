package ejer1.extremo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ServerExtremo implements Runnable{
	private final static Logger log = LoggerFactory.getLogger(ServerExtremo.class);
	int port;
	ArrayList<String> recursos;
	ServerSocket svExtremo;
	String carpeta;
	int reintentos=0;
	
public ServerExtremo(int port,ArrayList<String> recursos, String carpetaCompartida){
		initExtremo(port);
		this.port = port;
		this.recursos = recursos;
		this.carpeta = carpetaCompartida;
	}
	
	public void initExtremo(int port) {//Dado el caso que sumando el nro random igual se repita el puerto
		try {							//este metodo intenta la reconexion con diferentes puertos
			svExtremo = new ServerSocket(port);
		} catch (IOException e) {
			reintentos++;
			initExtremo(++port);
			log.info("[Sv-Side Extremo] No fue posible Iniciar Server Side Client, se intentaron: "+reintentos);
			e.printStackTrace();
		}
	}
	
	public void run() {
		String logName = ServerExtremo.class.getSimpleName().toString()+"-";
		MDC.put("log.name",logName);
		try {
			log.info("[Sv-Side Extremo] Se inicio el modo Servidor - En Cliente");
			while(true) {
				Socket client = svExtremo.accept();
				ThreadServerExtremo se = new ThreadServerExtremo(client,recursos,carpeta);
				Thread seThread = new Thread(se);
				seThread.start();
			}
	} catch (IOException e) {
		log.info("[Sv-Side Extremo] El puerto esta Ocupa3: "+e.getMessage());
	}
		MDC.remove(logName);
	}
}
