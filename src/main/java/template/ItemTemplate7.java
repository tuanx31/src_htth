package template;

import java.util.ArrayList;
import java.util.List;

public class ItemTemplate7 {
	public static List<ItemTemplate7> ENTRYS = new ArrayList<>();
	public short id;
	public String name;

	public static ItemTemplate7 get_it_by_id(int id) {
		for (int i = 0; i < ItemTemplate7.ENTRYS.size(); i++) {
			if (ItemTemplate7.ENTRYS.get(i).id == id) {
				return ItemTemplate7.ENTRYS.get(i);
			}
		}
		return null;
	}
}
