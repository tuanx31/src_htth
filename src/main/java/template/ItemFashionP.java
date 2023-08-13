package template;

import java.io.IOException;
import client.Player;
import io.Message;

public class ItemFashionP {
	public byte category;
	public short id;
	public short icon;
	public boolean is_use;

	public static void show_table(Player p, int type) throws IOException {
		switch (type) {
			case 103: {
				Message m = new Message(-19);
				m.writer().writeByte(103);
				m.writer().writeUTF("Tiệm tóc");
				m.writer().writeByte(103);
				m.writer().writeShort(ItemHair.get_size_type(103));
				for (int i = 0; i < ItemHair.ENTRYS.size(); i++) {
					ItemHair temp = ItemHair.ENTRYS.get(i);
					if (temp.type == 103) {
						m.writer().writeByte(temp.ID);
						m.writer().writeUTF(temp.name);
						m.writer().writeByte(0);
						m.writer().writeShort(temp.idIcon);
						m.writer().writeShort(0);
						m.writer().writeInt(0);
						if (p.check_itfashionP(temp.ID, 103) != null) {
							m.writer().writeShort(0);
						} else {
							m.writer().writeShort(500);
						}
					}
				}
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
			case 105: {
				Message m = new Message(-19);
				m.writer().writeByte(105);
				m.writer().writeUTF("Thời trang");
				m.writer().writeByte(105);
				m.writer().writeShort(ItemFashion.ENTRYS.size());
				for (int i = 0; i < ItemFashion.ENTRYS.size(); i++) {
					m.writer().writeByte(ItemFashion.ENTRYS.get(i).ID);
					m.writer().writeUTF(ItemFashion.ENTRYS.get(i).name);
					m.writer().writeUTF(ItemFashion.ENTRYS.get(i).info);
					m.writer().writeShort(ItemFashion.ENTRYS.get(i).idIcon);
					m.writer().writeByte(ItemFashion.ENTRYS.get(i).mWearing.length);
					for (int j = 0; j < ItemFashion.ENTRYS.get(i).mWearing.length; j++) {
						m.writer().writeShort(ItemFashion.ENTRYS.get(i).mWearing[j]);
					}
					if (!ItemFashion.ENTRYS.get(i).is_sell) {
						m.writer().writeInt(-1);
						m.writer().writeShort(0);
					} else {
						m.writer().writeInt(0);
						if (p.check_fashion(ItemFashion.ENTRYS.get(i).ID) != null) {
							m.writer().writeShort(0);
						} else {
							m.writer().writeShort(ItemFashion.ENTRYS.get(i).price);
						}
					}
					if (p.conn.version.equals("1.2.3")) {
						m.writer().writeByte(0);
					}
				}
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
			case 108: {
				Message m = new Message(-19);
				m.writer().writeByte(112);
				m.writer().writeUTF("Thẩm mỹ viện");
				m.writer().writeByte(108);
				m.writer().writeShort(ItemHair.get_size_type(108));
				for (int i = 0; i < ItemHair.ENTRYS.size(); i++) {
					ItemHair temp = ItemHair.ENTRYS.get(i);
					if (temp.type == 108) {
						m.writer().writeByte(temp.ID);
						m.writer().writeUTF(temp.name);
						m.writer().writeByte(0);
						m.writer().writeShort(temp.idIcon);
						m.writer().writeShort(0);
						m.writer().writeInt(0);
						if (p.check_itfashionP(temp.ID, 108) != null) {
							m.writer().writeShort(0);
						} else {
							m.writer().writeShort(500);
						}
					}
				}
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
		}
	}
}
