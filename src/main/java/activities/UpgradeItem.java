package activities;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import client.Player;
import core.Log;
import core.Service;
import core.Util;
import io.Message;
import template.DataUpgrade;
import template.ItemTemplate7;
import template.Item_wear;
import template.UpgradeMaterialTemplate;

public class UpgradeItem {
	public static List<DataUpgrade> DATA = new ArrayList<>();
	static {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Util.loadfile("data/msg/login/request/msg-7_12"));
		      DataInputStream dis = new DataInputStream(bais)) {
			int n = dis.readByte();
			for (int i = 0; i < n; i++) {
				DataUpgrade temp = new DataUpgrade();
				temp.level = dis.readByte();
				temp.per = dis.readShort();
				temp.prelevel = dis.readByte();
				temp.beri = dis.readInt();
				temp.beri_white = dis.readInt();
				temp.ruby = dis.readShort();
				temp.att = dis.readShort();
				int n2 = dis.readByte();
				temp.material = new UpgradeMaterialTemplate[n2];
				for (int j = 0; j < n2; j++) {
					temp.material[j] = new UpgradeMaterialTemplate(dis.readByte(), dis.readByte(), dis.readShort());
				}
				UpgradeItem.DATA.add(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void show_table_upgrade(Player p) throws IOException {
		Message m = new Message(-48);
		m.writer().writeByte(7);
		p.conn.addmsg(m);
		m.cleanup();
	}

	public static void process(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		short id = m2.reader().readShort();
		byte bery_gem = m2.reader().readByte();
		// System.out.println(type);
		// System.out.println(id);
		// System.out.println(bery_gem);
		if (type == 8 && id == 0 && bery_gem == 0) {
			Service.Send_UI_Shop(p, 21);
		} else if (type == 10 && id == 0 && bery_gem == 0) {
			Kham_Ngoc.show_table(p, 0);
		} else if (type == 12 && id == 0 && bery_gem == 0) {
			Kham_Ngoc.show_table(p, 1);
		} else if (type == 9 && id == 0 && bery_gem == 0) {
			Kham_Ngoc.show_table(p, 2);
		} else if (type == 11 && id == 0 && bery_gem == 0) {
			Service.Send_UI_Shop(p, 111);
		} else if (bery_gem == 0) {
			switch (type) {
				case 4: { // bo do vao
					Item_wear it = p.item.bag3[id];
					if (it != null && it.levelup < 10) {
						Message m = new Message(-48);
						m.writer().writeByte(4);
						m.writer().writeShort(id);
						p.conn.addmsg(m);
						m.cleanup();
						//
						p.tool_upgrade = new short[] {-1, -1};
					}
					break;
				}
				case 1: {
					Item_wear it = p.item.bag3[id];
					if (it != null) {
						if (it.levelup > 9) {
							Service.send_box_ThongBao_OK(p, "Đã nâng cấp tối đa!");
							return;
						}
						Message m = new Message(-48);
						m.writer().writeByte(1);
						m.writer().writeUTF("Xác nhận muốn nâng cấp vật phẩm lên +" + (it.levelup + 1));
						m.writer().writeInt(UpgradeItem.DATA.get(it.levelup).beri);
						m.writer().writeShort(UpgradeItem.DATA.get(it.levelup).ruby);
						m.writer().writeShort(id);
						p.conn.addmsg(m);
						m.cleanup();
					}
					break;
				}
			}
		} else if (type == 2) {
			Item_wear it = p.item.bag3[id];
			if (it != null) {
				if (it.levelup > 9) {
					Service.send_box_ThongBao_OK(p, "Đã nâng cấp tối đa!");
					return;
				}
				short[] material_req = get_material(it.levelup, it.color);
				if (material_req[0] > -1) {
					if (p.item.total_item_bag_by_id(7, material_req[0]) < material_req[1]) {
						Service.send_box_ThongBao_OK(p, "Không đủ bột cường hóa");
						return;
					}
					if (p.item.total_item_bag_by_id(7, material_req[2]) < material_req[3]) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + ItemTemplate7.get_it_by_id(material_req[2]).name);
						return;
					}
				} else {
					Service.send_box_ThongBao_OK(p, "Đã có lỗi xảy ra");
					return;
				}
				if (bery_gem == 1) {
					if (p.get_vang() < UpgradeItem.DATA.get(it.levelup).beri) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + UpgradeItem.DATA.get(it.levelup).beri + " beri");
						return;
					}
					p.update_vang(-UpgradeItem.DATA.get(it.levelup).beri);
					p.update_money();
					Log.gI().add_log(p,
					      "Cường hóa item " + it.name + " -" + UpgradeItem.DATA.get(it.levelup).beri + " beri");
				} else {
					if (p.get_ngoc() < UpgradeItem.DATA.get(it.levelup).ruby) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + UpgradeItem.DATA.get(it.levelup).ruby + " ruby");
						return;
					}
					p.update_ngoc(-UpgradeItem.DATA.get(it.levelup).ruby);
					p.update_money();
					Log.gI().add_log(p,
					      "Cường hóa item " + it.name + " -" + UpgradeItem.DATA.get(it.levelup).ruby + " ruby");
				}
				p.item.remove_item47(7, material_req[0], material_req[1]);
				p.item.remove_item47(7, material_req[2], material_req[3]);
				boolean suc = UpgradeItem.DATA.get(it.levelup).per > Util.random(1200);
				if (suc) {
					it.levelup++;
					if (it.levelup == 10) {
						UpgradeItem.show_table_upgrade(p);
					}
					notice_upgrade(p, 2, "Thành công, hên đấy");
				} else {
					notice_upgrade(p, 3, "Đừng đập nữa không lên đâu!");
					if (p.tool_upgrade[1] == -1) {
						it.levelup = UpgradeItem.DATA.get(it.levelup).prelevel;
					}
				}
				//
				if (p.tool_upgrade[0] != -1) {
					p.item.remove_item47(7, p.tool_upgrade[0], 1);
				}
				if (p.tool_upgrade[1] != -1) {
					p.item.remove_item47(7, p.tool_upgrade[1], 1);
				}
				if (p.item.total_item_bag_by_id(7, p.tool_upgrade[0]) < 1) {
					p.tool_upgrade[0] = -1;
				}
				if (p.item.total_item_bag_by_id(7, p.tool_upgrade[1]) < 1) {
					p.tool_upgrade[1] = -1;
				}
				//
				p.item.update_Inventory(4, false);
				p.item.update_Inventory(7, false);
				p.item.update_Inventory(3, false);
			}
		} else if (bery_gem == 1) {
			if (type == 5) {
				if (p.item.total_item_bag_by_id(7, id) > 0) {
					Message m5 = new Message(-48);
					m5.writer().writeByte(5);
					m5.writer().writeByte(1); // is use
					m5.writer().writeShort(id);
					p.conn.addmsg(m5);
					m5.cleanup();
					p.tool_upgrade[0] = id;
				}
			} else if (type == 6) {
				if (p.item.total_item_bag_by_id(7, id) > 0) {
					Message m5 = new Message(-48);
					m5.writer().writeByte(6);
					m5.writer().writeByte(1); // is use
					m5.writer().writeShort(id);
					p.conn.addmsg(m5);
					m5.cleanup();
					p.tool_upgrade[1] = id;
				}
			}
		}
	}

	private static short[] get_material(int level, int color) {
		short[] result = new short[] {-1, -1, -1, -1};
		DataUpgrade temp = UpgradeItem.DATA.get(level);
		for (int i = 0; i < temp.material.length; i++) {
			if (temp.material[i].type == -1) {
				result[0] = temp.material[i].id;
				result[1] = temp.material[i].quant;
			}
			if (temp.material[i].type == color) {
				result[2] = temp.material[i].id;
				result[3] = temp.material[i].quant;
			}
		}
		return result;
	}

	private static void notice_upgrade(Player p, int type, String s) throws IOException {
		Message m = new Message(-48);
		m.writer().writeByte(type);
		m.writer().writeUTF(s);
		p.conn.addmsg(m);
		m.cleanup();
	}
}
