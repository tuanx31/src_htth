package template;

public class Option {
	public static int[] PAR_PER_LEVELUP = new int[] {100, 110, 120, 130, 140, 150, 170, 190, 210, 230, 250};
	public byte id;
	private int param;

	public Option(int id, int param) {
		this.id = (byte) id;
		this.param = param;
	}

	public int getParam(int tier) {
		int result = param;
		if (ItemOptionTemplate.ENTRYS.get(this.id).is_percent) {
			result = (result * ((PAR_PER_LEVELUP[tier] - 100) / 2 + 100)) / 100;
		} else {
			result = (result * PAR_PER_LEVELUP[tier]) / 100;
		}
		return result;
	}

	public void setParam(int param) {
		this.param = param;
	}
}
