package fr.triedge.minecraft.plugin;

import java.io.IOException;

public class MetricSaverTask implements Runnable{

	private final MCPlugin plugin;
	
	public MetricSaverTask(MCPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		try {
			PluginMetrics.storeMetrics("metrics.info");
			plugin.getLogger().info("Metrics stored to metrics.info");
		} catch (IOException e) {
			plugin.getLogger().severe("Cannot save metrics to metrics.info");
		}
	}
}
