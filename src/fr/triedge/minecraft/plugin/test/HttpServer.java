package fr.triedge.minecraft.plugin.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HttpServer {

	public static void main(String[] args) {
		try {
			ServerSocket listener = new ServerSocket(8888);
			System.out.println("Listener started");
			Socket sok = listener.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(sok.getInputStream()));
			PrintWriter out = new PrintWriter(sok.getOutputStream());
			BufferedOutputStream dataOut = new BufferedOutputStream(sok.getOutputStream());
			String input = in.readLine();
			System.out.println("In: "+input);
			
			out.println("HTTP/1.1 200 OK");
			out.println("Server: Java HTTP Server from SBI : 1.0");
			out.println("Date: " + new Date());
			out.println(); // blank line between headers and content, very important !
			out.flush(); // flush character output stream buffer
			
			dataOut.write("Test".getBytes());
			dataOut.flush();
			//}
			out.close();
			dataOut.close();
			in.close();
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
