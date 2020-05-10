package Application;

import Entities.Cliente;
import Entities.Servidor;

public class clienteProgram {
	public static void main(String args[]) throws Exception {
		Cliente.iniciaCliente("127.0.0.1", 5555);
	}
}
