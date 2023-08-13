package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import core.Service;
import core.Util;
import io.Message;
import template.EffTemplate;
import template.ItemBag47;
import template.ItemFashionP;
import template.ItemFashionP2;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.Item_wear;

public class UseItem {
	public static void process(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		// System.out.println(id);
		// System.out.println(cat);
		switch (cat) {
			case 3: {
				use_item_3(p, id);
				break;
			}
			case 4: {
				// use_item_4(p, id, true);
				break;
			}
			case 7: {
				use_item_7(p, id);
				break;
			}
			case 105: {
				ItemFashionP2 temp = p.check_fashion(id);
				if (temp != null) {
					p.update_fashionP2(temp);
					for (int i = 0; i < p.map.players.size(); i++) {
						Player p0 = p.map.players.get(i);
						Service.charWearing(p, p0, false);
					}
					Service.send_box_ThongBao_OK(p, "Mặc thành công!");
				} else {
					Service.send_box_ThongBao_OK(p, "Chưa mua vật phẩm này!");
				}
				break;
			}
			case 108:
			case 103: {
				ItemFashionP temp = p.check_itfashionP(id, cat);
				if (temp != null) {
					p.update_itfashionP(temp, cat);
					for (int i = 0; i < p.map.players.size(); i++) {
						Player p0 = p.map.players.get(i);
						Service.charWearing(p, p0, false);
					}
					Service.send_box_ThongBao_OK(p, "Mặc thành công!");
				} else {
					Service.send_box_ThongBao_OK(p, "Chưa mua vật phẩm này!");
				}
				break;
			}
		}
	}

	private static void use_item_7(Player p, short id) {}

