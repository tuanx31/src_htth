package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import activities.ChuyenHoa;
import activities.DailyQuest;
import activities.Kham_Ngoc;
import activities.UpgradeDevil;
import activities.UpgradeItem;
import activities.VongQuay;
import client.Player;
import database.SQL;
import io.Message;
import io.SessionManager;
import map.Map;
import map.Vgo;
import template.EffTemplate;
import template.ItemFashionP;
import template.Part;
import template.Skill_Template;
import template.Skill_info;

public class MenuController {
	public static void send_menu(Player p, Message m) throws IOException {
		short type = m.reader().readShort();
		switch (type) {
			case -100: {
				send_dynamic_menu(p, type, "Nico Robin", new String[] {"Xem thời gian x2 xp chiêu thức"}, null);
				break;
			}
			case -4: {
				send_dynamic_menu(p, type, "Gap", new String[] {"Học Skill", "Xóa nội tại"}, null);
				break;
			}
			case -79:
			case -61:
			case -49:
			case -40:
			case -32:
			case -24:
			case -16:
			case -8: {
				send_dynamic_menu(p, type, "Nhiệm vụ", new String[] {"Nhiệm vụ chính", "Nhiệm vụ lặp"}, null);
				break;
			}
			case -6: {
				Service.Send_UI_Shop(p, 99);
				break;
			}
			case -1: {
				if (p.conn.user.length() > 12 && p.conn.user.substring(0, 13).equals("htth_tuhoang_")) {
					send_dynamic_menu(p, type, "Trưởng làng",
					      new String[] {"Đăng ký tài khoản", "Hướng dẫn", "Điểm danh", "Đổi coin sang ruby"}, null);
				} else {
					send_dynamic_menu(p, type, "Trưởng làng", new String[] {"Hướng dẫn", "Điểm danh", "Đổi coin sang ruby"},
					      null);
				}
				break;
			}
			case -21:
			case -13: {
				send_dynamic_menu(p, type, get_name_npc(type), new String[] {"Top Cao Thủ", "Giftcode"}, null);
				break;
			}
			case -133: {
				send_dynamic_menu(p, type, "Thuyền trưởng Buggi", new String[] {"Vòng quay", "Mua thẻ", "Ai rảnh thì xem"},
				      null);
				break;
			}
			case -70:
			case -47: {
				send_dynamic_menu(p, type, "Johny",
				      new String[] {"Cường Hóa", "Khảm ngọc", "Chuyển Hóa", "Cường hóa ác quỷ"},
				      new short[] {126, 126, 126, 126});
				break;
			}
			case -76:
			case -68:
			case -46:
			case -39:
			case -29:
			case -22:
			case -14:
			case -2: {
				send_dynamic_menu(p, type, get_name_npc(type), new String[] {"Quán ăn", "Tiệm tóc", "Thời trang",
				      "Thẩm mỹ viện", "Tháo tóc", "Tháo thời trang", "Tháo mặt"},
				      new short[] {104, 106, 108, 158, 124, 124, 124});
				break;
			}
			case 0:
			case -12:
			case -20:
			case -28:
			case -36:
			case -44:
			case -60:
                        case -97:
                        case -85:
                        case -107:
                        case -124:
                        case -132:
                        case -144:
                         
                        case -5: {
				send_dynamic_menu(p, type, "Dịch chuyển",
				      new String[] {"Làng Cối Xay Gió", "Thị trấn Vỏ Sò", "Thị trấn Orang", "Làng Sirup", "Nhà hàng Barati",
				            "Làng hạt dẻ", "Thị trấn khởi đầu", "Thị Trấn Whiskay","Đảo Little Gardan","Thị Trấn Horn","Thị Trấn Nanohano",
                                      "Đảo Jaza","Thị Trấn Thiên Sứ","Kinh đô nước"},
				      new short[] {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128});
				break;
			}
			case -7: {
				Menu_Change_Zone(p);
				break;
			}
			case -75:
			case -69:
			case -38:
			case -30:
			case -23:
			case -15:
			case -3: {
				send_dynamic_menu(p, type, get_name_npc(type),
				      new String[] {"Võ Sĩ", "Kiếm Khách", "Đầu Bếp", "Hoa Tiêu", "Xạ Thủ", "Tắt hiển thị nón"},
				      new short[] {115, 119, 120, 121, 122, 124});
				break;
			}
			default: {
				send_dynamic_menu(p, type, (get_name_npc(type) + " Coming soon id " + type),
				      new String[] {"Tứ Hoàng Online", "Số 1 game private"}, new short[] {117, 117});
				break;
			}
		}
	}

