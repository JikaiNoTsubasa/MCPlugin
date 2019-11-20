package fr.triedge.minecraft.plugin.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Material;

public class Schema {

	private ArrayList<Bloc> blocs = new ArrayList<>();

	public void store(File file) throws IOException {
		FileWriter w = new FileWriter(file);
		for (Bloc bloc : getBlocs()) {
			w.write(bloc.toStore()+"\r\n");
			w.flush();
		}
		w.close();
	}
	
	public void load(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] el = line.split("::");
			Bloc b = new Bloc();
			b.x = Integer.parseInt(el[0]);
			b.y = Integer.parseInt(el[1]);
			b.z = Integer.parseInt(el[2]);
			b.material = Material.getMaterial(el[3].toUpperCase());
			getBlocs().add(b);
		}
		scan.close();
	}

	public void rotate(Direction dir) {
		switch (dir) {
		case CLOCKWISE:
			rotateClockwize();
			break;
		case COUNTER_CLOCKWISE:
			rotateCounterClockwize();
			break;
		default:
			break;
		}
	}

	private void rotateCounterClockwize() {
		for (Bloc bloc : getBlocs()) {
			int tmp_x = bloc.x;
			int tmp_z = bloc.z;
			bloc.x = -tmp_z;
			bloc.z = tmp_x;
		}
	}

	private void rotateClockwize() {
		for (Bloc bloc : getBlocs()) {
			int tmp_x = bloc.x;
			int tmp_z = bloc.z;
			bloc.x = tmp_z;
			bloc.z = -tmp_x;
		}
	}

	public ArrayList<Bloc> getBlocs() {
		return blocs;
	}

	public void setBlocs(ArrayList<Bloc> blocs) {
		this.blocs = blocs;
	}

	enum Direction{
		CLOCKWISE, COUNTER_CLOCKWISE
	}
}
