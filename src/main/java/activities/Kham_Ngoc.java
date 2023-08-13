package activities;

import java.io.IOException;
import client.Player;
import core.Log;
import core.Service;
import core.Util;
import io.Message;
import template.ItemBag47;
import template.ItemTemplate4;
import template.Item_wear;
import template.Option;

public class Kham_Ngoc {
	public static short[] ID_SELL = new short[] {74, 68, 62, 56, 50, 44, 324, 325, 326, 323, 339};
	public static int[] ID_SELL_PRICE = new int[] {2, 2, 2, 2, 2, 2, 1_000_000, 1_000_000, 1_000_000, 500, 500};

	public static void show_table(Player p, int type) throws IOException {
		Message m = new Message(-67);
		m.writer().writeByte(0);
		switch (type) {
			case 0: {
				m.writer().writeByte(2);
				break;
			}
			case 1: {
				m.writer().writeByte(4);
				break;
			}
			case 2: {
				m.writer().writeByte(1);
				break;
			}
			case 3: {
				m.writer().writeByte(3);
				break;
			}
		}
		p.conn.addmsg(m);
		m.cleanup();
		p.item_to_kham_ngoc = null;
		p.item_to_kham_ngoc_id_ngoc = -1;
	}

	public static void process(Player p, Message m2) throws IOException {
		byte type = m2.reader().readByte();
		byte action = m2.reader().readByte();
		short idItem = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		short num = m2.reader().readShort();
		m2.reader().readShort();
		// System.out.println(type);
		// System.out.println(action);
		// System.out.println(idItem);
		// System.out.println(cat);
		// System.out.println(num);
		if (cat == 3 && num == 1 && type == 2 && action == 1) { // bo item duc lo
			Item_wear it_select = p.item.bag3[idItem];
			if (it_select != null) {
				Message m = new Message(-67);
				m.writer().writeByte(1);
				m.writer().writeShort(idItem);
				m.writer().writeByte(3);
				m.writer().writeShort(1);
				p.conn.addmsg(m);
				m.cleanup();
			}
		} else if (cat == 3 && num == 1 && type == 2 && action == 7) { // duc lo
			Item_wear it_select = p.item.bag3[idItem];
			if (it_select != null && it_select.numLoKham < 7) {
				if (it_select.valueChetac <= 0) {
					// Service.send_box_ThongBao_OK(p, "Vật phẩm không đủ phẩm chất để thực hiện!");
					// return;
				}
				int vang_req = 50 * (it_select.numLoKham + 1);
				if (p.get_ngoc() < vang_req) {
					Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby!");
					return;
				}
				boolean reduce_chetac = true;
				if (p.item.total_item_bag_by_id(4, 323) > 0) {
					p.item.remove_item47(4, 323, 1);
					reduce_chetac = false;
				} else if (p.item.total_item_bag_by_id(4, 339) > 0) {
					p.item.remove_item47(4, 339, 1);
				} else {
					Service.send_box_ThongBao_OK(p, "Không đủ búa đục trong hành trang!");
					return;
				}
				p.update_ngoc(-vang_req);
				p.update_money();
				Log.gI().add_log(p, "Đục lỗ " + it_select.name + " -" + vang_req + " ruby");
				boolean suc = 40 > Util.random(100 + it_select.numLoKham * 100);
				if (suc) {
					it_select.numLoKham += 1;
				} else {
					if (reduce_chetac) {
						it_select.valueChetac -= Util.random((it_select.numLoKham + 1) * 2);
						if (it_select.valueChetac < 0) {
							it_select.valueChetac = 0;
						}
					}
				}
				Kham_Ngoc.show_table(p, 0);
				p.item.update_Inventory(4, false);
				p.item.update_Inventory(7, false);
				p.item.update_Inventory(3, false);
				Kham_Ngoc.show_table(p, 0);
				if (suc) {
					Service.send_box_ThongBao_OK(p, "Đục lỗ thành công!");
				} else {
					Service.send_box_ThongBao_OK(p, "Đục lỗ thất bại!");
				}
			} else {
				Kham_Ngoc.show_table(p, 0);
				Service.send_box_ThongBao_OK(p, "Đục lỗ thất bại!");
			}
		} else if (cat == 4 && num > 0 && type == 4 && action == 1) { // bo da kham vao de hop
			if (p.item.total_item_bag_by_id(4, idItem) < num) {
				Service.send_box_ThongBao_OK(p, "Không đủ vật phẩm trong hành trang!");
				return;
			}
			Message m = new Message(-67);
			m.writer().writeByte(1);
			m.writer().writeShort(idItem);
			m.writer().writeByte(4);
			m.writer().writeShort(num);
			p.conn.addmsg(m);
			m.cleanup();
		} else if (cat == 4 && num > 0 && type == 4 && action == 5) { // hop da kham
			if (idItem >= 44 && idItem <= 78 && idItem != 73 && idItem != 67 && idItem != 61 && idItem != 55
			      && idItem != 49) {
				if (p.item.total_item_bag_by_id(4, idItem) < num) {
					Service.send_box_ThongBao_OK(p, "Không đủ vật phẩm trong hành trang!");
					return;
				}
				int vang_req = 50;
				if (p.get_ngoc() < vang_req) {
					Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby!");
					return;
				}
				int it_quan_can_process = (num - (num % 3)) / 3;
				//
				ItemBag47 it = new ItemBag47();
				it.id = (short) (idItem + 1);
				it.category = 4;
				it.quant = (short) it_quan_can_process;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.update_ngoc(-vang_req);
					p.update_money();
					Log.gI().add_log(p,
					      "Hợp đá khảm -> " + ItemTemplate4.get_it_by_id(it.id).name + " -" + vang_req + " ruby");
					//
					p.item.remove_item47(4, idItem, (it_quan_can_process * 3));
					p.item.add_item_bag47(4, it);
					p.item.update_Inventory(4, false);
					p.item.update_Inventory(7, false);
					p.item.update_Inventory(3, false);
					Kham_Ngoc.show_table(p, 1);
					Service.send_box_ThongBao_OK(p,
					      "Nhận được " + it_quan_can_process + " " + ItemTemplate4.get_item_name(it.id));
				} else {
					Kham_Ngoc.show_table(p, 1);
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
			} else {
				Kham_Ngoc.show_table(p, 1);
				Service.send_box_ThongBao_OK(p, "Vật phẩm không hợp lệ!");
			}
		} else if (cat == 3 && num == 1 && type == 1 && action == 1) { // bo item kham ngoc vao
			Item_wear it_select = p.item.bag3[idItem];
			if (it_select != null) {
				Message m = new Message(-67);
				m.writer().writeByte(1);
				m.writer().writeShort(idItem);
				m.writer().writeByte(3);
				m.writer().writeShort(1);
				p.conn.addmsg(m);
				m.cleanup();
				p.item_to_kham_ngoc = it_select;
			}
		} else if (cat == 4 && num == 1 && type == 1 && action == 1) { // bo ngoc kham vao
			if (p.item.total_item_bag_by_id(4, idItem) < num) {
				Service.send_box_ThongBao_OK(p, "Không đủ vật phẩm trong hành trang!");
				return;
			}
			Message m = new Message(-67);
			m.writer().writeByte(1);
			m.writer().writeShort(idItem);
			m.writer().writeByte(4);
			m.writer().writeShort(1);
			p.conn.addmsg(m);
			m.cleanup();
			p.item_to_kham_ngoc_id_ngoc = idItem;
		} else if (cat == 0 && num == 0 && type == 1 && action == 4) { // bat dau kham ngoc len item
			Item_wear it_select = p.item_to_kham_ngoc;
			if (it_select != null && p.item_to_kham_ngoc_id_ngoc != -1 && it_select.numLoKham > it_select.mdakham.length) {
				if (!check_can_kham_len_item(it_select, p.item_to_kham_ngoc_id_ngoc)) {
					Kham_Ngoc.show_table(p, 2);
					Service.send_box_ThongBao_OK(p, "Vật phẩm k hợp lệ!");
					return;
				}
				boolean suc = true;
				// 20 > Util.random(120 + 10 * it_select.numLoKham);
				int vang_req = 50 * (it_select.numLoKham + 1);
				if (p.get_ngoc() < vang_req) {
					Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby!");
					return;
				}
				p.update_ngoc(-vang_req);
				p.update_money();
				Log.gI().add_log(p, "Khảm ngọc lên -> " + it_select.name + " -" + vang_req + " ruby");
				p.item.remove_item47(4, p.item_to_kham_ngoc_id_ngoc, 1);
				if (suc) {
					add_op_ngoc_kham_new(it_select, p.item_to_kham_ngoc_id_ngoc);
					Kham_Ngoc.show_table(p, 2);
					Service.send_box_ThongBao_OK(p, "Khảm thành công!");
				} else {
					Kham_Ngoc.show_table(p, 2);
					Service.send_box_ThongBao_OK(p, "Khảm thất bại!");
				}
				p.item.update_Inventory(4, false);
				p.item.update_Inventory(7, false);
				p.item.update_Inventory(3, false);
			} else {
				Kham_Ngoc.show_table(p, 2);
				Service.send_box_ThongBao_OK(p, "Có lỗi xảy ra, hãy thử lại!");
			}
		} else if (cat == 3 && num == 1 && type == 3 && action == 1) { // bo item thao ngoc kham
			Item_wear it_select = p.item.bag3[idItem];
			if (it_select != null) {
				if (it_select.mdakham.length > 0) {
					Message m = new Message(-67);
					m.writer().writeByte(1);
					m.writer().writeShort(idItem);
					m.writer().writeByte(3);
					m.writer().writeShort(1);
					p.conn.addmsg(m);
					m.cleanup();
					p.item_to_kham_ngoc = it_select;
				} else {
					Service.send_box_ThongBao_OK(p, "Vật phẩm chưa có đá khảm!");
				}
			}
		} else if (cat == 3 && num == 1 && type == 3 && action == 6) { // bat dau thao ngoc kham
			if (p.item_to_kham_ngoc != null) {
				int vang_req = p.item_to_kham_ngoc.mdakham.length * 250;
				Service.send_box_yesno(p, 1, "Xác nhận tháo tất cả ngọc khảm với giá " + vang_req + " ruby?");
			}
		}
		// System.out.println(type);
		// System.out.println(action);
		// System.out.println(idItem);
		// System.out.println(cat);
		// System.out.println(num);
	}

