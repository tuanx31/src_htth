package activities;

import java.io.IOException;
import client.Player;
import core.Service;
import core.Util;
import io.Message;
import template.ItemTemplate7;
import template.Skill_info;

public class UpgradeDevil {
	public static void show_table(Player p, int index) throws IOException {
		switch (index) {
			case 0: {
				Message m = new Message(45);
				m.writer().writeByte(13);
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
			case 1: {
				Message m = new Message(45);
				m.writer().writeByte(8);
				p.conn.addmsg(m);
				m.cleanup();
				break;
			}
		}
	}

	public static void process(Player p, Message m2) throws IOException {
		byte act = m2.reader().readByte();
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		short num = m2.reader().readShort();
//		System.out.println("act " + act);
//		System.out.println("id " + id);
//		System.out.println("cat " + cat);
//		System.out.println("num " + num);
		if (act == 9 && cat == 104 && num == 0) { // bo skill vao
			Skill_info sk_temp = p.get_skill_temp(id);
			if (sk_temp != null && sk_temp.lvdevil < 1) {
				if (p.item.total_item_bag_by_id(7, 9) > 9) {
					Message m = new Message(45);
					m.writer().writeByte(9);
					m.writer().writeByte(0);
					m.writer().writeShort(id);
					m.writer().writeByte(104);
					m.writer().writeShort(1);
					p.conn.addmsg(m);
					m.cleanup();
					//
					m = new Message(45);
					m.writer().writeByte(9);
					m.writer().writeByte(1);
					m.writer().writeShort(9);
					m.writer().writeByte(7);
					m.writer().writeShort(10);
					p.conn.addmsg(m);
					m.cleanup();
				} else {
					Message m = new Message(45);
					m.writer().writeByte(11);
					m.writer().writeUTF("Bạn không có đủ " + ItemTemplate7.get_it_by_id(9).name);
					p.conn.addmsg(m);
					m.cleanup();
				}
			}
		} else if (act == 12 && cat == 104 && num == 0) { // bat dau cuong hoa skill
			Skill_info sk_temp = p.get_skill_temp(id);
			if (sk_temp != null) {
				if (p.get_vang() < 50_000) {
					Service.send_box_ThongBao_OK(p, "Không đủ 50k beri");
					return;
				}
				if (p.item.total_item_bag_by_id(7, 9) > 9) {
					p.item.remove_item47(7, 9, 10);
					p.update_vang(-50_000);
					p.update_money();
					boolean suc = 50 > Util.random(120);
					Message m = new Message(45);
					m.writer().writeByte(12);
					m.writer().writeByte(suc ? 1 : 3);
					m.writer().writeUTF(suc ? "Thành công" : "Rất tiếc!");
					p.conn.addmsg(m);
					m.cleanup();
					if (suc) {
						sk_temp.devilpercent += 10;
						if (sk_temp.devilpercent >= 100) {
							sk_temp.devilpercent = 0;
							sk_temp.lvdevil++;
						}
						p.send_skill();
					}
					//
					p.item.update_Inventory(4, false);
					p.item.update_Inventory(7, false);
					p.item.update_Inventory(3, false);
					UpgradeDevil.show_table(p, 1);
					Service.send_box_ThongBao_OK(p, suc ? "Thành công" : "Rất tiếc!");
				} else {
					Message m = new Message(45);
					m.writer().writeByte(11);
					m.writer().writeUTF("Bạn không có đủ " + ItemTemplate7.get_it_by_id(9).name);
					p.conn.addmsg(m);
					m.cleanup();
				}
			}
		}
	}
}
