package ejer2.sinc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadDepo implements Runnable {

CuentaBanco cuenta;
int deposito;
int idCliente;

	public ThreadDepo(CuentaBanco c, int valor, int id) {
		this.cuenta = c;
		this.deposito = valor;
		this.idCliente = id;
	}

	@Override
	public void run() {
		final Logger log = LoggerFactory.getLogger(ThreadDepo.class);
		String logName = ThreadDepo.class.getSimpleName().toString();
		MDC.put("log.name",logName);
		int i = 0;
		while(i<15) {
			synchronized (this.cuenta) {
				this.cuenta.deposito(deposito,idCliente);
			}
			i++;
		}
	}

}
