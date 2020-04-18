package ejer3.run;

import java.rmi.NotBoundException;

import ejer3.Cliente;

public class SimulacionMultiplesClientes {
	public void newClient(final int n) {
		new Thread(new Runnable() {
		    public void run() {
		    	try {
					Cliente.main(n);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
		    }
		}).start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			System.err.println("Nope");
		}
	}
	
	public SimulacionMultiplesClientes() {
		for (int i = 0; i < 30; i++) {
			newClient(i);
		}
	}
	
	
	
	public static void main(String[] args) {
		new SimulacionMultiplesClientes();
	}

}
