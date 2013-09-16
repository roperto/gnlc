package net.geral.slotcar.lapcounter.structs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.core.Storage;

public class PilotsData {
	private static class SortByNameComparator implements Comparator<Pilot> {
		@Override
		public int compare(final Pilot a, final Pilot b) {
			if (a == b) return 0;
			if (a == null) return -1;
			if (b == null) return 1;
			
			return a.getName().compareToIgnoreCase(b.getName());
		}
	}
	
	private final Vector<Pilot>	pilots	= new Vector<Pilot>();
	private final Vector<Pilot>	actives	= new Vector<Pilot>();
	
	public PilotsData() {
		loadDefault();
	}
	
	public void activateAll() {
		actives.clear();
		for (final Pilot p : pilots) {
			actives.add(p);
		}
	}
	
	public void add(final Pilot pilot) {
		add(pilot, true);
	}
	
	public void add(final Pilot pilot, final boolean active) {
		pilots.add(pilot);
		if (active) actives.add(pilot);
	}
	
	public void clear() {
		pilots.clear();
		actives.clear();
	}
	
	public int count() {
		return pilots.size();
	}
	
	public int countActive() {
		return actives.size();
	}
	
	public void deactivateAll() {
		actives.clear();
	}
	
	public void erase(final int i) {
		if ((i >= 0) && (i < pilots.size())) {
			final Pilot p = pilots.remove(i);
			actives.remove(p);
		}
	}
	
	public Pilot get(final int index) {
		return pilots.get(index);
	}
	
	public int indexOf(final Pilot p) {
		return pilots.indexOf(p);
	}
	
	public boolean isActive(final Pilot p) {
		return actives.contains(p);
	}
	
	public int laneIndexOf(final Pilot p) {
		return actives.indexOf(p);
	}
	
	public void load() {
		final File f = Storage.defaultFile(this);
		if (f.exists()) load(f);
	}
	
	public void load(final File f) {
		Logger.log("Loading " + f.getAbsolutePath());
		
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(f));
			
			String l;
			pilots.clear();
			actives.clear();
			while ((l = r.readLine()) != null) {
				try {
					if (l.length() == 0) continue;
					final boolean active = (l.charAt(0) == '*');
					if (active) l = l.substring(1);
					add(Pilot.fromString(l), active);
				}
				catch (final Exception e) {
					Logger.error(e);
				}
			}
		}
		catch (final FileNotFoundException e) {
			Logger.error(e);
		}
		catch (final IOException e) {
			Logger.error(e);
		}
		finally {
			try {
				if (r != null) r.close();
			}
			catch (final Exception e) {};
		}
	}
	
	public void loadDefault() {
		clear();
		add(new Pilot("Ayrton Senna", "Senna"));
		add(new Pilot("Bia Figueiredo", "Bia"));
		add(new Pilot("Emerson Fittipaldi", "Fittipaldi"));
		add(new Pilot("Felipe Massa", "Massa"));
		add(new Pilot("Lucas di Grassi", "DiGrassi"));
		add(new Pilot("Nelson Piquet", "Piquet"));
		add(new Pilot("Pedro Diniz", "Diniz"));
		add(new Pilot("Rubens Barrichello", "Rubinho"));
	}
	
	public void moveDown(final int i) {
		if ((i < 0) | ((i + 1) >= pilots.size())) return;
		pilots.insertElementAt(pilots.remove(i), i + 1);
		rebuildActivesOrder();
	}
	
	public void moveUp(final int i) {
		if ((i <= 0) | (i >= pilots.size())) return;
		pilots.insertElementAt(pilots.remove(i), i - 1);
		rebuildActivesOrder();
	}
	
	private void rebuildActivesOrder() {
		final Vector<Pilot> oldActives = new Vector<Pilot>(actives);
		actives.clear();
		for (final Pilot p : pilots) {
			if (oldActives.contains(p)) actives.add(p);
		}
	}
	
	public void rotate() {
		// convert to array (easier to change positions)
		final Pilot[] aActive = actives.toArray(new Pilot[actives.size()]);
		
		// rotate selected
		final int n = aActive.length;
		if (n < 2) return;
		final Pilot second = aActive[1];
		int i;
		
		// set index 1 = 3, 3 = 5, 5 = 7 ...
		for (i = 3; i < n; i += 2) {
			aActive[i - 2] = aActive[i];
		}
		
		// move back where stopped
		i -= 2;
		
		// now depending if number is odd or even ...
		if ((n % 2) == 0) {
			// set the last (stopped) to one previous (7 = 6)
			aActive[i] = aActive[i - 1];
			i--;
		}
		else {
			aActive[i] = aActive[i + 1];
			i++;
		}
		
		// now set 6 = 4, 4 = 2 ...
		for (; i >= 2; i -= 2) {
			aActive[i] = aActive[i - 2];
		}
		
		// now put old second into first
		aActive[0] = second;
		
		// remake list
		int pos = 0;
		actives.clear();
		for (final Pilot p : aActive) {
			actives.add(p);
			pilots.remove(p); // remove from where it is
			pilots.insertElementAt(p, pos++); // add in order
		}
	}
	
	public void save() {
		save(Storage.defaultFile(this));
	}
	
	public void save(final File file) {
		Logger.log("Saving Pilots Data to: " + file.getAbsolutePath());
		if (!Storage.makeDirectory(file.getParentFile())) return;
		
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new FileWriter(file, false));
			for (final Pilot p : pilots) {
				w.write(String.format("%s%s%n", (isActive(p) ? "*" : ""), p.toString()));
			}
		}
		catch (final Exception e) {
			Logger.error(e);
		}
		finally {
			try {
				if (w != null) w.close();
			}
			catch (final IOException e) {}
		}
	}
	
	public void setActive(final Pilot p, final boolean yn) {
		// is going to change?
		if (actives.contains(p) == yn) return;
		
		if (yn) {
			// add
			actives.add(p);
			rebuildActivesOrder();
		}
		else {
			// remove is easier
			actives.remove(p);
		}
	}
	
	public void shuffle() {
		Collections.shuffle(pilots);
		rebuildActivesOrder();
	}
	
	public void sort() {
		Collections.sort(pilots, new SortByNameComparator());
		rebuildActivesOrder();
	}
}
