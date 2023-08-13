package client;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import core.Log;
import core.Manager;
import core.Service;
import core.Util;
import database.SQL;
import io.Message;
import template.GiftTemplate;
import template.ItemBag47;
import template.ItemTemplate3;
import template.ItemTemplate4;
import template.ItemTemplate7;
import template.Item_wear;
import template.Level;

public class ClientInput {
	public static void process(Player p, Message m2) throws IOException {
		short id = m2.reader().readShort();
		String[] name = new String[m2.reader().readByte()];
		for (int i = 0; i < name.length; i++) {
			name[i] = m2.reader().readUTF();
		}
		switch (id) {
			case 3: {
				if (name.length == 1) {
					if (!Util.isnumber(name[0])) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int value = Integer.parseInt(name[0]);
					if (value < 10_000 || value > 2_000_000_000) {
						Service.send_box_ThongBao_OK(p, "Tối thiểu 10k, tối đa 2tỷ!");
						return;
					}
					if (p.update_coin(-value)) {
						p.update_ngoc(value / 5);
						p.update_money();
						Service.send_box_ThongBao_OK(p, "Thành công đổi " + value + " coin sang " + (value / 10) + " ruby");
					}
				}
				break;
			}
			case 2: {
				if (name.length == 2) {
					name[0] = name[0].replace(" ", "");
					name[1] = name[1].replace(" ", "");
					name[0] = name[0].toLowerCase();
					name[1] = name[1].toLowerCase();
					if (name[0].contains("admin") || name[1].contains("admin")) {
						Service.send_box_ThongBao_OK(p, "Tên tài khoản và mật khẩu không được trùng admin!");
						return;
					}
					Pattern pat = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
					if (!pat.matcher(name[0]).matches() || !pat.matcher(name[1]).matches()) {
						Service.send_box_ThongBao_OK(p,
						      "Tên tài khoản và mật khẩu phải dài hơn 6 và không chứa ký tự đặc biệt!");
						return;
					}
					Connection conn = null;
					Statement st = null;
					try {
						conn = SQL.gI().getCon();
						st = conn.createStatement();
						st.executeUpdate("UPDATE `accounts` SET `user` = '" + name[0] + "', `pass` = '" + name[1]
						      + "' WHERE BINARY `user` = '" + p.conn.user + "' AND BINARY `pass` = '" + p.conn.pass
						      + "' LIMIT 1;");
					} catch (SQLException e) {
						e.printStackTrace();
						Service.send_box_ThongBao_OK(p, "Tên đã được sử dụng, hãy thử lại!");
						return;
					} finally {
						try {
							if (st != null) {
								st.close();
							}
							if (conn != null) {
								conn.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					p.conn.user = name[0];
					p.conn.pass = name[1];
					Message m = new Message(-59);
					m.writer().writeUTF(name[0]);
					m.writer().writeUTF(name[1]);
					p.conn.addmsg(m);
					m.cleanup();
				}
				break;
			}
			case 1: {
				if (p.conn.kichhoat == 0) {
					// Service.send_box_ThongBao_OK(p, "Tài khoản trải nghiệm không thể điểm danh!");
					return;
				}
				if (name.length == 1) {
					Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{1,20}$");
					if (!pattern.matcher(name[0]).matches()) {
						Service.send_box_ThongBao_OK(p, "Ký tự không hợp lệ");
						return;
					}
					Service.send_box_ThongBao_OK(p, "Xin hãy đợi giây lát...");
					Connection conn = null;
					Statement st = null;
					ResultSet rs = null;
					GiftTemplate temp = null;
					try {
						conn = SQL.gI().getCon();
						st = conn.createStatement();
						rs = st.executeQuery("SELECT * FROM `giftcode` WHERE BINARY `giftname` = '" + name[0] + "' LIMIT 1;");
						if (!rs.next()) {
							Service.send_box_ThongBao_OK(p, "Giftcode không tồn tại hoặc đã được nhập");
							return;
						}
						temp = new GiftTemplate(rs.getString("giftname"), rs.getInt("luotnhap"), rs.getInt("gioihan"),
						      rs.getString("thongbao"), rs.getInt("beri"), rs.getInt("ruby"), rs.getString("item"),
						      rs.getString("used"), rs.getString("special"));
					} catch (SQLException e) {
						e.printStackTrace();
						Service.send_box_ThongBao_OK(p, "Có lỗi xảy ra hãy thử lại!");
						return;
					} finally {
						try {
							if (rs != null) {
								rs.close();
							}
							if (st != null) {
								st.close();
							}
							if (conn != null) {
								conn.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (temp != null) {
						if (temp.luotnhap >= temp.gioihan) {
							Service.send_box_ThongBao_OK(p, "Giftcode này đã đạt lượt nhập tối đa!");
							return;
						}
						if (!temp.used.isEmpty()) {
							String[] used_ = temp.used.split(",");
							for (int i = 0; i < used_.length; i++) {
								if (!used_[i].isBlank() && used_[i].equals(p.name)) {
									Service.send_box_ThongBao_OK(p, "Giftcode không tồn tại hoặc đã được nhập");
									return;
								}
							}
						}
						if (!temp.special.isEmpty()) { // quà chỉ dành cho 1 số acc
							boolean can_receiv = false;
							String[] used_ = temp.special.split(",");
							for (int i = 0; i < used_.length; i++) {
								if (!used_[i].isBlank() && used_[i].equals(p.name)) {
									can_receiv = true;
									break;
								}
							}
							if (!can_receiv) {
								Service.send_box_ThongBao_OK(p, "Bạn không có tên trong danh sách nhận giftcode này!");
								return;
							}
						}
					}
					GiftTemplate.update_used(temp, p.name);
					p.update_vang(temp.beri);
					p.update_ngoc(temp.ruby);
					p.update_money();
					if (temp.type != null) {
						for (int i = 0; i < temp.type.length; i++) {
							switch (temp.type[i]) {
								case 3: {
									Item_wear it_add = new Item_wear();
									it_add.setup_template_by_id(temp.id[i]);
									if (it_add.id > -1) {
										p.item.add_item_bag3(it_add);
									}
									break;
								}
								case 4:
								case 7: {
									ItemBag47 it = new ItemBag47();
									it.id = temp.id[i];
									it.category = temp.type[i];
									it.quant = temp.quant[i];
									if (p.item.able_bag() > 0
									      && (p.item.total_item_bag_by_id(temp.type[i], it.id) + it.quant) < 32_000) {
										p.item.add_item_bag47(temp.type[i], it);
									}
									break;
								}
							}
						}
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
					}
					String notice = "Bạn nhận được:" + "\nBeri : " + temp.beri + "\nRuby : " + temp.ruby + "\nItem : ";
					if (temp.type != null) {
						for (int i = 0; i < temp.type.length; i++) {
							switch (temp.type[i]) {
								case 3: {
									notice += ItemTemplate3.get_it_by_id(temp.id[i]).name + " x" + temp.quant[i] + ", ";
									break;
								}
								case 4: {
									notice += ItemTemplate4.get_it_by_id(temp.id[i]).name + " x" + temp.quant[i] + ", ";
									break;
								}
								case 7: {
									notice += ItemTemplate7.get_it_by_id(temp.id[i]).name + " x" + temp.quant[i] + ", ";
									break;
								}
							}
						}
					}
					Log.gI().add_log(p, "Nhập giftcode " + notice);
					notice += "\n" + temp.notice;
					Service.send_box_ThongBao_OK(p, notice);
				}
				break;
			}
			case 0: {
				if (name.length == 1) {
					if (!Util.isnumber(name[0])) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int value = Integer.parseInt(name[0]);
					if (value <= 0 || value > 32000 || (value + p.item.total_item_bag_by_id(4, 232)) > 32000) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int vang_req = value * ItemTemplate4.get_it_by_id(232).ruby;
					if (p.get_ngoc() < vang_req) {
						Service.send_box_ThongBao_OK(p, "Không đủ " + vang_req + " ruby");
						return;
					}
					p.update_ngoc(-vang_req);
					p.update_money();
					Log.gI().add_log(p, "Mua " + value + " vé vòng quay -" + vang_req + " ruby");
					ItemBag47 it = new ItemBag47();
					it.id = 232;
					it.category = 4;
					it.quant = (short) value;
					if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
						p.item.add_item_bag47(4, it);
						Service.send_box_ThongBao_OK(p, "Mua thành công " + value + " " + ItemTemplate4.get_item_name(it.id));
						p.item.update_Inventory(4, false);
						p.item.update_Inventory(7, false);
						p.item.update_Inventory(3, false);
					} else {
						Service.send_box_ThongBao_OK(p, "Hành trang không đủ chỗ trống!");
					}
				}
				break;
			}
			case 32002: {
				if (name.length == 3) {
					if (!Util.isnumber(name[0]) || !Util.isnumber(name[1]) || !Util.isnumber(name[2])) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int value1 = Integer.parseInt(name[0]);
					int value2 = Integer.parseInt(name[1]);
					int value3 = Integer.parseInt(name[2]);
					if (value3 <= 0 || value3 > 32000) {
						value3 = 1;
					}
					switch (value1) {
						case 3: {
							ItemTemplate3 temp = ItemTemplate3.get_it_by_id(value2);
							if (temp != null) {
								Item_wear it_add = new Item_wear();
								it_add.setup_template_by_id(temp.id);
								if (it_add.id > -1) {
									p.item.add_item_bag3(it_add);
								}
								p.item.update_Inventory(4, false);
								p.item.update_Inventory(7, false);
								p.item.update_Inventory(3, false);
								Service.send_box_ThongBao_OK(p, "Lấy thành công " + temp.name);
							}
							break;
						}
						case 4: {
							// for (int i = 0; i < ItemTemplate4.ENTRYS.size(); i++) {
							// ItemTemplate4 temp = ItemTemplate4.ENTRYS.get(i);
							// if (temp.type == 7) {
							// // ItemTemplate4 temp = ItemTemplate4.get_it_by_id(temppp);
							// if (temp != null) {
							// ItemBag47 it = new ItemBag47();
							// it.id = temp.id;
							// it.category = 4;
							// it.quant = (short) value3;
							// if (p.item.able_bag() > 0
							// && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
							// p.item.add_item_bag47(4, it);
							// p.item.update_Inventory(4, false);
							// p.item.update_Inventory(7, false);
							// p.item.update_Inventory(3, false);
							// Service.send_box_ThongBao_OK(p, "Lấy thành công " + temp.name);
							// }
							// }
							// }
							// }
							ItemTemplate4 temp = ItemTemplate4.get_it_by_id(value2);
							if (temp != null) {
								ItemBag47 it = new ItemBag47();
								it.id = temp.id;
								it.category = 4;
								it.quant = (short) value3;
								if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(4, it.id) + it.quant) < 32_000) {
									p.item.add_item_bag47(4, it);
									p.item.update_Inventory(4, false);
									p.item.update_Inventory(7, false);
									p.item.update_Inventory(3, false);
									Service.send_box_ThongBao_OK(p, "Lấy thành công " + temp.name);
								}
							}
							break;
						}
						case 7: {
							ItemTemplate7 temp = ItemTemplate7.get_it_by_id(value2);
							if (temp != null) {
								ItemBag47 it = new ItemBag47();
								it.id = temp.id;
								it.category = 7;
								it.quant = (short) value3;
								if (p.item.able_bag() > 0 && (p.item.total_item_bag_by_id(7, it.id) + it.quant) < 32_000) {
									p.item.add_item_bag47(7, it);
									p.item.update_Inventory(4, false);
									p.item.update_Inventory(7, false);
									p.item.update_Inventory(3, false);
									Service.send_box_ThongBao_OK(p, "Lấy thành công " + temp.name);
								}
							}
							break;
						}
					}
				}
				break;
			}
			case 32000: {
				if (name.length == 1) {
					if (!Util.isnumber(name[0])) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int value = Integer.parseInt(name[0]);
					if (value > Manager.gI().lvmax) {
						value = Manager.gI().lvmax;
					}
					if (value == 1) {
						value = 2;
					}
					p.level = (short) (value - 1);
					p.exp = Level.ENTRYS.get(p.level - 1).exp - 1;
					p.update_exp(1, false);
					p.reset_point(0);
				}
				break;
			}
			case 32001: {
				if (name.length == 1) {
					if (!Util.isnumber(name[0])) {
						Service.send_box_ThongBao_OK(p, "Số nhập không hợp lệ");
						return;
					}
					int value = Integer.parseInt(name[0]);
					if (value < 0 || value > 1000) {
						value = 1;
					}
					Manager.gI().exp = value;
					Service.send_box_ThongBao_OK(p, "Thay đổi xp x" + value);
				}
				break;
			}
		}
	}
}
