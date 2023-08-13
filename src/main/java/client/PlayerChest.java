package client;

import java.io.IOException;
import core.Service;
import io.Message;
import template.ItemBag47;
import template.Item_wear;

public class PlayerChest {
	public static void process(Player p, Message m2) throws IOException {
		byte act = m2.reader().readByte();
		short id = m2.reader().readShort();
		byte cat = m2.reader().readByte();
		int num = m2.reader().readInt();
		System.out.println(act);
		System.out.println(id);
		System.out.println(cat);
		System.out.println(num);
		if (act == 1) { // cat vao
			if (p.item.able_box() < 1) {
				Service.send_box_ThongBao_OK(p, "Rương đầy!");
				return;
			}
			switch (cat) {
				case 3: {
					Item_wear it_select = p.item.bag3[id];
					if (it_select != null) {
						Item_wear it_add = new Item_wear();
						it_add.clone_obj(it_select);
						p.item.add_item_box3(it_add);
						//
						p.item.bag3[id] = null;
					}
					break;
				}
				case 4:
				case 7: {
					if (p.item.total_item_bag_by_id(cat, id) >= num) {
						ItemBag47 it = new ItemBag47();
						it.id = id;
						it.category = cat;
						it.quant = (short) num;
						if ((p.item.total_item_box_by_id(cat, it.id) + it.quant) < 32_000) {
							p.item.add_item_box47(cat, it);
							//
							p.item.remove_item47(cat, id, num);
						}
					}
					break;
				}
			}
		} else { // lay ra
			if (p.item.able_bag() < 1) {
				Service.send_box_ThongBao_OK(p, "Rương đầy!");
				return;
			}
			switch (cat) {
				case 3: {
					Item_wear it_select = p.item.box3[id];
					if (it_select != null) {
						Item_wear it_add = new Item_wear();
						it_add.clone_obj(it_select);
						p.item.add_item_bag3(it_add);
						//
						p.item.box3[id] = null;
					}
					break;
				}
				case 4:
				case 7: {
					if (p.item.total_item_box_by_id(cat, id) >= num) {
						ItemBag47 it = new ItemBag47();
						it.id = id;
						it.category = cat;
						it.quant = (short) num;
						if ((p.item.total_item_bag_by_id(cat, it.id) + it.quant) < 32_000) {
							p.item.add_item_bag47(cat, it);
							//
							p.item.remove_item47_box(cat, id, num);
						}
					}
					break;
				}
			}
		}
		p.item.update_Inventory(4, false);
		p.item.update_Inventory(7, false);
		p.item.update_Inventory(3, false);
		//
		p.item.update_Inventory_box(4, false);
		p.item.update_Inventory_box(7, false);
		p.item.update_Inventory_box(3, false);
	}
}
