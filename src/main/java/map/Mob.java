package map;

import java.util.HashMap;
import template.MobTemplate;

public class Mob {
	public final static HashMap<Integer, Mob> ENTRYS = new HashMap<>();
	public final static int TIME_RESPAWN = 20;
	public short x, y;
	public int hp, hp_max;
	public short level;
	public MobTemplate mob_template;
	public boolean isdie;
	public int id_target;
	public int index;
	public long time_skill;
	public long time_refresh;
	public Boss boss_info;
}
