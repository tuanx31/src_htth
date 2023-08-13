package template;

import java.util.ArrayList;
import java.util.List;

public class ItemTemplate4 {
	public static List<ItemTemplate4> ENTRYS = new ArrayList<>();
	public short icon;
	public short id;
	public String name;
	public String description;
	public byte type;
	public int ruby;
	public int beri;

	public static ItemTemplate4 get_it_by_id(int id) {
		for (int i = 0; i < ItemTemplate4.ENTRYS.size(); i++) {
			if (ItemTemplate4.ENTRYS.get(i).id == id) {
				return ItemTemplate4.ENTRYS.get(i);
			}
		}
		return null;
	}

	public static String get_item_description(short id) {
		for (int i = 0; i < ItemTemplate4.ENTRYS.size(); i++) {
			if (ItemTemplate4.ENTRYS.get(i).id == id) {
				if (id == 214) {
					return ItemTemplate4.ENTRYS.get(i).description + " giá 20 bery";
				} else {
					return ItemTemplate4.ENTRYS.get(i).description;
				}
			}
		}
		return null;
	}

	public static String get_item_name(short id) {
		String s = "";
		for (int i = 0; i < ItemTemplate4.ENTRYS.size(); i++) {
			if (ItemTemplate4.ENTRYS.get(i).id == id) {
				s = ItemTemplate4.ENTRYS.get(i).name;
				break;
			}
		}
		return s;
	}
}
