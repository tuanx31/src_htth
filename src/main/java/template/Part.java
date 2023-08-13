package template;

import java.util.ArrayList;
import java.util.List;

public class Part {
	public static List<Part> ENTRYS = new ArrayList<>();
	public short id;
	public short part;

	public Part(short id, short part) {
		this.id = id;
		this.part = part;
	}

	public static short get_part(short id) {
		for (int i = 0; i < Part.ENTRYS.size(); i++) {
			if (Part.ENTRYS.get(i).id == id) {
				return Part.ENTRYS.get(i).part;
			}
		}
		return -1;
	}

	public static boolean update() {
		boolean check = false;
		// Part.entry.clear();
		// try (Connection conn = SQL.gI().getCon();
		// Statement st = conn.createStatement();
		// ResultSet rs = st.executeQuery("SELECT * FROM `parts`;")) {
		// while (rs.next()) {
		// Part.entry.add(new Part(rs.getShort("id_part"), rs.getShort("part")));
		// }
		// check = true;
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// ItemTemplate3.update();
		return check;
	}
}
