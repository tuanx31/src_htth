package template;

import java.util.ArrayList;
import java.util.List;
import core.Util;

public class ItemVongQuayTemplate {
	public static List<ItemVongQuayTemplate> ENTRYS = new ArrayList<>();
	public byte type;
	public short id;
	static {
		byte[] type = new byte[] {4};
		short[] id_ = new short[] {127, 128, 177, 178, 179, 7, 113, 114, 100, 79, 9, 158, 58, 29, 372};
		for (int i = 0; i < id_.length; i++) {
			ItemVongQuayTemplate temp = new ItemVongQuayTemplate();
			temp.id = id_[i];
			temp.type = 4;
			ENTRYS.add(temp);
		}
	}

	public static ItemVongQuayTemplate get_random() {
		if (65 > Util.random(100)) {
			return null;
		}
		int index = Util.random(8);
		if (80 > Util.random(100)) {
			index = Util.random(2, 6);
		}
		if (10 == Util.random(10_000)) {
			index = Util.random(14);
		}
		if (10 == Util.random(100)) {
			index = 13;
		}
		if (10 == Util.random(500)) {
			index = 11;
		}
		return ItemVongQuayTemplate.ENTRYS.get(index);
	}
}
