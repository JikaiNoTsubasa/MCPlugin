package fr.triedge.minecraft.plugin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class HTTPDashboard implements Runnable{

	private int port;
	private int toMB = 1048576;
	private boolean running = true;

	@Override
	public void run() {
		try {
			ServerSocket listener = new ServerSocket(getPort());
			System.out.println("Server listening to port: "+getPort());
			while (isRunning()) {
				Socket sok = listener.accept();
				System.out.println("Connection received");
				replyToRequest(sok);
			}
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void replyToRequest(Socket sok) {
		// we read characters from the client via input stream on the socket
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(sok.getInputStream()));
			// we get character output stream to client (for headers)
			PrintWriter out = new PrintWriter(sok.getOutputStream());
			// get binary output stream to client (for requested data)
			BufferedOutputStream dataOut = new BufferedOutputStream(sok.getOutputStream());

			// get first line of the request from the client
			//String input = in.readLine();
			// we parse the request with a string tokenizer
			//StringTokenizer parse = new StringTokenizer(input);
			//String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			// we get file requested
			//if (method.equals("GET")) { // GET method so we return content

			// send HTTP Headers
			out.println("HTTP/1.1 200 OK");
			out.println("Server: Java HTTP Server from SBI : 1.0");
			out.println("Date: " + new Date());
			out.println(); // blank line between headers and content, very important !
			out.flush(); // flush character output stream buffer

			//dataOut.write(fileData, 0, fileLength);
			dataOut.write(writePage().getBytes());
			dataOut.flush();
			//}
			out.close();
			dataOut.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String writePage() {
		StringBuilder tmp = new StringBuilder();
		String page = "";
		try {
			Scanner scan = new Scanner(new File("example.html"));
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				tmp.append(line);
				tmp.append("\n");
			}
			scan.close();

			MemoryUsage mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			double mem_heap_percent = (mem.getUsed()*100)/mem.getMax();
			long mem_os_available = getAvailableMemory();
			long mem_os_total = getTotalMemory();
			double mem_os_max = mem_os_total/toMB;
			double mem_os_cur = (mem_os_total - mem_os_available)/toMB;
			double mem_os_percent = 100 - ((getAvailableMemory() * 100) / getTotalMemory());
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			String player_list = "";
			for (Player p : players)
				player_list += p.getDisplayName()+"<br />";

			page = tmp.toString();
			page = page.replaceAll("##CONNECTED##", player_list);
			page = page.replaceAll("##CPU_PERCENT##", String.valueOf(getCpu()));
			page = page.replaceAll("##MEM_OS_PERCENT##", String.valueOf(mem_os_percent));
			page = page.replaceAll("##MEM_OS_CUR##", String.valueOf(mem_os_cur));
			page = page.replaceAll("##MEM_OS_MAX##", String.valueOf(mem_os_max));
			page = page.replaceAll("##MEM_HEAP_PERCENT##", String.valueOf(mem_heap_percent));
			page = page.replaceAll("##MEM_HEAP_CUR##", String.valueOf(mem.getUsed()/toMB));
			page = page.replaceAll("##MEM_HEAP_MAX##", String.valueOf(mem.getMax()/toMB));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return page;
	}

	private long getAvailableMemory() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		return hal.getMemory().getAvailable();
	}

	private long getTotalMemory() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		return hal.getMemory().getTotal();
	}

	private double getCpu() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		CentralProcessor processor = hal.getProcessor();
		long[] prevTicks = processor.getSystemCpuLoadTicks();

		return processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
	}

	public HTTPDashboard(int port) {
		setPort(port);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}


}
