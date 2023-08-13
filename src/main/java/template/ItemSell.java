package template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemSell {
	public short id;
	public int price;
	public static HashMap<Integer, List<List<ItemSell>>> ENTRYS = new HashMap<>();
	static {
		for (int i = 0; i < ItemTemplate3.ENTRYS.size(); i++) {
			ItemTemplate3 it_temp = ItemTemplate3.ENTRYS.get(i);
			if (it_temp.beri > 0) {
				ItemSell temp = new ItemSell(it_temp.id, it_temp.beri);
				List<List<ItemSell>> list_temp = ItemSell.ENTRYS.get(it_temp.level / 10);
				if (list_temp == null) {
					list_temp = new ArrayList<>();
					for (int j = 0; j < 5; j++) {
						list_temp.add(new ArrayList<>());
					}
					ItemSell.ENTRYS.put((it_temp.level / 10), list_temp);
				}
				List<ItemSell> temp1 = list_temp.get(it_temp.clazz - 1);
				temp1.add(temp);
			}
		}
	}

	public ItemSell(short id, int price) {
		this.id = id;
		this.price = price;
	}

	public static List<ItemSell> get_it_sell(int level, int clazz) {
		List<ItemSell> result = new ArrayList<>();
		level /= 10;
		if (level < 2) {
			result.addAll(ItemSell.ENTRYS.get(0).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(1).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(2).get(clazz));
		} else if (level > 7) {
			result.addAll(ItemSell.ENTRYS.get(7).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(8).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(9).get(clazz));
		} else {
			result.addAll(ItemSell.ENTRYS.get(level - 1).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(level).get(clazz));
			result.addAll(ItemSell.ENTRYS.get(level + 1).get(clazz));
		}
		return result;
	}
}
