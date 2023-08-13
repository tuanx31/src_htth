package template;

public class EffTemplate {
	// 0 eff + hp
	// 1 eff + mp
	// 2 x2cd
	// 3 x2 skill exp
	public int id;
	public int param;
	public long time;

	public EffTemplate(int id, int param, long time) {
		this.id = id;
		this.param = param;
		this.time = time;
	}

	public static boolean check_eff_can_save(int id) {
		return id == 2 || id == 3;
	}
}
