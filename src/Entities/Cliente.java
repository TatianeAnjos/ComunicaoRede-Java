package Entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Cliente extends Thread {

	private Socket conexao;
	private static String nomeCliente;

	public Cliente(Socket socket) {
		this.conexao = socket;
	}

	// Inicia cliente informando o nome ao servidor
	public static void iniciaCliente(String servidor, int porta) {// "127.0.0.1", 5555
		try {

			Socket socket = new Socket(servidor, porta);
			PrintStream saida = new PrintStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Digite seu nome para entrar: ");
			nomeCliente = br.readLine();

			Thread thread = new Cliente(socket);
			thread.start();
			// leva nome para o servidor
			saida.println(nomeCliente);
			String msg = "";
			//Envia msg se não for nula, se for nula encerra a conexão
			while (true) {
				if (msg == null) {
					System.out.println("Conexão encerrada!");
					socket.close();
					br.close();
					break;
				}
				System.out.print(">>>");
				msg = br.readLine();
				saida.println(msg);
			}

			br.close();
			socket.close();

		} catch (IOException e) {
			System.out.println("Erro: " + e);
		}
	}
	// recebe a mensagem que o servidor está enviando(dos outros clientes) e envia resposta.
	public void run() {
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
			String msg = " ";
			while (true) {
				if (msg == null) {
					System.out.println("Conexão encerrada!");
					br.close();
					System.exit(0);
					conexao.close();
				}
				msg = br.readLine();
				System.out.println(msg);
				System.out.print(">>> ");
			}
		} catch (IOException e) {
			System.out.println("Erro: " + e);
		}
	}

}