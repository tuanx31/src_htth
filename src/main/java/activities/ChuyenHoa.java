package activities;

import java.io.IOException;
import client.Player;
import core.Log;
import core.Service;
import core.Util;
import io.Message;
import template.Item_wear;

public class ChuyenHoa {
	public static void show_table(Player p) throws IOException {
		Message m = new Message(-77);
		m.writer().writeByte(0);
		p.conn.addmsg(m);
		m.cleanup();
		p.item_chuyenhoa_save_0 = null;
		p.item_chuyenhoa_save_1 = null;
	}

	public static void process(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		short idLeft = m2.reader().readShort();
		short idRight = -1;
		if (type == 2 || type == 3) {
			idRight = m2.reader().readShort();
		}
		// System.out.println(type);
		// System.out.println(idLeft);
		// System.out.println(idRight);
		switch (type) {
			case 1: {
				if (idRight == -1 && (p.item_chuyenhoa_save_0 == null || p.item_chuyenhoa_save_1 == null)) {
					Item_wear it_get = p.item.bag3[idLeft];
					if (it_get != null) {
						Message m = new Message(-77);
						m.writer().writeByte(1);
						if (p.item_chuyenhoa_save_0 == null) {
							p.item_chuyenhoa_save_0 = it_get;
							m.writer().writeByte(0);
						} else {
							p.item_chuyenhoa_save_1 = it_get;
							m.writer().writeByte(1);
						}
						m.writer().writeShort(idLeft);
						p.conn.addmsg(m);
						m.cleanup();
					}
				}
				break;
			}
			case 2: {
				if (p.item_chuyenhoa_save_0 != null && p.item_chuyenhoa_save_1 != null) {
					if (p.item_chuyenhoa_save_0.level > p.item_chuyenhoa_save_1.level) {
						ChuyenHoa.show_table(p);
						Service.send_box_ThongBao_OK(p, "Không thể chuyển hóa cho vật phẩm level thấp hơn!");
						return;
					}
					if (p.item_chuyenhoa_save_0.levelup == 0 || p.item_chuyenhoa_save_1.levelup > 0
					      || p.item_chuyenhoa_save_0.typeEquip != p.item_chuyenhoa_save_1.typeEquip
					      || (p.item_chuyenhoa_save_0.typeEquip != 0 && p.item_chuyenhoa_save_0.typeEquip != 3
					            && p.item_chuyenhoa_save_0.typeEquip != 5)) {
						ChuyenHoa.show_table(p);
						Service.send_box_ThongBao_OK(p, "Không thể chuyển hóa!");
						return;
					}
					int vang_req = 10 * p.item_chuyenhoa_save_0.levelup;
					if (p.get_ngoc() < vang_req) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby!");
						return;
					}
					int random = Util.random(100);
					p.item_chuyenhoa_save_1.levelup = p.item_chuyenhoa_save_0.levelup;
					p.item_chuyenhoa_save_0.levelup = 0;
					if (random < 25) {
						p.item_chuyenhoa_save_1.levelup -= 1;
					} else if (random < 50) {
						p.item_chuyenhoa_save_1.levelup -= 2;
					} else if (random < 85) {
						p.item_chuyenhoa_save_1.levelup -= 3;
					}
					if (p.item_chuyenhoa_save_1.levelup < 0) {
						p.item_chuyenhoa_save_1.levelup = 0;
					}
					p.update_ngoc(-vang_req);
					p.update_money();
					Log.gI().add_log(p, "Chuyển hóa " + p.item_chuyenhoa_save_0.name + " sang "
					      + p.item_chuyenhoa_save_1.name + " -" + vang_req + " ruby");
					p.item.update_Inventory(4, false);
					p.item.update_Inventory(7, false);
					p.item.update_Inventory(3, false);
					ChuyenHoa.show_table(p);
					Service.send_box_ThongBao_OK(p, "chuyển hóa thành công!");
				}
				break;
			}
		}
	}
}
