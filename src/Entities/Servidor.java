package Entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class Servidor extends Thread {

	private Socket conexao;
	private String nomeCliente;
	private int chave = 0;
	private static Vector clientes;

	// Construtor recebe a porta pra conectar
	public Servidor(Socket socket) {
		this.conexao = socket;
	}

	// Inicia o servidor, recebe uma conexão na porta, e inicia num novo thread para cada cliente
	public static void iniciaServidor(int porta) {
		try {
			clientes = new Vector();
			ServerSocket server = new ServerSocket(porta);
			System.out.println("ServidorSocket rodando na porta " + porta);
			while (true) {
				Socket conexao = server.accept();
				Thread thread = new Servidor(conexao);
				thread.start();
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

//recebe o nome do cliente e adiciona na lista de clientes conectados, assim é possível mandar msg a todos.
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
			PrintStream saida = new PrintStream(this.conexao.getOutputStream());
			this.nomeCliente = entrada.readLine();
			System.out.println(this.nomeCliente + ": entrou no chat!");
			clientes.add(saida);
			String msg = entrada.readLine();
			// manda msg pra todos
			while (msg != null && !(msg.trim().equals(""))) {
				notificaClientes(saida, msg);
				msg = entrada.readLine();
			}
			System.out.println(this.nomeCliente + " saiu!");
			notificaClientes(saida,"saiu!");
			clientes.remove(this.nomeCliente);
			this.conexao.close();

		} catch (IOException e) {
			System.out.println("Erro: " + e);
		}
	}

	// Recebe todos os clientes na lista e envia a todos os outros exceto a quem está enviando a msg
	public void notificaClientes(PrintStream saida, String msg) throws IOException {
		Enumeration e = clientes.elements();
		while (e.hasMoreElements()) {
			PrintStream ps = (PrintStream) e.nextElement();
			if (ps != saida) {
				ps.println(this.nomeCliente + ": " + msg);
			}
		}
	}

}
