package ejer1.extremo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadServerExtremo implements Runnable {
	Socket client;
	PrintWriter outputChannel;
	BufferedReader inputChannel;
	String msgIn;
	String user;
	String userTo;
	ArrayList<String> recursosDisponibles;
	String carpetaCompartida;
	private final Logger log = LoggerFactory.getLogger(ThreadServerExtremo.class);
	
	public ThreadServerExtremo(Socket client,ArrayList<String> rr, String carpeta) {
		this.client = client;
		this.recursosDisponibles = rr;
		this.carpetaCompartida = carpeta;
		try {
			this.outputChannel = new PrintWriter (client.getOutputStream(), true);//Flujo de Salida del Cliente
			this.inputChannel = new BufferedReader (new InputStreamReader (client.getInputStream()));//Flujo Entrante
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	};
	
public String buscarArchivo(String aBuscar) {
	int i = 0;
	while(i<recursosDisponibles.size()) {
		if(aBuscar.equalsIgnoreCase(recursosDisponibles.get(i))) {
			log.info("[Sv-Side Extremo] "+recursosDisponibles.get(i));
			aBuscar = recursosDisponibles.get(i);
			i = recursosDisponibles.size();
		}
		i++;
	}
return aBuscar;
}
	
public void enviarArchivo() {
	try {
		DataOutputStream dataOut = new DataOutputStream(client.getOutputStream());
		String nombreArchivo = this.inputChannel.readLine();
		nombreArchivo = buscarArchivo(nombreArchivo);
		String rutaArch = carpetaCompartida+System.getProperty("file.separator")+nombreArchivo;
		File archivo = new File(rutaArch);
		FileInputStream fileIn = new FileInputStream(rutaArch);
		BufferedInputStream bis = new BufferedInputStream(fileIn);
		BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
		
		int tamanioArch = (int) archivo.length();
		dataOut.writeInt(tamanioArch);
		//Array Bytes
		byte[] buffer = new byte[tamanioArch];
		//Leo archivo
		bis.read(buffer);
		//envio arch
		for(int i=0;i<buffer.length;i++) {
			bos.write(buffer[i]);
		}
		
		fileIn.close();
		bis.close();
		bos.close();
		
	} catch (IOException e) {
	}
}
		

public void run() {
		String packetName=ThreadServerExtremo.class.getSimpleName().toString()+"-"+Thread.currentThread().getId();
		MDC.put("log.name",packetName);
		log.info("[Sv-Side Extremo] Client Connect! from: "+client.getInetAddress()+":"+client.getPort());//Conexion
		try {
			int option = 0;
			while (option!=3) {
				msgIn = this.inputChannel.readLine();//Leo el mensaje del client
				msgIn.trim();//Elimino los espacios
				option = Integer.parseInt(msgIn);//Transformo a INT 
				
				switch (option) {
					case 1:
						enviarArchivo();
						break;
					case 2:
						
						break;
					case 3:
						log.info("[Sv-Side Extremo] El Cliente "+user+" se Desconecto");
						client.close();
						break;
				}
			};
			
		} catch (Exception e) {
	
		};
		
	};
}

