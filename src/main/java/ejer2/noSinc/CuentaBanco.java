package ejer2.noSinc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ejer1.maestro.ThreadServerMaestro;

public class CuentaBanco {
int valorInicial;
private final Logger log = LoggerFactory.getLogger(CuentaBanco.class);
	
public CuentaBanco(int valor) {
		this.valorInicial = valor;
		int thread = (int) Thread.currentThread().getId();
		String logName = CuentaBanco.class.getSimpleName().toString()+"-"+thread;
		MDC.put("log.name",logName);
	}

	public int getValue() {
		return this.valorInicial;
	}
	
	public void extraccion(int valor, int idCliente) {
		log.info("CLIENTE: "+idCliente+" ->Valor antes de Realizar Extraccion: "+valorInicial);
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valorInicial -= valor;	
		
		log.info("CLIENTE: "+idCliente+" ->Valor luego de Realizar Extraccion: "+valorInicial);
	}
	
	public void deposito(int valor, int idCliente) {
		log.info("CLIENTE: "+idCliente+" ->Valor antes de Realizar DEPOSITO: "+valorInicial);
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valorInicial+=valor;
		log.info("CLIENTE: "+idCliente+" ->Valor luego de Realizar DEPOSITO: "+valorInicial);
	}
}
