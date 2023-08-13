package io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import activities.Trade;
import client.Player;
import core.Log;
import core.Util;

public class SessionManager {
	public static final List<Session> CLIENT_ENTRYS = new LinkedList<>();
	public static HashMap<String, Long> time_login = new HashMap<>();

	public synchronized static void client_connect(Session ss) {
		ss.init();
		SessionManager.CLIENT_ENTRYS.add(ss);
		System.out.println("accecpt ip " + ss.ip + " - online : " + SessionManager.CLIENT_ENTRYS.size());
	}

	public synchronized static void client_disconnect(Session ss) {
		if (SessionManager.CLIENT_ENTRYS.contains(ss)) {
			ss.connected = false;
			SessionManager.time_login.put(ss.user, System.currentTimeMillis() + 1_000_000_000L);
			if (ss.p != null && ss.p.map != null) {
				ss.p.map.leave_map(ss.p);
				try {
					if (ss.p.trade_target != null) {
						Trade.end_trade_by_disconnect(ss.p.trade_target, ss.p, false);
					}
					if (ss.p.party != null) {
						ss.p.party.remove_mem(ss.p);
					}
					Player.flush(ss.p, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.gI().add_log(ss.p, "Logout : [Beri] " + Util.number_format(ss.p.get_vang()) + " [Ruby] "
				      + Util.number_format(ss.p.get_ngoc()));
			}
			//
			ss.clear_network(ss);
			SessionManager.time_login.put(ss.user, System.currentTimeMillis() + 5_000L);
			ss.update_onl(0);
			//
			SessionManager.CLIENT_ENTRYS.remove(ss);
			System.out.println("disconnect session " + ss.user + " - online : " + SessionManager.CLIENT_ENTRYS.size());
		}
	}
}
