package template;

import java.util.List;

public class Skill_Template {
	public static List<Skill_Template> ENTRYS;
	public short ID;
	public short indexSkillInServer;
	public short idIcon;
	public byte typeSkill;
	public byte typeBuff;
	public String name;
	public short range;
	public short typeEffSkill;
	public List<Option> op;
	public byte LvDevilSkill;
	public byte phanTramDevilSkill;
	public byte idEffSpec;
	public short perEffSpec;
	public short timeEffSpec;
	public byte Lv_RQ;
	public byte nTarget;
	public short rangeLan;
	public int damage;
	public short manaLost;
	public int timeDelay;
	public byte nKick;
	private String info;

	public String getInfo(int clazz) {
		switch (clazz) {
			case 2: {
				return info.replace("Quả đấm tốc độ", "Nhất kiếm");
			}
			case 3: {
				return info.replace("Quả đấm tốc độ", "Hắc cước");
			}
			case 4: {
				return info.replace("Quả đấm tốc độ", "Gậy chong chóng");
			}
			case 5: {
				return info.replace("Quả đấm tốc độ", "Double Shot");
			}
		}
		return info;
	}

	public short percentLv;
	public byte typeDevil;

	public Skill_Template(short Index, int Id, short IdImage, byte type, byte typeBuff, String name, short typeEff,
	      short range) {
		this.indexSkillInServer = Index;
		this.ID = (short) Id;
		this.idIcon = IdImage;
		this.typeSkill = type;
		this.typeBuff = typeBuff;
		this.name = name;
		this.range = range;
		this.typeEffSkill = typeEff;
	}
	// public Skill_Template(int clazz, int index_skill, int level_setup, short percent) {
	// int index = (clazz - 1) * 4 + index_skill;
	// Skill_Template temp = Skill_Template.ENTRYS[index];
	// this.indexSkillInServer = temp.indexSkillInServer;
	// this.ID = temp.ID;
	// this.idIcon = temp.idIcon;
	// this.typeSkill = temp.typeSkill;
	// this.typeBuff = temp.typeBuff;
	// this.name = temp.name;
	// this.range = temp.range;
	// this.typeEffSkill = temp.typeEffSkill;
	// //
	// this.nTarget = temp.nTarget;
	// this.rangeLan = temp.rangeLan;
	// this.damage = (temp.damage * (100 + level_setup * 50)) / 100;
	// this.manaLost = (temp.manaLost > 0) ? (short) (temp.manaLost + level_setup * 2) : 0;
	// this.timeDelay = temp.timeDelay;
	// this.nKick = temp.nKick;
	// this.info = temp.info;
	// this.Lv_RQ = (byte) level_setup;
	// this.percentLv = percent;
	// this.typeDevil = temp.typeDevil;
	// this.op = new ArrayList<>();
	// this.idEffSpec = temp.idEffSpec;
	// this.perEffSpec = temp.perEffSpec;
	// this.timeEffSpec = temp.timeEffSpec;
	// this.LvDevilSkill = temp.LvDevilSkill;
	// this.phanTramDevilSkill = temp.phanTramDevilSkill;
	// }

	public void getData(byte nTarget, short rangeLan, int Damage, short Manacost, int CoolDown, byte nkick,
	      String Description, int LvCur, int percentLv, byte typeDevil) {
		this.nTarget = nTarget;
		this.rangeLan = rangeLan;
		this.damage = Damage;
		this.manaLost = Manacost;
		this.timeDelay = CoolDown;
		this.nKick = nkick;
		this.info = Description;
		this.Lv_RQ = (byte) LvCur;
		this.percentLv = (short) percentLv;
		this.typeDevil = typeDevil;
	}

	public static Skill_Template get_temp(short index, long exp) {
		for (int i = 0; i < Skill_Template.ENTRYS.size(); i++) {
			Skill_Template temp = Skill_Template.ENTRYS.get(i);
			if (temp.indexSkillInServer == index) {
				if (exp == -1 && temp.Lv_RQ == -1) {
					return temp;
				} else if (exp > -1 && temp.Lv_RQ > -1) {
					return temp;
				}
			}
		}
		return null;
	}
        
//        public static Skill_Template get_temp(short index) {
//		for (int i = 0; i < Skill_Template.ENTRYS.size(); i++) {
//			if (Skill_Template.ENTRYS.get(i).indexSkillInServer == index &&
//                                Skill_Template.ENTRYS.get(i).Lv_RQ == -1) {
//					return Skill_Template.ENTRYS.get(i);
//				
//			}
//		}
//		return null;
//	}

