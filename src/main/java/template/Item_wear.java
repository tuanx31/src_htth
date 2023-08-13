package template;

import java.util.ArrayList;
import java.util.List;
import core.Util;

public class Item_wear {
	public short id;
	public String name;
	public byte clazz;
	public byte typeEquip;
	public short icon;
	public short level;
	public byte levelup;
	public byte color;
	public byte isTrade;
	public byte typelock;
	public byte numHoleDaDuc;
	public int timeUse;
	public short valueChetac;
	public byte isHoanMy;
	public byte valueKichAn;
	public List<Option> option_item;
	public List<Option> option_item_2;
	public byte numLoKham;
	public short[] mdakham;
	public byte index;

	public void setup_template_by_id(int id) {
		ItemTemplate3 it_temp = ItemTemplate3.get_it_by_id(id);
		if (it_temp != null) {
			this.id = (short) id;
			this.name = it_temp.name;
			this.clazz = it_temp.clazz;
			this.typeEquip = it_temp.typeEquip;
			this.icon = it_temp.icon;
			this.level = it_temp.level;
			this.levelup = 0;
			this.color = it_temp.color;
			this.isTrade = 0;
			this.typelock = it_temp.typelock;
			this.numHoleDaDuc = it_temp.numHoleDaDuc;
			this.timeUse = 0;
			this.valueChetac = (short) Util.random(it_temp.valueChetac);
			this.isHoanMy = it_temp.isHoanMy;
			this.valueKichAn = it_temp.valueKichAn;
			this.option_item = new ArrayList<>();
			for (int i = 0; i < it_temp.option_item.size(); i++) {
				int param_random = it_temp.option_item.get(i).getParam(0);
				param_random -= (param_random * Util.random(10)) / 100;
				this.option_item.add(new Option(it_temp.option_item.get(i).id, param_random));
			}
			this.option_item_2 = new ArrayList<>();
			// for (int i = 0; i < it_temp.option_item_2.size(); i++) {
			// int param_random = it_temp.option_item_2.get(i).getParam(0);
			// param_random-= (param_random*Util.random(10))/100;
			// this.option_item_2.add(new Option(it_temp.option_item_2.get(i).id, param_random));
			// }
			this.numLoKham = it_temp.numLoKham;
			this.mdakham = new short[it_temp.mdakham.length];
			for (int i = 0; i < it_temp.mdakham.length; i++) {
				this.mdakham[i] = it_temp.mdakham[i];
			}
			this.index = 0;
			//
			if (this.option_item.size() == 0) {
				for (int i = 0; i < 3; i++) {
					int id_add = Util.random(25);
					int par_add = ItemOptionTemplate.ENTRYS.get(id_add).is_percent
					      ? ((Util.random(10, 20) * this.level * (this.color + 1)) / 50)
					      : ((Util.random(1, 10) * this.level * (this.color + 1)) / 70);
					if (par_add > 0) {
						this.option_item.add(new Option(id_add, par_add));
					}
				}
			}
			if (this.color == 3) {
				byte[] id_can_add = null;
				int id_add = -1;
				int par_add = -1;
				switch (this.typeEquip) {
					case 0:
					case 2:
					case 4: {
						id_can_add = new byte[] {5, 6, 7, 8, 9, 1, 10, 11, 13, 21, 22};
						id_add = id_can_add[Util.random(id_can_add.length)];
						par_add = ItemOptionTemplate.ENTRYS.get(id_add).is_percent
						      ? ((Util.random(10, 20) * this.level * (this.color + 1)) / 50)
						      : ((Util.random(1, 10) * this.level * (this.color + 1)) / 70);
						break;
					}
					case 1:
					case 3:
					case 5: {
						id_can_add = new byte[] {5, 6, 7, 8, 9, 3, 4, 12, 14, 15, 16, 17, 18, 19, 20, 23, 24, 26, 27};
						id_add = id_can_add[Util.random(id_can_add.length)];
						par_add = ItemOptionTemplate.ENTRYS.get(id_add).is_percent
						      ? ((Util.random(10, 20) * this.level * (this.color + 1)) / 35)
						      : ((Util.random(1, 10) * this.level * (this.color + 1)) / 55);
						break;
					}
				}
				if (par_add > 0) {
					this.option_item.add(new Option(id_add, par_add));
				}
			}
		} else { // vatpham load template loi
			this.id = -1;
		}
	}

	public void clone_obj(Item_wear it_temp) {
		if (it_temp != null) {
			this.id = it_temp.id;
			this.name = it_temp.name;
			this.clazz = it_temp.clazz;
			this.typeEquip = it_temp.typeEquip;
			this.icon = it_temp.icon;
			this.level = it_temp.level;
			this.levelup = it_temp.levelup;
			this.color = it_temp.color;
			this.isTrade = it_temp.isTrade;
			this.typelock = it_temp.typelock;
			this.numHoleDaDuc = it_temp.numHoleDaDuc;
			this.timeUse = it_temp.timeUse;
			this.valueChetac = it_temp.valueChetac;
			this.isHoanMy = it_temp.isHoanMy;
			this.valueKichAn = it_temp.valueKichAn;
			this.option_item = new ArrayList<>();
			for (int i = 0; i < it_temp.option_item.size(); i++) {
				this.option_item.add(new Option(it_temp.option_item.get(i).id, it_temp.option_item.get(i).getParam(0)));
			}
			this.option_item_2 = new ArrayList<>();
			for (int i = 0; i < it_temp.option_item_2.size(); i++) {
				this.option_item_2
				      .add(new Option(it_temp.option_item_2.get(i).id, it_temp.option_item_2.get(i).getParam(0)));
			}
			this.numLoKham = it_temp.numLoKham;
			this.mdakham = new short[it_temp.mdakham.length];
			for (int i = 0; i < it_temp.mdakham.length; i++) {
				this.mdakham[i] = it_temp.mdakham[i];
			}
			this.index = it_temp.index;
		}
	}
}
