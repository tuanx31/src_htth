package map;

import java.io.IOException;
import client.Player;
import core.Util;
import io.Message;
import template.ItemBag47;
import template.ItemMap;

public class LeaveItemMap {
	public static void leave_vang(Map map, Mob mob_target, Player p0) throws IOException {
		int vang_random = mob_target.level * Util.random(1, 200);
		//
		ItemMap itm = new ItemMap();
		itm.id = -1;
		itm.id_master = (short) p0.id;
		itm.category = 4;
		itm.icon = 0;
		itm.color = 0;
		itm.name = vang_random + " Beri";
		itm.quant = (short) vang_random;
		itm.time_exist = System.currentTimeMillis() + 50_000L;
		itm.time_pick_master = System.currentTimeMillis() + 35_000L;
		itm.index_mob_die = (short) mob_target.index;
		// if (map.add_item_map(itm)) {
		if (p0.add_item_map(itm, 4)) {
			Message m = new Message(11);
			m.writer().writeByte(1); // size item
			//
			m.writer().writeShort(itm.id); // id item
			m.writer().writeByte(itm.category); // item category
			m.writer().writeShort(itm.icon); // icon
			m.writer().writeByte(itm.color); // color
			m.writer().writeUTF(itm.name); // name
			//
			m.writer().writeShort(mob_target.index); // index mob leave item
			m.writer().writeByte(0); // type obj
			m.writer().writeShort(p0.id);
			// map.send_msg_all_p(m, null, true);
			p0.conn.addmsg(m);
			m.cleanup();
		}
	}

	public static void get_item_boss_leave(Player p, Mob mob_target) throws IOException {
		short[] id_ = new short[] {1, 2, 3, 4, 5, 6, 9};
		for (int i = 0; i < id_.length; i++) {
			int quant = (20 > Util.random(100)) ? 0 : Util.random(10);
			for (int j = 0; j < quant; j++) {
				ItemBag47 it = new ItemBag47();
				it.id = id_[i];
				it.category = 7;
				it.quant = (short) Util.random(5);
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(7, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(7, it);
				}
			}
		}
		//
		id_ = new short[] {2, 3, 4, 5};
		for (int i = 0; i < id_.length; i++) {
			int quant = (20 > Util.random(100)) ? 0 : Util.random(10);
			for (int j = 0; j < quant; j++) {
				ItemBag47 it = new ItemBag47();
				it.id = id_[i];
				it.category = 4;
				it.quant = (short) Util.random(5);
				if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
					p.item.add_item_bag47(4, it);
				}
			}
		}
		//
		id_ = new short[] {112, 113, 114, 115, 116, 117, 118, 119};
		ItemBag47 it = new ItemBag47();
		it.id = id_[Util.random(id_.length)];
		it.category = 4;
		it.quant = (short) 1;
		if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
			p.item.add_item_bag47(4, it);
		}
		p.item.update_Inventory(4, false);
		p.item.update_Inventory(7, false);
		p.item.update_Inventory(3, false);
	}
}
