package Entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

public class Cliente extends Thread {

	private static Socket conexao;
	private static String nomeCliente;

	public Cliente(Socket socket) {
		this.conexao = socket;
	}

	// Inicia cliente informando o nome ao servidor
	public static void iniciaCliente(String servidor, int porta) throws Exception {// "127.0.0.1", 5555 como exemplo
																					// local host.
		try {

			Socket socket = new Socket(servidor, porta);
			PrintStream saida = new PrintStream(socket.getOutputStream());
			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(in);

			System.out.print("Digite seu nome para entrar: ");
			nomeCliente = br.readLine();

			Thread thread = new Cliente(socket);
			thread.start();
			// leva nome para o servidor
			saida.println(nomeCliente);
			String msg = "";
			
			// Envia msg se não for nula, se for nula encerra a conexão
			while (true) {
				if (msg == null) {
					System.out.println("Conexão encerrada!");
					socket.close();
					br.close();
					break;
				}
				//Esse if deve ser implementado com um botão
				if (msg == "Enviar Arquivo" || (msg.trim().equals("Enviar Arquivo"))) {
					enviarArquivo(saida);
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

	// recebe a mensagem que o servidor está enviando(dos outros clientes) e envia
	// resposta.
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
				System.out.print(">>>");
			}
		} catch (IOException e) {
			System.out.println("Erro: " + e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Envia todos os tipos de arquivo
	private static void enviarArquivo(PrintStream saida) throws IOException {
		try {
			// Abre janela pra selecionar arquivo;
			JFileChooser jfc = new JFileChooser();
			File f = null;
			int opt = jfc.showOpenDialog(null);
			if (opt == jfc.APPROVE_OPTION) {
				f = jfc.getSelectedFile();
			}
			//Recebe o arquivo que o cliente selecionou e leva para todos conectados ao chat
			System.out.println("Enviando Arquivo... .. .");
			long time = System.currentTimeMillis();

			FileReader fr = new FileReader(f);
			FileInputStream in = new FileInputStream(f);
			//Pasta aleatório do localhost, o arquivo é gerado dentro da pasta nesse caminho.
			FileOutputStream out = new FileOutputStream("c:\\z\\" + time + "_" + f.getName());
			FileChannel fin = in.getChannel();
			FileChannel fout = out.getChannel();
			
			long size = fin.size();
			fin.transferTo(0, size, fout);
			saida.println("Enviando Arquivo... .. .");
			saida.println("Nome do arquivo: " + f.getName());
			System.out.println("Arquivo " + f.getName()+" Enviado!!!");

		} catch (FileNotFoundException e1) {
			e1.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}
		
	}
}