	private static String get_name_npc(short type) {
		switch (type) {
			case -75:
				return "Ms Vivi";
			case -76:
				return "Mr Acrobatic";
			case -68:
				return "Sapie";
			case -46:
				return "Noziko";
			case -39:
				return "Cami";
			case -29:
				return "Kaiya";
			case -21:
				return "Thị Trưởng";
			case -13:
				return "Cobi";
			case -69:
				return "Masu";
			case -3:
				return "Guru";
			case -15:
				return "Mẹ Rita";
			case -23:
				return "Poroy";
			case -30:
				return "Merri";
			case -38:
				return "Partty";
			case -2:
				return "Machiko";
			case -14:
				return "Rita";
			case -22:
				return "Cho Cho";
		}
		return "NPC";
	}

	public static void process_menu(Player p, Message m2) throws IOException {
		if (!p.isdie) {
			short idNPC = m2.reader().readShort();
			byte idMenu = m2.reader().readByte();
			byte index = m2.reader().readByte();
			// System.out.println(idNPC);
			// System.out.println(idMenu);
			// System.out.println(index);
			switch (idNPC) {
				case -100: {
					EffTemplate eff = p.get_eff(3);
					if (eff != null) {
						Service.send_box_ThongBao_OK(p,
						      "Thời gian x2 xp chiếu thức hiện tại " + (eff.time - System.currentTimeMillis()) / 1000
						            + " s, xem lại tại npc nico robin làng cối xay gió");
					} else {
						Service.send_box_ThongBao_OK(p,
						      "Thời gian x2 xp chiếu thức hiện tại " + 0 + " s, xem lại tại npc nico robin làng cối xay gió");
					}
					break;
				}
				case 998: {
					Menu_Learn_Skill(p, index);
					break;
				}
				case -4: {
					Menu_Gap(p, index);
					break;
				}
				case 999: { // select mode
					if (p.quest_daily[4] > 0) {
						DailyQuest.get_quest(p, index);
					}
					break;
				}
				case 1000: {
					Menu_Quest_Daily(p, index);
					break;
				}
				case -79:
				case -61:
				case -49:
				case -40:
				case -32:
				case -24:
				case -16:
				case -8: {
					Menu_Quest(p, index);
					break;
				}
				case -1: {
					Menu_TruongLang(p, index);
					break;
				}
				case 120: { // bhx
					break;
				}
				case -13: {
					Menu_Cobi(p, index);
					break;
				}
				case -133: {
					Menu_Buggi(p, index);
					break;
				}
				case 9999: {
					Menu_Admin(p, index);
					break;
				}
				case 32002: {
					UpgradeDevil.show_table(p, index);
					break;
				}
				case 32001: {
					Menu_KhamNgoc(p, index);
					break;
				}
				case 32000: {
					Menu_Rebuilt_Item(p, index);
					break;
				}
				case -70:
				case -47: {
					Menu_Johny(p, index);
					break;
				}
				case -76:
				case -68:
				case -46:
				case -39:
				case -29:
				case -22: // menu cho cho
				case -14: // menu rita
				case -2: {
					Menu_Machiko(p, index);
					break;
				}
				case -75:
				case -69: // masu
				case -38: // menu partty
				case -30: // menu merri
				case -23: // menu poroy
				case -15: // Menu_MomRiTa
				case -3: {
					Menu_Guru(p, index);
					break;
				}
				case 0:
				case -12:
				case -20:
				case -28:
				case -36:
				case -44:
				case -60:
                                case -97:
                                case -85:
                                case -107:
                                case -124:
                                case -132:
                                case -144:
				case -5: {
					Menu_DichChuyen(p, index);
					break;
				}
			}
		}
	}

