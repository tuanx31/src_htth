package template;

public class UpgradeMaterialTemplate {
	public byte type;
	public byte id;
	public short quant;

	public UpgradeMaterialTemplate(byte type, byte id, short quant) {
		this.type = type;
		this.id = id;
		this.quant = quant;
	}
}
