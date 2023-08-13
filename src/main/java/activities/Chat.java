package activities;

import java.io.IOException;
import client.Player;
import core.Service;
import io.Message;
import map.Map;

public class Chat {
	public static void process(Player p, Message m2, int type) throws IOException {
		// System.out.println("type " + type);
		if (type == 0) { // chat tab private
			String tab_name = m2.reader().readUTF();
			String text = m2.reader().readUTF();
			System.out.println(tab_name);
			switch (tab_name) {
				case "Nhóm": {
					if (p.party != null) {
						for (int i = 0; i < p.party.list.size(); i++) {
							Player p0 = p.party.list.get(i);
							send_chat(p0, tab_name, "@" + p.name + " : " + text);
						}
					} else {
						Service.send_box_ThongBao_OK(p, "Nhóm không tồn tại");
					}
					break;
				}
				default: {
					Player p0 = Map.get_player_by_name_allmap(tab_name);
					if (p0 != null) {
						send_chat(p0, p.name, text);
					} else {
						Service.send_box_ThongBao_OK(p, "Đối phương offline");
					}
					break;
				}
			}
		}
	}

	private static void send_chat(Player p, String tab_name, String text) throws IOException {
		Message m = new Message(18);
		m.writer().writeUTF(tab_name);
		m.writer().writeUTF(text);
		p.conn.addmsg(m);
		m.cleanup();
	}
}