	private static void Menu_Learn_Skill(Player p, byte index) throws IOException {
		int dem = 0;
		for (int i = 0; i < p.skill_point.size(); i++) {
			Skill_info temp = p.skill_point.get(i);
			if (temp.temp.ID >= 1000 && temp.temp.ID < 2000 && temp.temp.typeSkill == 3 && temp.temp.Lv_RQ > 0) {
				dem++;
			}
		}
		if (dem >= p.numPassive) {
			Service.send_box_ThongBao_OK(p, "Không thể học thêm skill này, hãy tẩy bớt");
			return;
		}
		for (int i = 0; i < p.skill_point.size(); i++) {
			Skill_info temp = p.skill_point.get(i);
			if (temp.temp.ID >= 1000 && temp.temp.ID < 2000 && temp.temp.Lv_RQ == -1) {
				index--;
				if (index == -1) {
					if (Skill_Template.learn_skill(temp)) {
						p.send_skill();
						p.update_info_to_all();
						Service.send_box_ThongBao_OK(p, "Học Thành công " + temp.temp.name);
					} else {
						Service.send_box_ThongBao_OK(p, "Có lỗi xảy ra, hãy thử lại");
					}
					break;
				}
			}
		}
	}

	private static void Menu_Gap(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				List<String> str_ = new ArrayList<>();
				List<Integer> icon_ = new ArrayList<>();
				for (int i = 0; i < p.skill_point.size(); i++) {
					Skill_info temp = p.skill_point.get(i);
					if (temp.temp.ID >= 1000 && temp.temp.ID < 2000 && temp.temp.Lv_RQ == -1) {
						str_.add(temp.temp.name);
						icon_.add((int) (temp.temp.idIcon));
					}
				}
				if (str_.size() == 0) {
					str_.add("Hiện tại không có skill gì để học");
					icon_ = null;
				}
				send_dynamic_menu(p, 998, "Học kỹ năng", str_, icon_, 4);
				break;
			}
			case 1: {
				Service.send_box_yesno(p, 2, "Xác nhận dùng 5 ruby để tẩy skill bị động");
				break;
			}
		}
	}

	private static void Menu_Quest_Daily(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				String notice =
				      "Nhiệm vụ lặp: đánh quái ngẫu nhiên theo level, tối đa ngày nhận 100 nhiệm vụ, mỗi nhiệm vụ sẽ nhận được phần thưởng kinh nghiệm, ruby hoặc có cơ hội nhận được vật phẩm quý giá";
				Service.send_box_ThongBao_OK(p, notice);
				break;
			}
			case 1: {
				if (p.quest_daily[0] != -1) {
					Service.send_box_ThongBao_OK(p, "Đã nhận nhiệm vụ rồi!");
				} else {
					if (p.quest_daily[4] > 0) {
						send_dynamic_menu(p, 999, "Chọn độ khó",
						      new String[] {"Cực Dễ", "Bình thường", "Khó", "Khó không thể làm"}, null);
					} else {
						Service.send_box_ThongBao_OK(p, "Hôm nay đã hết lượt, quay lại vào ngày mai");
					}
				}
				break;
			}
			case 2: {
				DailyQuest.remove_quest(p);
				break;
			}
			case 3: {
				DailyQuest.finish_quest(p);
				break;
			}
			case 4: {
				Service.send_box_ThongBao_OK(p, DailyQuest.info_quest(p));
				break;
			}
		}
	}

	private static void Menu_Quest(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				Service.send_box_ThongBao_OK(p, "Sắp ra mắt!");
				break;
			}
			case 1: {
				send_dynamic_menu(p, 1000, "Nhiệm vụ lặp",
				      new String[] {"Hướng dẫn", "Nhận nhiệm vụ", "Hủy nhiệm vụ", "Trả nhiệm vụ", "Kiểm tra"}, null);
				break;
			}
		}
	}

	private static void Menu_TruongLang(Player p, byte index) throws IOException {
		if (!(p.conn.user.length() > 12 && p.conn.user.substring(0, 13).equals("htth_tuhoang_"))) {
			index++;
		}
		switch (index) {
			case 0: {
				if (p.level > 10) {
					Service.input_text(p, 2, "Nhập thông tin", new String[] {"Tên tài khoản", "Mật khẩu"});
				} else {
					Service.send_box_ThongBao_OK(p, "Hãy luyện tập khi level 11 hãy quay lại đây!");
				}
				break;
			}
			case 1: {
				Service.send_box_ThongBao_OK(p, "Chào mừng đến với server Tứ Hoàng Online!");
				break;
			}
			case 2: {
				if (p.conn.kichhoat == 0) {
					Service.send_box_ThongBao_OK(p, "Tài khoản trải nghiệm không thể điểm danh!");
					return;
				}
				if (p.quest_daily[5] == 1) {
					p.quest_daily[5] = 0;
					int beri = Util.random(20_000, 25_000);
					int ruby = Util.random(1, 100);
					p.update_vang(beri);
					p.update_ngoc(ruby);
					p.update_money();
					Service.send_box_ThongBao_OK(p, "Thành công nhận được " + beri + " beri và " + ruby + " ruby!");
				} else {
					Service.send_box_ThongBao_OK(p, "Hôm nay đã điểm danh rồi!");
				}
				break;
			}
			case 3: {
				Service.input_text(p, 3, "Đổi coin", new String[] {"Nhập coin đổi: (10k coin = 1000 ruby)"});
				break;
			}
		}
	}

	private static void Menu_Cobi(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				// List<BXH> list = new ArrayList<>();
				// for (Map[] mapall : Map.ENTRYS) {
				// for (Map map : mapall) {
				// for (int i = 0; i < map.players.size(); i++) {
				// Player p0 = map.players.get(i);
				// list.add(new BXH(p0.name, p0.level));
				// }
				// }
				// }
				// Util.sort_collections(list);
				// Collections.sort(list, new Comparator<BXH>() {
				// @Override
				// public int compare(BXH o1, BXH o2) {
				// return o1.level >= o2.level ? -1 : 1;
				// }
				// });
				// String[] result;
				// if (list.size() < 10) {
				// result = new String[list.size()];
				// } else {
				// result = new String[10];
				// }
				// for (int i = 0; i < result.length; i++) {
				// result[i] = "Top " + (i + 1) + " : " + list.get(i).name + " : lv " + list.get(i).level;
				// }
				// send_dynamic_menu(p, 120, "BXH", result, null);
				break;
			}
			case 1: {
				if (p.conn.kichhoat == 0) {
					Service.send_box_ThongBao_OK(p, "Tài khoản trải nghiệm không thể điểm danh!");
					return;
				}
				Service.input_text(p, 1, "Quà tặng máy chủ", new String[] {"Nhập giftcode"});
				break;
			}
		}
	}

	private static void Menu_Buggi(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				VongQuay.show_table(p);
				break;
			}
			case 1: {
				Service.send_box_ThongBao_OK(p, "Vào vòng xoay mua!");
				break;
			}
			case 2: {
				Service.send_box_ThongBao_OK(p, "Trò chơi chỉ mang tính chất giải trí!");
				break;
			}
		}
	}

	private static void Menu_KhamNgoc(Player p, byte index) throws IOException {
		switch (index) {
			case 0:
			case 1:
			case 2:
			case 3: {
				Kham_Ngoc.show_table(p, index);
				break;
			}
			case 4: {
				Service.Send_UI_Shop(p, 111);
				break;
			}
		}
	}

	private static void Menu_Admin(Player p, byte index) throws IOException {
		if (p.conn.user.equals("admin")) {
			switch (index) {
				case 0: {
					new Thread(() -> {
						synchronized (SessionManager.CLIENT_ENTRYS) {
							System.out.println("START CLOSE SERVER");
							SaveData.process();
							try {
								ServerManager.gI().close();
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(0);
							}
							for (int i = SessionManager.CLIENT_ENTRYS.size() - 1; i >= 0; i--) {
								SessionManager.CLIENT_ENTRYS.get(i).p = null;
								SessionManager.CLIENT_ENTRYS.get(i).disconnect();
							}
							System.out.println("WAIT TO SHUTDOWN SERVICE");
							try {
								Thread.sleep(5000L);
							} catch (InterruptedException e) {
								e.printStackTrace();
								System.exit(0);
							}
							Manager.gI().close();
							SQL.gI().close();
							System.out.println("CLOSE SERVER");
						}
					}).start();
					break;
				}
				case 1: {
					synchronized (SessionManager.CLIENT_ENTRYS) {
						for (int i = SessionManager.CLIENT_ENTRYS.size() - 1; i >= 0; i--) {
							if (SessionManager.CLIENT_ENTRYS.get(i).p == null) {
								SessionManager.CLIENT_ENTRYS.get(i).disconnect();
							}
						}
					}
					Service.send_box_ThongBao_OK(p, "OK");
					break;
				}
				case 2: {
					p.update_vang(1_000_000_000);
					p.update_ngoc(1_000_000_000);
					p.update_money();
					break;
				}
				case 3: {
					Service.input_text(p, 32000, "Uplevel", new String[] {"Nhập level"});
					break;
				}
				case 4: {
					Service.input_text(p, 32001, "SetXP", new String[] {"Nhập mức"});
					break;
				}
				case 5: {
					Service.input_text(p, 32002, "Get Item", new String[] {"Type item", "Id item", "Số lượng"});
					break;
				}
				case 6: {
					if (Part.update()) {
						Service.send_box_ThongBao_OK(p, "Thành công");
					} else {
						Service.send_box_ThongBao_OK(p, "Thất bại");
					}
					break;
				}
				case 7: {
					SaveData.process();
					Service.send_box_ThongBao_OK(p, "Thành công");
					break;
				}
			}
		}
	}

	private static void Menu_Rebuilt_Item(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				// Service.Send_UI_Shop(p, index);
				UpgradeItem.show_table_upgrade(p);
				break;
			}
			case 1: {
				Service.Send_UI_Shop(p, 21);
				break;
			}
		}
	}

	private static void Menu_Johny(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				send_dynamic_menu(p, 32000, "Cường Hóa", new String[] {"Cường Hóa Đồ", "Shop nguyên liệu"},
				      new short[] {126, 126});
				break;
			}
			case 1: {
				send_dynamic_menu(p, 32001, "Khảm Ngọc",
				      new String[] {"Đục lỗ", "Ghép đá", "Khảm Ngọc", "Tách đá", "Shop nguyên liệu"},
				      new short[] {126, 126, 126, 126, 126});
				break;
			}
			case 2: {
				ChuyenHoa.show_table(p);
				break;
			}
			case 3: {
				send_dynamic_menu(p, 32002, "Cường hóa ác quỷ", new String[] {"Rương ác quỷ", "Kỹ năng"}, null);
				break;
			}
		}
	}

	private static void Menu_Machiko(Player p, byte index) throws IOException {
		switch (index) {
			case 0: {
				Service.Send_UI_Shop(p, 20);
				break;
			}
			case 1: {
				ItemFashionP.show_table(p, 103);
				break;
			}
			case 2: {
				ItemFashionP.show_table(p, 105);
				break;
			}
			case 3: {
				ItemFashionP.show_table(p, 108);
				break;
			}
			case 4: {
				p.remove_hairf();
				break;
			}
			case 5: {
				p.remove_fashion();
				break;
			}
			case 6: {
				p.remove_headf();
				break;
			}
			case 7: {
				break;
			}
		}
	}

	private static void Menu_Guru(Player p, int index) throws IOException {
		switch (index) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4: {
				Service.Send_UI_Shop(p, index);
				break;
			}
		}
	}

	private static void Menu_Change_Zone(Player p) throws IOException {
		Message m = new Message(23);
		m.writer().writeByte(p.map.max_zone);
		Map[] map_ = Map.get_map_by_id(p.map.id);
		for (int i = 0; i < map_.length; i++) {
			int s = map_[i].players.size();
			int max = map_[i].max_player;
			m.writer().writeByte((s == max) ? 2 : ((s > (max / 2)) ? 1 : 0));
		}
		p.conn.addmsg(m);
		m.cleanup();
	}

	private static void Menu_DichChuyen(Player p, byte index) throws IOException {
		Vgo vgo = null;
		switch (index) {
			case 0: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(1)[0].name;
				vgo.xnew = 782;
				vgo.ynew = 203;
				break;
			}
			case 1: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(9)[0].name;
				vgo.xnew = 159;
				vgo.ynew = 245;
				break;
			}
			case 2: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(17)[0].name;
				vgo.xnew = 376;
				vgo.ynew = 210;
				break;
			}
			case 3: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(25)[0].name;
				vgo.xnew = 376;
				vgo.ynew = 210;
				break;
			}
			case 4: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(33)[0].name;
				vgo.xnew = 376;
				vgo.ynew = 210;
				break;
			}
			case 5: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(41)[0].name;
				vgo.xnew = 376;
				vgo.ynew = 210;
				break;
			}
			case 6: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(49)[0].name;
				vgo.xnew = 376;
				vgo.ynew = 210;
				break;
			}
			case 7: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(69)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 8: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(79)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 9: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(83)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 10: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(93)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 11: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(107)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 12: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(113)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}case 13: {
				vgo = new Vgo();
				vgo.name_map_goto = Map.get_map_by_id(191)[0].name;
				vgo.xnew = 292;
				vgo.ynew = 210;
				break;
			}
		}
		if (vgo != null) {
			p.change_map(vgo);
		}
	}

	public static void send_dynamic_menu(Player p, int id_npc, String name_npc, String[] list_menu, short[] list_icon)
	      throws IOException {
		if (!p.isdie) {
			Message m = new Message(-20);
			if (list_icon == null) {
				m.writer().writeByte(0);
			} else {
				m.writer().writeByte(5);
			}
			m.writer().writeShort(id_npc);
			m.writer().writeByte(0);
			m.writer().writeUTF(name_npc);
			m.writer().writeByte(list_menu.length);
			for (int i = 0; i < list_menu.length; i++) {
				m.writer().writeUTF(list_menu[i]);
				if (list_icon != null) {
					m.writer().writeShort(list_icon[i]);
				}
			}
			p.conn.addmsg(m);
			m.cleanup();
		}
	}

	private static void send_dynamic_menu(Player p, int id_npc, String name_npc, List<String> list_menu,
	      List<Integer> list_icon, int type_icon) throws IOException {
		if (!p.isdie) {
			Message m = new Message(-20);
			if (list_icon == null) {
				m.writer().writeByte(0);
			} else {
				m.writer().writeByte(type_icon);
			}
			m.writer().writeShort(id_npc);
			m.writer().writeByte(0);
			m.writer().writeUTF(name_npc);
			m.writer().writeByte(list_menu.size());
			for (int i = 0; i < list_menu.size(); i++) {
				m.writer().writeUTF(list_menu.get(i));
				if (list_icon != null) {
					m.writer().writeShort(list_icon.get(i));
				}
			}
			p.conn.addmsg(m);
			m.cleanup();
		}
	}
}