	private static void use_item_4(Player p, short id) throws IOException {
		switch (id) {
			case 2: {
				EffTemplate eff = p.get_eff(0);
				if (eff == null) {
					int par = 60;
					par = (par * (100 + p.body.get_hp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(0, par, 10_000);
				}
				break;
			}
			case 3: {
				EffTemplate eff = p.get_eff(0);
				if (eff == null) {
					int par = 120;
					par = (par * (100 + p.body.get_hp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(0, par, 10_000);
				}
				break;
			}
			case 4: {
				EffTemplate eff = p.get_eff(1);
				if (eff == null) {
					int par = 2;
					par = (par * (100 + p.body.get_mp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(1, par, 10_000);
				}
				break;
			}
			case 5: {
				EffTemplate eff = p.get_eff(1);
				if (eff == null) {
					int par = 4;
					par = (par * (100 + p.body.get_mp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(1, par, 10_000);
				}
				break;
			}
			case 29: {
				short id_add = 86;
				if (15 > Util.random(120)) {
					id_add = 87;
				}
				open_taq_random(p, id_add, "Rương ác quỷ", "Nhận ngẫu nhiên");
				ItemBag47 it = new ItemBag47();
				it.id = id_add;
				it.category = 4;
				it.quant = 1;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
				break;
			}
			case 158: {
				short[] id_add = new short[] {32, 33, 92, 93, 160, 161, 219, 220, 240, 316, 317, 318};
				int index = Util.random(id_add.length);
				open_taq_random(p, id_add[index], "Rương đại ác quỷ", "Nhận ngẫu nhiên");
				ItemBag47 it = new ItemBag47();
				it.id = id_add[index];
				it.category = 4;
				it.quant = 1;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
				break;
			}
			case 32:
			case 33:
			case 34:
			case 88:
			case 90:
			case 91:
			case 92:
			case 93:
			case 160:
			case 161:
			case 219:
			case 220:
			case 240:
			case 316:
			case 317:
			case 318:
			case 427: {
				Service.send_box_yesno(p, (id + 4000), "Xác nhận ăn " + ItemTemplate4.get_it_by_id(id).name);
				break;
			}
			case 80: {
				EffTemplate eff = p.get_eff(2);
				if (eff != null && (eff.time > (System.currentTimeMillis() + 3000L))) {
					if ((eff.time - System.currentTimeMillis()) < (1000L * 60 * 60 * 24 * 7)) {
						eff.time += 1000L * 60 * 60 * 2;
					}
				} else {
					p.add_new_eff(2, 2, 1000L * 60 * 60 * 2);
				}
				Service.send_x2cd(p, false);
				break;
			}
			case 85: {
				EffTemplate eff = p.get_eff(0);
				if (eff == null) {
					int par = 200;
					par = (par * (100 + p.body.get_hp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(0, par, 10_000);
				}
				break;
			}
			case 86: {
				short[] id_add = new short[] {34, 88, 90, 91};
				int index = Util.random(id_add.length);
				open_taq_random(p, id_add[index], "Trái ác quỷ", "Nhận ngẫu nhiên trái ác quỷ sơ");
				ItemBag47 it = new ItemBag47();
				it.id = id_add[index];
				it.category = 4;
				it.quant = 1;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
				break;
			}
			case 87: {
				short[] id_add = new short[] {32, 33, 92, 93};
				int index = Util.random(id_add.length);
				open_taq_random(p, id_add[index], "Trái ác quỷ trung cấp", "Nhận ngẫu nhiên trái ác quỷ trung cấp");
				ItemBag47 it = new ItemBag47();
				it.id = id_add[index];
				it.category = 4;
				it.quant = 1;
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
				} else {
					Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
				}
				break;
			}
			case 112:
			case 113:
			case 114:
			case 115:
			case 116:
			case 117:
			case 118: {
				open_box(p, ItemTemplate4.get_it_by_id(id).type, (id - 111) * 10);
				break;
			}
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127:
			case 128: {
				open_box(p, ItemTemplate4.get_it_by_id(id).type, (id - 121) * 10);
				break;
			}
			case 159: {
				EffTemplate eff = p.get_eff(3);
				if (eff != null && (eff.time > (System.currentTimeMillis() + 3000L))) {
					if ((eff.time - System.currentTimeMillis()) < (1000L * 60 * 60 * 24 * 7)) {
						eff.time += 1000L * 60 * 60 * 2;
					}
				} else {
					p.add_new_eff(3, 2, 1000L * 60 * 60 * 2);
				}
				eff = p.get_eff(3);
				Service.send_box_ThongBao_OK(p, "Thời gian x2 xp chiếu thức hiện tại "
				      + (eff.time - System.currentTimeMillis()) / 1000 + " s, xem lại tại npc nico robin làng cối xay gió");
				break;
			}
			case 173: {
				EffTemplate eff = p.get_eff(0);
				if (eff == null) {
					int par = p.body.get_hp_max(true) / 200;
					if (par > 1_000) {
						par = 1_000;
					}
					par = (par * (100 + p.body.get_hp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(0, par, 10_000);
				}
				break;
			}
			case 174: {
				EffTemplate eff = p.get_eff(1);
				if (eff == null) {
					int par = 6;
					par = (par * (100 + p.body.get_mp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(1, par, 10_000);
				}
				break;
			}
			case 179: {
				EffTemplate eff = p.get_eff(1);
				if (eff == null) {
					int par = 6;
					par = (par * (100 + p.body.get_mp_potion_use_percent(true) / 10)) / 100;
					p.add_new_eff(1, par, 10_000);
					//
					p.update_info_to_all();
				}
				break;
			}
			case 214: {
				p.reset_point(0);
				Service.send_box_ThongBao_OK(p, "Tẩy thành công");
				break;
			}
			default: {
				Service.send_box_ThongBao_OK(p, "Chưa có chức năng item " + id);
				return;
			}
		}
	}

	private static void open_taq_random(Player p, short id, String name1, String name2) throws IOException {
		Message m = new Message(-34);
		m.writer().writeByte(21);
		m.writer().writeShort(-1);
		m.writer().writeUTF(name1);
		m.writer().writeUTF(name2);
		m.writer().writeByte(1);
		ItemTemplate4 it_temp = ItemTemplate4.get_it_by_id(id);
		m.writer().writeByte(4);
		m.writer().writeUTF(it_temp.name);
		m.writer().writeShort(it_temp.icon);
		m.writer().writeInt(1);
		m.writer().writeByte(0);
		p.conn.addmsg(m);
		m.cleanup();
	}

	private static void open_box(Player p, byte type, int level) throws IOException {
		switch (type) {
			case 22: {
				int bound1 = ((level / 10) * 192), bound2 = (((level / 10) + 1) * 192);
				List<Item_wear> list_receiv = new ArrayList<>();
				Item_wear temp = new Item_wear();
				int id_add = 0;
				while (!(ItemTemplate3.get_it_by_id(id_add).color == 3
				      && ItemTemplate3.get_it_by_id(id_add).typeEquip < 6)) {
					id_add = Util.random(bound1, bound2);
				}
				if (id_add > 0) {
					temp.setup_template_by_id(id_add);
					list_receiv.add(temp);
					if (temp.id > -1) {
						p.item.add_item_bag3(temp);
					}
					Service.open_box_item3_orange(p, list_receiv, 545, "Mở Khóa Rương", ("Rương Đồ Cam Lv" + level));
				} else {
					Service.send_box_ThongBao_OK(p, "Lỗi, hãy thử lại");
				}
				break;
			}
			case 23: {
				int bound1 = ((level / 10) * 192), bound2 = (((level / 10) + 1) * 192);
				List<Item_wear> list_receiv = new ArrayList<>();
				Item_wear temp = new Item_wear();
				int id_add = 0;
				while (!(ItemTemplate3.get_it_by_id(id_add).color == 3 && ItemTemplate3.get_it_by_id(id_add).typeEquip < 6
				      && (ItemTemplate3.get_it_by_id(id_add).clazz == 0
				            || ItemTemplate3.get_it_by_id(id_add).clazz == p.clazz))) {
					id_add = Util.random(bound1, bound2);
				}
				if (id_add > 0) {
					temp.setup_template_by_id(id_add);
					list_receiv.add(temp);
					if (temp.id > -1) {
						p.item.add_item_bag3(temp);
					}
					Service.open_box_item3_orange(p, list_receiv, 545, "Mở Khóa Rương", ("Rương Đồ Cam cùng hệ Lv" + level));
				} else {
					Service.send_box_ThongBao_OK(p, "Lỗi, hãy thử lại");
				}
				break;
			}
		}
	}

	private static void use_item_3(Player p, short id) throws IOException {
		if (p.use_item_3 == -1) {
			Item_wear it = p.item.bag3[id];
			if (it != null && check_it_can_wear(it.typeEquip)) {
				p.use_item_3 = id;
				if (it.typelock == 1) {
					p.wear_item(it);
				} else {
					Service.send_box_yesno(p, 0, "Khi sử dụng vật phẩm sẽ bị khóa, hãy xác nhận");
				}
			} else {
				Service.send_box_ThongBao_OK(p, "Tạm Khóa");
			}
		}
	}

	public static boolean check_it_can_wear(byte type) {
		return type >= 0 && type <= 7;
	}

	public static void use_item_potion(Player p, short id) throws IOException {
		// System.out.println(id);
		if (p.item.total_item_bag_by_id(4, id) > 0) {
			use_item_4(p, id);
			p.item.remove_item47(4, id, 1);
			Message m2 = new Message(-13);
			m2.writer().writeShort(id);
			m2.writer().writeShort(p.item.total_item_bag_by_id(4, id));
			p.conn.addmsg(m2);
			m2.cleanup();
			//
			p.item.update_Inventory(4, false);
			p.item.update_Inventory(7, false);
			p.item.update_Inventory(3, false);
		}
	}
}
