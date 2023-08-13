package template;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemHair {
	public static List<ItemHair> ENTRYS = new ArrayList<>();
	public final byte ID;
	public final short idIcon;
	public final String name;
	public final int beri;
	public final short ruby;
	public byte type;

	public ItemHair(byte ID, short IDIcon, String name, int b, short r) {
		this.ID = ID;
		this.idIcon = IDIcon;
		this.name = name;
		this.beri = b;
		this.ruby = r;
	}

	public static ItemHair readUpdateItemHair(DataInputStream dis) throws IOException {
		byte b = dis.readByte();
		String name = dis.readUTF();
		byte b2 = dis.readByte();
		short idicon = dis.readShort();
		short num = dis.readShort();
		int price = dis.readInt();
		short priceRuby = dis.readShort();
		return new ItemHair(b, idicon, name, price, priceRuby);
	}

	public static ItemHair get_item(short id, int type) {
		for (int i = 0; i < ItemHair.ENTRYS.size(); i++) {
			ItemHair temp = ItemHair.ENTRYS.get(i);
			if (temp.ID == id && temp.type == type) {
				return temp;
			}
		}
		return null;
	}

	public static int get_size_type(int type) {
		int size = 0;
		for (int i = 0; i < ItemHair.ENTRYS.size(); i++) {
			ItemHair temp = ItemHair.ENTRYS.get(i);
			if (temp.type == type) {
				size++;
			}
		}
		return size;
	}
}
