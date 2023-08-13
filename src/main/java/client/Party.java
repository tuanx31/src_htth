package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import core.Service;
import io.Message;

public class Party {
	public List<Player> list = new ArrayList<>();

	public Party(Player p) {
		this.list.add(p);
	}

	public static void process(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		short id = -1;
		if (type == 0 || type == 2 || type == 4 || type == 6) {
			id = m2.reader().readShort();
		}
		//System.out.println("type " + type);
		//System.out.println("id " + id);
		switch (type) {
			case 0: { // request
				Player p0 = p.map.get_player_by_id_inmap(id);
				if (p0 != null) {
					if (p0.party != null) {
						Service.send_box_ThongBao_OK(p, "Đối phương đang ở trong nhóm khác!");
					} else {
						Message m = new Message(-25);
						m.writer().writeByte(0);
						m.writer().writeShort(p.id);
						m.writer().writeUTF(p.name);
						p0.conn.addmsg(m);
						m.cleanup();
						//
						if (p.party == null) {
							p.party = new Party(p);
							p.party.send_info();
						}
					}
				} else {
					Service.send_box_ThongBao_OK(p, "Đối phương offline");
				}
				break;
			}
			case 4: { // accept
				Player p0 = p.map.get_player_by_id_inmap(id);
				if (p0 != null) {
					if (p0.party != null) {
						p0.party.add_new_mem(p);
					} else {
						Service.send_box_ThongBao_OK(p, "Đối phương đã hủy nhóm");
					}
				} else {
					Service.send_box_ThongBao_OK(p, "Đối phương offline");
				}
				break;
			}
			case 3: { // delete
				if (p.party != null && p.party.list.get(0).equals(p)) {
					p.party.delete();
				}
				break;
			}
			case 2: { // leave
				if (p.party != null) {
					Player p0 = p.map.get_player_by_id_inmap(id);
					if (p0 != null) {
						p.party.remove_mem(p0);
						if (p0.id == p.id) {
							Service.send_box_ThongBao_OK(p0, "Bạn rời khỏi nhóm");
						} else {
							Service.send_box_ThongBao_OK(p0, "Bạn bị đuổi khỏi nhóm");
						}
					}
				}
				break;
			}
		}
	}

	public synchronized void remove_mem(Player p) throws IOException {
		for (int i = 0; i < list.size(); i++) {
			Player p0 = list.get(i);
			if (p0.equals(p)) {
				Message m = new Message(-25);
				m.writer().writeByte(3);
				p0.conn.addmsg(m);
				m.cleanup();
				p0.party = null;
				list.remove(p);
				this.send_info();
				break;
			}
		}
	}

	private synchronized void delete() throws IOException {
		Message m = new Message(-25);
		m.writer().writeByte(3);
		for (int i = 0; i < list.size(); i++) {
			Player p0 = list.get(i);
			p0.party = null;
			p0.conn.addmsg(m);
			Service.send_box_ThongBao_OK(p0, "Nhóm đã giải tán");
		}
		m.cleanup();
		this.list.clear();
	}

	private synchronized void add_new_mem(Player p) throws IOException {
		if (this.list.size() < 4) {
			list.add(p);
			p.party = this;
			this.send_info();
			Service.send_box_ThongBao_OK(p, "Vào nhóm thành công!");
		} else {
			Service.send_box_ThongBao_OK(list.get(0), "Nhóm đầy!");
		}
	}

	public void send_info() throws IOException {
		Message m = new Message(-25);
		m.writer().writeByte(5);
		m.writer().writeByte(list.size());
		for (int i = 0; i < list.size(); i++) {
			Player p0 = list.get(i);
			m.writer().writeShort(p0.id);
			m.writer().writeUTF(p0.name);
			m.writer().writeShort(p0.map.id);
			m.writer().writeByte(i == 0 ? 1 : 0);
			m.writer().writeByte(p0.map.zone_id);
		}
		for (int i = 0; i < list.size(); i++) {
			Player p0 = list.get(i);
			p0.conn.addmsg(m);
		}
		m.cleanup();
	}
}
