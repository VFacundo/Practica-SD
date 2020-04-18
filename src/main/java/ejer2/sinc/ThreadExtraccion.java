package ejer2.sinc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadExtraccion implements Runnable{
CuentaBanco cuenta;
int extraccion;
int idCliente;
	
	public ThreadExtraccion(CuentaBanco c, int valor,int id) {
		this.cuenta = c;
		this.extraccion = valor;
		this.idCliente = id;
	}
	
	@Override
	public void run() {
		final Logger log = LoggerFactory.getLogger(ThreadExtraccion.class);
		String logName = ThreadExtraccion.class.getSimpleName().toString();
		MDC.put("log.name",logName);
		int i = 0;
		while(i<15) {
			if(extraccion<=cuenta.getValue()) {
				synchronized (this.cuenta) {
					cuenta.extraccion(extraccion,idCliente);
				}
			} else
				log.info("CLIENTE: "+idCliente+" SIN DINERO DISPONIBLE!!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			i++;
		}
	}

}
