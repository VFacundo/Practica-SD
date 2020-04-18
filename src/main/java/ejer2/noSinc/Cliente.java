package ejer2.noSinc;

public class Cliente implements Runnable{
int deposito;
int extraccion;
int idCliente;
CuentaBanco cuenta;

	public Cliente(int depo,int extracc, int id, CuentaBanco cuenta){
		this.deposito = depo;
		this.extraccion = extracc;
		this.idCliente = id;
		this.cuenta = cuenta;
	}

	@Override
	public void run() {
		ThreadDepo depositarCuenta = new ThreadDepo(cuenta, deposito,idCliente);
		ThreadExtraccion extraerCuenta = new ThreadExtraccion(cuenta, extraccion,idCliente);
		Thread thDep = new Thread(depositarCuenta);
		Thread thEx = new Thread(extraerCuenta);
		thDep.start();
		thEx.start();
	}
	
	
}
