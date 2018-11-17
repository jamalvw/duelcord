package com.oopsjpeg.enigma.game.unit;

import com.oopsjpeg.enigma.game.util.Stats;
import com.oopsjpeg.enigma.game.util.Unit;

import java.awt.*;

public class Gunslinger implements Unit {
	public static final String NAME = "Gunslinger";
	public static final String DESC = "Every **4th** shot is a guaranteed crit and never misses."
			+ "\nHowever, attacks before the **4th** shot cannot crit."
			+ "\nAdditionally, crit chance from items and effects increase damage.";
	public static final Color COLOR = new Color(255, 88, 0);
	public static final boolean RANGED = true;
	public static final Stats STATS = new Stats()
			.put(Stats.ENERGY, 125)
			.put(Stats.MAX_HP, 545)
			.put(Stats.DAMAGE, 22)
			.put(Stats.ACCURACY, 0.6f);
	public static final Stats PER_TURN = new Stats()
			.put(Stats.HP, 12)
			.put(Stats.GOLD, 75);

	private int shot = 0;

	public int getShot() {
		return shot;
	}

	public void setShot(int shot) {
		this.shot = Math.max(0, Math.min(4, shot));
	}

	public int shot() {
		setShot(shot + 1);
		return shot;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDesc() {
		return DESC;
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

	@Override
	public boolean isRanged() {
		return RANGED;
	}

	@Override
	public Stats getStats() {
		return STATS;
	}

	@Override
	public Stats getPerTurn() {
		return PER_TURN;
	}
}
