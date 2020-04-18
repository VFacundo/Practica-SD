package ejer1.extremo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LeecherExtremo implements Runnable{
	
String ip;
int puerto;
private static Logger log = LoggerFactory.getLogger(LeecherExtremo.class);
Socket s;
String nombreArchivo;
	
	public LeecherExtremo(String ip, int puerto, String nombreArchivo) {
		this.ip = ip;
		this.puerto = puerto;
		this.nombreArchivo = nombreArchivo;
		conectar();
	}
	
	public boolean socketStatus() {//Verifica si el socket sigue activo
		return s.isConnected();
	}
	
	public int conectar() {
		int conectado = 0;
		try {
			s = new Socket(ip,puerto);
			conectado = 1;
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		return conectado;
	}
	
	@Override
	public void run() {
		String logName = LeecherExtremo.class.getSimpleName().toString()+"-";
		MDC.put("log.name",logName);
		
		try {
			DataInputStream dataInput = new DataInputStream(s.getInputStream());
			PrintWriter outChannel = new PrintWriter(s.getOutputStream(),true);
			//Nombre de Archivo
			outChannel.println("1");
			outChannel.println(nombreArchivo);
			//Tamaño del Archivo
			int tamañoArchivo = dataInput.readInt();
			log.info("[Leecher Thread] Se va a descargar: "+nombreArchivo+" ,tamaño: "+tamañoArchivo);
			File directorio = new File("extremoRecibidos"+Thread.currentThread().getId());
			directorio.mkdir();
			FileOutputStream fos = new FileOutputStream(directorio.getName()+System.getProperty("file.separator")+nombreArchivo);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
			
			byte[] buffer = new byte[tamañoArchivo];
			
			for(int i=0; i<buffer.length;i++) {
				buffer[i] = (byte)bis.read();
			}
			bos.write(buffer);
			outChannel.println(3);
			bos.flush();
			bis.close();
			bos.close();
			s.close();
			log.info("[Leecher Thread] Archivo Descargado a "+directorio.getPath());
		} catch (IOException e) {
			log.info("[Leecher Thread] No fue posible descargar el archivo solicitado: "+e.getMessage());
		}
	}

}
