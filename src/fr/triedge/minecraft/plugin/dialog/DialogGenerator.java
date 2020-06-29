package fr.triedge.minecraft.plugin.dialog;

import org.bukkit.entity.Player;

public class DialogGenerator {

	public static Dialog createDialogRegistrationEvent(Player player) {
		Dialog dial = new Dialog(player);
		dial.setDialogLine("Bienvnue a l'arena! Voulez vous participer?");
		dial.setDialogYes(new Dialog(player));
		return dial;
	}
}
