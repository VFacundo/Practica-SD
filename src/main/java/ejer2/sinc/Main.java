package ejer2.sinc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final Logger log = LoggerFactory.getLogger(Main.class);
		String logName = Main.class.getSimpleName().toString();
		MDC.put("log.name",logName);
		
		CuentaBanco cuenta = new CuentaBanco(1000);
		Cliente c1 = new Cliente(100, 150, 1, cuenta);
		Cliente c2 = new Cliente(200, 350, 2, cuenta);
		
		Thread thC1 = new Thread(c1);
		Thread thC2 = new Thread(c2);
		thC1.start();
		thC2.start();
		thC2.join();
	}

}
