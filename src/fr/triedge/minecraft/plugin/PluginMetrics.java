package fr.triedge.minecraft.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class PluginMetrics {
	
	public static void storeMetrics(String path) throws IOException {
		FileWriter w = new FileWriter(new File(path));
		w.write("heap_usage="+getHeapUsage()+"\n");
		w.write("heap_max="+getHeapMax()+"\n");
		w.write("heap_percent="+getHeapUsagePercent()+"\n");
		w.write("connected="+getOnlinePlayers()+"\n");
		w.flush();
		w.close();
	}
	
	public static String getOnlinePlayers() {
		if (Bukkit.getOnlinePlayers().size() > 0) {
			String list = "";
			for (Player p : Bukkit.getOnlinePlayers()) {
				list += p.getName()+",";
			}
			return list;
		}else {
			return "-";
		}
	}

	public static double getHeapUsagePercent() {
		MemoryUsage mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return (mem.getUsed()*100)/mem.getMax();
	}
	
	public static double getHeapUsage() {
		MemoryUsage mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return mem.getUsed()/1048576;
	}
	
	public static double getHeapMax() {
		MemoryUsage mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return mem.getMax()/1048576;
	}
	
	public static long getAvailableMemory() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		return hal.getMemory().getAvailable()/1048576;
	}
	
	public static long getTotalMemory() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		return hal.getMemory().getTotal()/1048576;
	}
}