	public static boolean upgrade_skill(Skill_info sk_info, byte clazz) {
		Skill_Template result = null;
		for (int i = 0; i < Skill_Template.ENTRYS.size(); i++) {
			Skill_Template temp_ss = Skill_Template.ENTRYS.get(i);
			if (sk_info.temp.ID == temp_ss.ID && temp_ss.Lv_RQ == (sk_info.temp.Lv_RQ + 1)) {
				switch (clazz) {
					case 1: {
						if (temp_ss.indexSkillInServer >= 0 && temp_ss.indexSkillInServer < 60
						      || temp_ss.indexSkillInServer >= 375 && temp_ss.indexSkillInServer < 395
                                                        || temp_ss.indexSkillInServer >= 566 && temp_ss.indexSkillInServer < 584) {
							result = temp_ss;
						}
						break;
					}
					case 2: {
						if (temp_ss.indexSkillInServer >= 60 && temp_ss.indexSkillInServer < 120
						      || temp_ss.indexSkillInServer >= 395 && temp_ss.indexSkillInServer < 415
                                                        || temp_ss.indexSkillInServer >= 584 && temp_ss.indexSkillInServer < 602) {
							result = temp_ss;
						}
						break;
					}
					case 3: {
						if (temp_ss.indexSkillInServer >= 120 && temp_ss.indexSkillInServer < 180
						      || temp_ss.indexSkillInServer >= 415 && temp_ss.indexSkillInServer < 435
                                                        || temp_ss.indexSkillInServer >= 602 && temp_ss.indexSkillInServer < 620) {
							result = temp_ss;
						}
						break;
					}
					case 4: {
						if (temp_ss.indexSkillInServer >= 180 && temp_ss.indexSkillInServer < 240
						      || temp_ss.indexSkillInServer >= 435 && temp_ss.indexSkillInServer < 455
                                                        || temp_ss.indexSkillInServer >= 620 && temp_ss.indexSkillInServer < 638) {
							result = temp_ss;
						}
						break;
					}
					case 5: {
						if (temp_ss.indexSkillInServer >= 240 && temp_ss.indexSkillInServer < 300
						      || temp_ss.indexSkillInServer >= 455 && temp_ss.indexSkillInServer < 475
                                                        || temp_ss.indexSkillInServer >= 638 && temp_ss.indexSkillInServer < 656) {
							result = temp_ss;
						}
						break;
					}
				}
				if (result != null) {
					break;
				}
			}
		}
		if (result != null && result.Lv_RQ > 0) {
			if (sk_info.temp.damage == result.damage || result.Lv_RQ > 25) {
				return false;
			} else {
				sk_info.temp = result;
				return true;
			}
		}
		return false;
	}

	public static boolean learn_skill(Skill_info sk_info) {
		if (sk_info.temp.Lv_RQ != -1) {
			return false;
		}
		Skill_Template result = null;
		for (int i = 0; i < Skill_Template.ENTRYS.size(); i++) {
			if (sk_info.temp.ID == Skill_Template.ENTRYS.get(i).ID && Skill_Template.ENTRYS.get(i).Lv_RQ == 1) {
				result = Skill_Template.ENTRYS.get(i);
				break;
			}
		}
		if (result != null) {
			sk_info.temp = result;
                        sk_info.exp =0;
			return true;
		}
		return false;
	}

	public static void reset_skill(Skill_info sk_info) {
		if (sk_info.temp.Lv_RQ == -1) {
			return;
		}
		for (int i = 0; i < Skill_Template.ENTRYS.size(); i++) {
			if (sk_info.temp.ID == Skill_Template.ENTRYS.get(i).ID && Skill_Template.ENTRYS.get(i).Lv_RQ == -1) {
				sk_info.temp = Skill_Template.ENTRYS.get(i);
				break;
			}
		}
	}
}
