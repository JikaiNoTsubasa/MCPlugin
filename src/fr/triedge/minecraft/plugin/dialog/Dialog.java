package fr.triedge.minecraft.plugin.dialog;

import org.bukkit.entity.Player;

public class Dialog {

	private Player player;
	private String dialogLine;
	private Dialog dialogYes, dialogNo;
	
	public Dialog(Player player) {
		setPlayer(player);
	}
	
	public void execute() {
		
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Dialog getDialogNo() {
		return dialogNo;
	}

	public void setDialogNo(Dialog dialogNo) {
		this.dialogNo = dialogNo;
	}

	public Dialog getDialogYes() {
		return dialogYes;
	}

	public void setDialogYes(Dialog dialogYes) {
		this.dialogYes = dialogYes;
	}

	public String getDialogLine() {
		return dialogLine;
	}

	public void setDialogLine(String dialogLine) {
		this.dialogLine = dialogLine;
	}
}
