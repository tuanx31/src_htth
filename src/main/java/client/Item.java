package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import io.Message;
import template.ItemBag47;
import template.ItemTemplate3;
import template.Item_wear;
import template.Option;

public class Item {
	private final Player p;
	public Item_wear[] bag3;
	public Item_wear[] box3;
	public Item_wear[] it_body;
	public byte max_bag;
	public byte max_box;
	public List<ItemBag47> bag47;
	public List<ItemBag47> box47;

	public Item(Player p) {
		this.p = p;
	}

	public void send_maxbag_Inventory() throws IOException {
		Message m = new Message(-12);
		m.writer().writeByte(6);
		m.writer().writeByte(3);
		m.writer().writeShort(max_bag); // max bag
		p.conn.addmsg(m);
		m.cleanup();
	}

	public void send_maxbox_Inventory() throws IOException {
		Message m = new Message(-32);
		m.writer().writeByte(6);
		m.writer().writeByte(3);
		m.writer().writeShort(max_box); // max box
		p.conn.addmsg(m);
		m.cleanup();
	}

	public void update_Inventory(int type, boolean save_cache) throws IOException {
		Message m = new Message(-12);
		m.writer().writeByte(0);
		m.writer().writeByte(type);
		switch (type) {
			case 3: {
				m.writer().writeByte(this.quant_item_inbag(3));
				for (int i = 0; i < bag3.length; i++) {
					if (bag3[i] != null) {
						Item.readUpdateItem(m.writer(), bag3[i]);
					}
				}
				break;
			}
			case 4:
			case 8: {
				m.writer().writeByte(this.quant_item_inbag(4));
				for (int i = 0; i < bag47.size(); i++) {
					if (bag47.get(i).category == 4) {
						m.writer().writeShort(bag47.get(i).id);
						m.writer().writeShort(bag47.get(i).quant);
					}
				}
				break;
			}
			case 5: {
				m.writer().writeByte(0);
				break;
			}
			case 7: {
				m.writer().writeByte(this.quant_item_inbag(7));
				for (int i = 0; i < bag47.size(); i++) {
					if (bag47.get(i).category == 7) {
						m.writer().writeByte(bag47.get(i).id);
						m.writer().writeShort(bag47.get(i).quant);
					}
				}
				break;
			}
		}
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public void update_Inventory_box(int type, boolean save_cache) throws IOException {
		Message m = new Message(-32);
		m.writer().writeByte(0);
		m.writer().writeByte(type);
		switch (type) {
			case 3: {
				m.writer().writeByte(this.quant_item_inbox(3));
				for (int i = 0; i < box3.length; i++) {
					if (box3[i] != null) {
						Item.readUpdateItem(m.writer(), box3[i]);
					}
				}
				break;
			}
			case 4:
			case 8: {
				m.writer().writeByte(this.quant_item_inbox(4));
				for (int i = 0; i < box47.size(); i++) {
					if (box47.get(i).category == 4) {
						m.writer().writeShort(box47.get(i).id);
						m.writer().writeShort(box47.get(i).quant);
					}
				}
				break;
			}
			case 5: {
				m.writer().writeByte(0);
				break;
			}
			case 7: {
				m.writer().writeByte(this.quant_item_inbox(7));
				for (int i = 0; i < box47.size(); i++) {
					if (box47.get(i).category == 7) {
						m.writer().writeByte(box47.get(i).id);
						m.writer().writeShort(box47.get(i).quant);
					}
				}
				break;
			}
		}
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public void update_assets_Inventory(boolean save_cache) throws IOException {
		Message m = new Message(-12);
		m.writer().writeByte(3);
		m.writer().writeByte(6);
		//
		m.writer().writeLong(p.get_vang());
		m.writer().writeInt(p.get_ngoc());
		m.writer().writeShort(20); // ticket
		m.writer().writeShort(20); // max ticket
		m.writer().writeByte(3);
		m.writer().writeByte(3); // max pvp ticket
		m.writer().writeByte(3);
		m.writer().writeByte(3); // max key boss
		m.writer().writeInt(0); // vnd
		m.writer().writeInt(0); // bua
		m.writer().writeInt(0); // diem nap
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public void update_assets_Box(boolean save_cache) throws IOException {
		Message m = new Message(-32);
		m.writer().writeByte(3);
		m.writer().writeByte(6);
		//
		m.writer().writeLong(p.get_vang());
		m.writer().writeInt(p.get_ngoc());
		m.writer().writeInt(0); // vnd
		m.writer().writeInt(0); // bua
		m.writer().writeInt(0); // diem nap
		if (save_cache) {
			p.list_msg_cache.add(m);
		} else {
			p.conn.addmsg(m);
		}
		m.cleanup();
	}

	public static void readUpdateItem(DataOutputStream dos, Item_wear it) throws IOException {
		dos.writeShort(it.index);
		dos.writeUTF(it.name);
		dos.writeByte(it.clazz);
		dos.writeByte(it.typeEquip);
		dos.writeShort(it.icon);
		dos.writeShort(it.level);
		dos.writeByte(it.levelup);
		dos.writeByte(it.color);
		dos.writeByte(it.isTrade);
		dos.writeByte(it.typelock);
		dos.writeByte(it.numHoleDaDuc);
		dos.writeInt(it.timeUse);
		dos.writeShort(it.valueChetac);
		dos.writeByte(it.isHoanMy);
		dos.writeByte(it.valueKichAn);
		dos.writeByte(it.option_item.size());
		for (int i = 0; i < it.option_item.size(); i++) {
			dos.writeByte(it.option_item.get(i).id);
			dos.writeShort(it.option_item.get(i).getParam(it.levelup));
		}
		dos.writeByte(it.option_item_2.size());
		for (int i = 0; i < it.option_item_2.size(); i++) {
			dos.writeByte(it.option_item_2.get(i).id);
			dos.writeShort(it.option_item_2.get(i).getParam(it.levelup));
		}
		dos.writeByte(it.numLoKham);
		dos.writeByte(it.mdakham.length);
		for (int i = 0; i < it.mdakham.length; i++) {
			dos.writeShort(it.mdakham[i]);
		}
	}

	public static void readUpdateItem(String jsdata, Item_wear it) {
		JSONArray js = (JSONArray) JSONValue.parse(jsdata);
		it.id = Short.parseShort(js.get(0).toString());
		// it.islock = Byte.parseByte(js.get(1).toString()) == 1;
		it.name = ItemTemplate3.get_it_by_id(it.id).name;
		// if (it.islock) {
		// it.name += " [Khóa]";
		// }
		it.clazz = Byte.parseByte(js.get(2).toString());
		it.typeEquip = Byte.parseByte(js.get(3).toString());
		it.icon = Short.parseShort(js.get(4).toString());
		it.level = Short.parseShort(js.get(5).toString());
		it.levelup = Byte.parseByte(js.get(6).toString());
		it.color = Byte.parseByte(js.get(7).toString());
		it.isTrade = Byte.parseByte(js.get(8).toString());
		it.typelock = Byte.parseByte(js.get(9).toString());
		it.numHoleDaDuc = Byte.parseByte(js.get(10).toString());
		it.timeUse = Integer.parseInt(js.get(11).toString());
		it.valueChetac = Short.parseShort(js.get(12).toString());
		it.isHoanMy = Byte.parseByte(js.get(13).toString());
		it.valueKichAn = Byte.parseByte(js.get(14).toString());
		it.option_item = new ArrayList<>();
		JSONArray js2 = (JSONArray) JSONValue.parse(js.get(15).toString());
		for (int i = 0; i < js2.size(); i++) {
			JSONArray js_3 = (JSONArray) JSONValue.parse(js2.get(i).toString());
			it.option_item
			      .add(new Option(Byte.parseByte(js_3.get(0).toString()), Short.parseShort(js_3.get(1).toString())));
		}
		it.option_item_2 = new ArrayList<>();
		JSONArray js4 = (JSONArray) JSONValue.parse(js.get(16).toString());
		for (int i = 0; i < js4.size(); i++) {
			JSONArray js_3 = (JSONArray) JSONValue.parse(js4.get(i).toString());
			it.option_item_2
			      .add(new Option(Byte.parseByte(js_3.get(0).toString()), Short.parseShort(js_3.get(1).toString())));
		}
		it.numLoKham = Byte.parseByte(js.get(17).toString());
		JSONArray js5 = (JSONArray) JSONValue.parse(js.get(18).toString());
		it.mdakham = new short[js5.size()];
		for (int i = 0; i < it.mdakham.length; i++) {
			it.mdakham[i] = Short.parseShort(js5.get(i).toString());
		}
		it.index = Byte.parseByte(js.get(19).toString());
	}

	public void add_item_bag3(Item_wear it_add) {
		if (able_bag() > 0) {
			for (int i = 0; i < bag3.length; i++) {
				if (bag3[i] == null) {
					bag3[i] = it_add;
					it_add.index = (byte) i;
					break;
				}
			}
		}
	}

	public void add_item_box3(Item_wear it_add) {
		if (able_box() > 0) {
			for (int i = 0; i < box3.length; i++) {
				if (box3[i] == null) {
					box3[i] = it_add;
					it_add.index = (byte) i;
					break;
				}
			}
		}
	}

	public int able_bag() {
		return this.max_bag - this.quant_item_inbag(3) - this.quant_item_inbag(4) - this.quant_item_inbag(7);
	}

	public int able_box() {
		return this.max_box - this.quant_item_inbox(3) - this.quant_item_inbox(4) - this.quant_item_inbox(7);
	}

	private int quant_item_inbag(int type) {
		int par = 0;
		switch (type) {
			case 3: {
				for (int i = 0; i < bag3.length; i++) {
					if (bag3[i] != null) {
						par++;
					}
				}
			}
			case 4:
			case 7: {
				for (int i = 0; i < bag47.size(); i++) {
					if (bag47.get(i).category == type) {
						par++;
					}
				}
				break;
			}
		}
		return par;
	}

	private int quant_item_inbox(int type) {
		int par = 0;
		switch (type) {
			case 3: {
				for (int i = 0; i < box3.length; i++) {
					if (box3[i] != null) {
						par++;
					}
				}
			}
			case 4:
			case 7: {
				for (int i = 0; i < box47.size(); i++) {
					if (box47.get(i).category == type) {
						par++;
					}
				}
				break;
			}
		}
		return par;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray it_data_to_json(Item_wear it) {
		JSONArray js = new JSONArray();
		js.add(it.id);
		js.add(-1); // islock old, not use
		js.add(it.clazz);
		js.add(it.typeEquip);
		js.add(it.icon);
		js.add(it.level);
		js.add(it.levelup);
		js.add(it.color);
		js.add(it.isTrade);
		js.add(it.typelock);
		js.add(it.numHoleDaDuc);
		js.add(it.timeUse);
		js.add(it.valueChetac);
		js.add(it.isHoanMy);
		js.add(it.valueKichAn);
		JSONArray js_2 = new JSONArray();
		for (int i = 0; i < it.option_item.size(); i++) {
			JSONArray js_3 = new JSONArray();
			js_3.add(it.option_item.get(i).id);
			js_3.add(it.option_item.get(i).getParam(0));
			js_2.add(js_3);
		}
		js.add(js_2);
		JSONArray js_4 = new JSONArray();
		for (int i = 0; i < it.option_item_2.size(); i++) {
			JSONArray js_3 = new JSONArray();
			js_3.add(it.option_item_2.get(i).id);
			js_3.add(it.option_item_2.get(i).getParam(0));
			js_4.add(js_3);
		}
		js.add(js_4);
		js.add(it.numLoKham);
		JSONArray js_5 = new JSONArray();
		for (int i = 0; i < it.mdakham.length; i++) {
			js_5.add(it.mdakham[i]);
		}
		js.add(js_5);
		js.add(it.index);
		return js;
	}

	public void add_item_bag47(int type, ItemBag47 it) {
		ItemBag47 it_in_bag = null;
		for (int i = 0; i < bag47.size(); i++) {
			if (bag47.get(i).category == type && bag47.get(i).id == it.id) {
				it_in_bag = bag47.get(i);
				break;
			}
		}
		if (it_in_bag != null) {
			it_in_bag.quant += it.quant;
		} else {
			if (able_bag() > 0) {
				this.bag47.add(it);
			}
		}
	}

	public void add_item_box47(int type, ItemBag47 it) {
		ItemBag47 it_in_bag = null;
		for (int i = 0; i < box47.size(); i++) {
			if (box47.get(i).category == type && box47.get(i).id == it.id) {
				it_in_bag = box47.get(i);
				break;
			}
		}
		if (it_in_bag != null) {
			it_in_bag.quant += it.quant;
		} else {
			if (able_bag() > 0) {
				this.box47.add(it);
			}
		}
	}

	public int total_item_bag_by_id(int type, int id) {
		int par = 0;
		switch (type) {
			case 4:
			case 7: {
				for (int i = 0; i < bag47.size(); i++) {
					if (bag47.get(i).category == type && bag47.get(i).id == id) {
						par += bag47.get(i).quant;
					}
				}
				break;
			}
		}
		return par;
	}

	public int total_item_box_by_id(int type, int id) {
		int par = 0;
		switch (type) {
			case 4:
			case 7: {
				for (int i = 0; i < box47.size(); i++) {
					if (box47.get(i).category == type && box47.get(i).id == id) {
						par += box47.get(i).quant;
					}
				}
				break;
			}
		}
		return par;
	}

	public void remove_item47(int type, int id, int num) throws IOException {
		for (int i = 0; i < bag47.size(); i++) {
			if (bag47.get(i).category == type && bag47.get(i).id == id) {
				bag47.get(i).quant -= num;
				if (bag47.get(i).quant <= 0) {
					bag47.remove(i);
				}
				break;
			}
		}
	}

	public void remove_item47_box(int type, int id, int num) throws IOException {
		for (int i = 0; i < box47.size(); i++) {
			if (box47.get(i).category == type && box47.get(i).id == id) {
				box47.get(i).quant -= num;
				if (box47.get(i).quant <= 0) {
					box47.remove(i);
				}
				break;
			}
		}
	}

	public void remove_item_wear(Item_wear item_wear) {
		for (int i = 0; i < bag3.length; i++) {
			if (bag3[i] != null && bag3[i].equals(item_wear)) {
				bag3[i] = null;
				break;
			}
		}
	}
}
