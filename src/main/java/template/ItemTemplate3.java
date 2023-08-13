package template;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemTemplate3 {
	public static List<ItemTemplate3> ENTRYS = new ArrayList<>();
	public short id;
	public String name;
	public byte clazz;
	public byte typeEquip;
	public short icon;
	public short level;
	public byte color;
	public byte typelock;
	public byte numHoleDaDuc;
	public short valueChetac;
	public byte isHoanMy;
	public byte valueKichAn;
	public List<Option> option_item;
	public List<Option> option_item_2;
	public byte numLoKham;
	public short[] mdakham;
	public short part;
	public int beri;
	public int ruby;

	public static ItemTemplate3 get_it_by_id(int id) {
		for (int i = 0; i < ItemTemplate3.ENTRYS.size(); i++) {
			if (ItemTemplate3.ENTRYS.get(i).id == id) {
				return ItemTemplate3.ENTRYS.get(i);
			}
		}
		return null;
	}

	public static void readUpdateItem(DataOutputStream dos, ItemTemplate3 it) throws IOException {
		dos.writeShort(it.id);
		dos.writeUTF(it.name);
		dos.writeByte(it.clazz);
		dos.writeByte(it.typeEquip);
		dos.writeShort(it.icon);
		dos.writeShort(it.level);
		dos.writeByte(0); // level up
		dos.writeByte(it.color);
		dos.writeByte(0); // is trade
		dos.writeByte(it.typelock);
		dos.writeByte(it.numHoleDaDuc);
		dos.writeInt(0); // time use
		dos.writeShort(it.valueChetac);
		dos.writeByte(it.isHoanMy);
		dos.writeByte(it.valueKichAn);
		dos.writeByte(it.option_item.size());
		for (int i = 0; i < it.option_item.size(); i++) {
			dos.writeByte(it.option_item.get(i).id);
			dos.writeShort(it.option_item.get(i).getParam(0));
		}
		dos.writeByte(it.option_item_2.size());
		for (int i = 0; i < it.option_item_2.size(); i++) {
			dos.writeByte(it.option_item_2.get(i).id);
			dos.writeShort(it.option_item_2.get(i).getParam(0));
		}
		dos.writeByte(it.numLoKham);
		dos.writeByte(it.mdakham.length);
		for (int i = 0; i < it.mdakham.length; i++) {
			dos.writeShort(it.mdakham[i]);
		}
	}

	public static void update() {
		for (int i = 0; i < ItemTemplate3.ENTRYS.size(); i++) {
			ItemTemplate3.ENTRYS.get(i).part = Part.get_part(ItemTemplate3.ENTRYS.get(i).id);
		}
	}
}
