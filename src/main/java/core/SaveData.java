package core;

import client.Player;
import map.Map;

public class SaveData {
	public static void process() {
		long t = System.currentTimeMillis();
		for (Map[] mapall : Map.ENTRYS) {
			for (Map map : mapall) {
				for (int i = 0; i < map.players.size(); i++) {
					Player p0 = map.players.get(i);
					Player.flush(p0, false);
				}
			}
		}
		System.out.println("[" + Util.get_now_by_time() + "] SAVE DATA OK " + (System.currentTimeMillis() - t));
	}
}
