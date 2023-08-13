package client;

import java.io.IOException;
import activities.Kham_Ngoc;
import activities.Trade;
import core.Service;
import io.Message;
import template.ItemBag47;
import template.Item_wear;
import template.Skill_Template;
import template.Skill_info;

public class ClientYesNo {
	public static void process(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		byte value = m2.reader().readByte();
		if (value == 0) { // ok
			switch (id) {
				case 2: {
					if (p.get_ngoc() < 5) {
						Service.send_box_ThongBao_OK(p, "Không đủ 5 ruby");
						return;
					}
					p.update_ngoc(-5);
					p.update_money();
					for (int i = 0; i < p.skill_point.size(); i++) {
						Skill_info temp = p.skill_point.get(i);
						if (temp.temp.ID >= 1000 && temp.temp.ID < 2000 && temp.temp.Lv_RQ != -1) {
							Skill_Template.reset_skill(temp);
						}
					}
					p.send_skill();
					p.update_info_to_all();
					Service.send_box_ThongBao_OK(p, "Tẩy thành công");
					break;
				}
				case 4032: {
					String[] name_ = new String[] {"Sức mạnh của lửa", "Hỏa quyền", "Nắm đấm lửa"};
					short[] icon_ = new short[] {32, 30, 29};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4033: {
					String[] name_ = new String[] {"Sức sống bất diệt", "Chất bất ổn", "Súng máy causu"};
					short[] icon_ = new short[] {34, 33, 31};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4034: {
					String[] name_ = new String[] {"Tiến hóa", "Thuốc tăng trưởng", "Hóa tuần lộc"};
					short[] icon_ = new short[] {37, 36, 35};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4088: {
					String[] name_ = new String[] {"Khói bất tử", "Khói tốc độ", "Mưa khói"};
					short[] icon_ = new short[] {38, 39, 40};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4090: {
					String[] name_ = new String[] {"Bản năng thủ lĩnh", "Hóa bò tót", "Bất khuất"};
					short[] icon_ = new short[] {48, 47, 46};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4091: {
					String[] name_ = new String[] {"Nét vẽ cường hóa", "Nét vẽ phòng thủ", "Nét vẽ sức mạnh"};
					short[] icon_ = new short[] {51, 50, 49};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4092: {
					String[] name_ = new String[] {"Băng vĩnh cửu", "Mưa băng", "Tuyết tê tái"};
					short[] icon_ = new short[] {57, 56, 53};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4093: {
					String[] name_ = new String[] {"Cát lưu động", "Bão cát sa mạc", "Cát linh động"};
					short[] icon_ = new short[] {55, 54, 52};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4160: {
					String[] name_ = new String[] {"Sấm chớp rền vang", "Lôi phạt", "Bùng nổ sức mạnh", "Ý chí thần sấm"};
					short[] icon_ = new short[] {61, 60, 59, 58};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4161: {
					String[] name_ = new String[] {"Bão nham thạch", "Cột lửa", "Bùng cháy", "Nỗi đau bỏng cháy"};
					short[] icon_ = new short[] {65, 64, 63, 62};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4219: {
					String[] name_ = new String[] {"Sóng âm - Xung kích", "Hóa báo đốm", "Tia chớp"};
					short[] icon_ = new short[] {72, 71, 70};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4220: {
					String[] name_ = new String[] {"Cơn lốc - Ưng kích", "Hóa chim ưng", "Chim săn mồi"};
					short[] icon_ = new short[] {69, 68, 67};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4240: {
					String[] name_ = new String[] {"Bộc phá", "Vết nứt", "Kình lực", "Địa chấn"};
					short[] icon_ = new short[] {76, 75, 73, 74};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4316: {
					String[] name_ = new String[] {"Giáp sáp", "Đao không kích", "Lao sáp"};
					short[] icon_ = new short[] {79, 77, 78};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4317: {
					String[] name_ = new String[] {"Thân thể thép", "Ảo ảnh trảm", "Loạn trảm"};
					short[] icon_ = new short[] {82, 81, 80};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4318: {
					String[] name_ = new String[] {"Thần hộ thể", "Tăng trọng", "Sức nặng ngàn cân"};
					short[] icon_ = new short[] {85, 84, 83};
					Service.NewDialog_eat_taq(p, name_, icon_, (id - 4000));
					p.get_skill_taq_new(id - 4000);
					break;
				}
				case 4427: {
					// String[] name_ = new String[] {"Nét vẽ cường hóa", "Nét vẽ phòng thủ", "Nét vẽ sức mạnh"};
					// short[] icon_ = new short[] {51, 50, 49};
					// Service.NewDialog_eat_taq(p, name_, icon_, (id-4000));p.get_skill_taq_new(id-4000);
					break;
				}
				case 1: {
					if (p.item_to_kham_ngoc != null) {
						int vang_req = p.item_to_kham_ngoc.mdakham.length * 250;
						if (p.get_ngoc() < vang_req) {
							Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
							return;
						}
						p.update_ngoc(-vang_req);
						p.update_money();
						for (int i = 0; i < p.item_to_kham_ngoc.mdakham.length; i++) {
							ItemBag47 it = new ItemBag47();
							it.id = p.item_to_kham_ngoc.mdakham[i];
							it.category = 4;
							it.quant = 1;
							if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
								p.item.add_item_bag47(4, it);
							}
						}
						p.item_to_kham_ngoc.mdakham = new short[0];
						p.item_to_kham_ngoc.option_item_2.clear();
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
						Kham_Ngoc.show_table(p, 3);
						Service.send_box_ThongBao_OK(p, "Tháo thành công");
					} else {
						Kham_Ngoc.show_table(p, 3);
						Service.send_box_ThongBao_OK(p, "Có lỗi xảy ra, hãy thử lại");
					}
					break;
				}
				case 500: {
					if (p.trade_target != null && p.trade_target.trade_target.equals(p)) {
						Trade.show_table(p, p.trade_target.name);
						Trade.show_table(p.trade_target, p.name);
					} else {
						Service.send_box_ThongBao_OK(p, "Đối phương không online");
					}
					break;
				}
				case 0: {
					// p.use_item_3
					if (p.use_item_3 != -1) {
						Item_wear it = p.item.bag3[p.use_item_3];
						if (it != null && UseItem.check_it_can_wear(it.typeEquip)) {
							p.wear_item(it);
						}
					}
					break;
				}
			}
		} else { // hoi ren
			switch (id) {
				case 1: {
					Kham_Ngoc.show_table(p, 3);
					break;
				}
				case 500: {
					p.trade_target.trade_target = null;
					p.trade_target = null;
					break;
				}
				case 0: {
					p.use_item_3 = -1;
					break;
				}
			}
		}
	}
}