	private static void add_op_ngoc_kham_new(Item_wear it_select, short id) {
		if (it_select.mdakham.length > 0) {
			short[] temp = new short[it_select.mdakham.length + 1];
			for (int i = 0; i < it_select.mdakham.length; i++) {
				temp[i] = it_select.mdakham[i];
			}
			temp[temp.length - 1] = id;
			it_select.mdakham = temp;
		} else {
			it_select.mdakham = new short[] {id};
		}
		byte[] id_add = null;
		int[] par_add = null;
		switch (id) {
			case 44: {
				id_add = new byte[] {4};
				par_add = new int[] {20};
				break;
			}
			case 45: {
				id_add = new byte[] {4};
				par_add = new int[] {30};
				break;
			}
			case 46: {
				id_add = new byte[] {4};
				par_add = new int[] {40};
				break;
			}
			case 47: {
				id_add = new byte[] {4};
				par_add = new int[] {60};
				break;
			}
			case 48: {
				id_add = new byte[] {4};
				par_add = new int[] {90};
				break;
			}
			case 49: {
				id_add = new byte[] {4};
				par_add = new int[] {140};
				break;
			}
			case 50: {
				id_add = new byte[] {1};
				par_add = new int[] {50};
				break;
			}
			case 51: {
				id_add = new byte[] {1};
				par_add = new int[] {70};
				break;
			}
			case 52: {
				id_add = new byte[] {1};
				par_add = new int[] {90};
				break;
			}
			case 53: {
				id_add = new byte[] {1};
				par_add = new int[] {120};
				break;
			}
			case 54: {
				id_add = new byte[] {1};
				par_add = new int[] {160};
				break;
			}
			case 55: {
				id_add = new byte[] {1};
				par_add = new int[] {220};
				break;
			}
			case 56: {
				id_add = new byte[] {10};
				par_add = new int[] {10};
				break;
			}
			case 57: {
				id_add = new byte[] {10};
				par_add = new int[] {20};
				break;
			}
			case 58: {
				id_add = new byte[] {10};
				par_add = new int[] {30};
				break;
			}
			case 59: {
				id_add = new byte[] {10};
				par_add = new int[] {40};
				break;
			}
			case 60: {
				id_add = new byte[] {10};
				par_add = new int[] {50};
				break;
			}
			case 61: {
				id_add = new byte[] {10};
				par_add = new int[] {60};
				break;
			}
			case 62: {
				id_add = new byte[] {13};
				par_add = new int[] {10};
				break;
			}
			case 63: {
				id_add = new byte[] {13};
				par_add = new int[] {20};
				break;
			}
			case 64: {
				id_add = new byte[] {13};
				par_add = new int[] {30};
				break;
			}
			case 65: {
				id_add = new byte[] {13};
				par_add = new int[] {40};
				break;
			}
			case 66: {
				id_add = new byte[] {13};
				par_add = new int[] {60};
				break;
			}
			case 67: {
				id_add = new byte[] {13};
				par_add = new int[] {90};
				break;
			}
			case 68: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {10, 10};
				break;
			}
			case 69: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {20, 20};
				break;
			}
			case 70: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {30, 30};
				break;
			}
			case 71: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {40, 40};
				break;
			}
			case 72: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {60, 60};
				break;
			}
			case 73: {
				id_add = new byte[] {26, 27};
				par_add = new int[] {90, 90};
				break;
			}
			case 74: {
				id_add = new byte[] {14};
				par_add = new int[] {10};
				break;
			}
			case 75: {
				id_add = new byte[] {14};
				par_add = new int[] {20};
				break;
			}
			case 76: {
				id_add = new byte[] {14};
				par_add = new int[] {30};
				break;
			}
			case 77: {
				id_add = new byte[] {14};
				par_add = new int[] {40};
				break;
			}
			case 78: {
				id_add = new byte[] {14};
				par_add = new int[] {60};
				break;
			}
			case 79: {
				id_add = new byte[] {14};
				par_add = new int[] {90};
				break;
			}
			case 324: {
				id_add = new byte[] {1, 4, 10, 13, 14, 26, 27};
				par_add = new int[] {70, 110, 30, 50, 50, 50, 50};
				break;
			}
			case 325: {
				id_add = new byte[] {49, 50, 52, 51, 47, 48};
				par_add = new int[] {40, 40, 40, 40, 40, 20};
				break;
			}
			case 326: {
				id_add = new byte[] {1, 4, 10, 13, 14, 26, 27};
				par_add = new int[] {140, 220, 60, 90, 90, 90, 90};
				break;
			}
		}
		if (id_add != null && par_add != null) {
			for (int i = 0; i < id_add.length; i++) {
				Option op_new = null;
				for (int j = 0; j < it_select.option_item_2.size(); j++) {
					if (it_select.option_item_2.get(j).id == id_add[i]) {
						op_new = it_select.option_item_2.get(j);
						break;
					}
				}
				if (op_new != null) {
					int par_old = op_new.getParam(0);
					op_new.setParam(par_old + par_add[i]);
				} else {
					op_new = new Option(id_add[i], par_add[i]);
					it_select.option_item_2.add(op_new);
				}
			}
		}
	}

	private static boolean check_can_kham_len_item(Item_wear it_select, short id) {
		if (id >= 324 && id <= 326) {
			return true;
		}
		boolean result = false;
		switch (it_select.typeEquip) {
			case 0: {
				if (id >= 50 && id <= 55) {
					result = true;
				}
				break;
			}
			case 3:
			case 5: {
				if (id >= 68 && id <= 73 || id >= 44 && id <= 49) {
					result = true;
				}
				break;
			}
		}
		return result;
	}
}
