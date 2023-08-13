package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import client.Item;
import client.MessageHandler;
import client.Player;
import core.Service;
import core.Util;
import database.SQL;
import template.ItemFashionP;
import template.ItemFashionP2;
import template.ItemTemplate3;
import template.Item_wear;

public class Session implements Runnable {
	private static final byte[] KEYS = "vietvan@".getBytes();
	private final Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Thread sendd;
	private Thread receiv;
	public boolean connected;
	private final BlockingQueue<Message> list_msg;
	private boolean sendKeyComplete;
	private byte curR;
	private byte curW;
	public String user;
	public String pass;
	public String ip;
	private final MessageHandler controller;
	public Player p;
	public List<String> list_char;
	public byte zoomlv;
	public boolean get_in4;
	public String version;
	public byte lock;
	public byte kichhoat;

	public Session(Socket socket) {
		this.socket = socket;
		this.list_msg = new LinkedBlockingQueue<Message>();
		this.sendKeyComplete = false;
		this.connected = false;
		this.controller = new MessageHandler(this);
	}

	public void init() {
		try {
			this.ip = this.socket.getInetAddress().getHostAddress();
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.sendd = new Thread(() -> {
				try {
					while (connected) {
						Message m = list_msg.poll(5, TimeUnit.SECONDS);
						if (m != null) {
							send_msg(m);
							m.cleanup();
						}
					}
				} catch (InterruptedException e) {
				} catch (IOException e) {
				} finally {
					this.disconnect();
				}
			});
			this.receiv = new Thread(this);
			this.connected = true;
			this.get_in4 = false;
			//
			this.receiv.start();
			this.sendd.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		SessionManager.client_disconnect(this);
	}

	public void addmsg(Message m) {
		if (this.connected) {
			this.list_msg.add(m);
		}
	}

	@Override
	public void run() {
		try {
			while (this.connected) {
				Message m = read_msg();
				if (m != null) {
					// if (this.ip.equals("127.0.0.1") && m.cmd == -1) {
					// } else
					if (m.cmd == -27) {
						sendkeys();
					} else if (sendKeyComplete) {
						try {
							controller.process_msg(m);
						} catch (NullPointerException e) {
//							e.printStackTrace();
						}
					}
					m.cleanup();
				}
			}
		} catch (IOException e) {
		} finally {
			disconnect();
		}
	}

	private void send_msg(Message msg) throws IOException {
		byte[] data = msg.getData();
		if (sendKeyComplete) {
			byte b = writeKey(msg.cmd);
			dos.writeByte(b);
		} else {
			dos.writeByte(msg.cmd);
		}
		if (data != null) {
			int size = data.length;
			if (sendKeyComplete) {
				if ((msg.cmd == -39) || msg.cmd == -101 || msg.cmd == -93 || msg.cmd == 76) {
					dos.writeByte(writeKey((byte) (size >> 24)));
					dos.writeByte(writeKey((byte) (size >> 16)));
					dos.writeByte(writeKey((byte) (size >> 8)));
					dos.writeByte(writeKey((byte) (size)));
				} else {
					int byte1 = writeKey((byte) (size >> 8));
					dos.writeByte(byte1);
					int byte2 = writeKey((byte) (size));
					dos.writeByte(byte2);
				}
			} else if (msg.cmd == -39) {
				dos.writeInt(size);
			} else {
				final int byte1 = (byte) (size & 0xFF00);
				this.dos.writeByte(byte1);
				final int byte2 = (byte) (size & 0xFF);
				this.dos.writeByte(byte2);
			}
			if (sendKeyComplete) {
				for (int i = 0; i < data.length; i++) {
					data[i] = writeKey(data[i]);
				}
			}
			dos.write(data);
		} else {
			this.dos.writeShort(0);
		}
		dos.flush();
		msg.cleanup();
		Util.logconsole("___send msg : " + msg.cmd + " - size : " + data.length + " : " + user, 1, msg.cmd);
	}

	private Message read_msg() throws IOException {
		byte cmd = dis.readByte();
		if (sendKeyComplete) {
			cmd = readKey(cmd);
		}
		int size;
		if (sendKeyComplete) {
			byte b1 = dis.readByte();
			byte b2 = dis.readByte();
			size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
		} else {
			size = dis.readShort();
		}
		byte data[] = new byte[size];
		int len = 0;
		int byteRead = 0;
		while (len != -1 && byteRead < size) {
			len = dis.read(data, byteRead, size - byteRead);
			if (len > 0) {
				byteRead += len;
			}
		}
		if (sendKeyComplete) {
			for (int i = 0; i < data.length; i++) {
				data[i] = readKey(data[i]);
			}
		}
		Util.logconsole("Read msg : " + cmd + " - size : " + data.length, 0, cmd);
		return new Message(cmd, data);
	}

	private byte readKey(final byte b) {
		final byte curR = this.curR;
		this.curR = (byte) (curR + 1);
		final byte i = (byte) ((KEYS[curR] & 0xFF) ^ (b & 0xFF));
		if (this.curR >= KEYS.length) {
			this.curR %= (byte) KEYS.length;
		}
		return i;
	}

	private byte writeKey(final byte b) {
		final byte curW = this.curW;
		this.curW = (byte) (curW + 1);
		final byte i = (byte) ((KEYS[curW] & 0xFF) ^ (b & 0xFF));
		if (this.curW >= KEYS.length) {
			this.curW %= (byte) KEYS.length;
		}
		return i;
	}

	public void sendkeys() throws IOException {
		Message msg = new Message(-27);
		msg.writer().writeByte(KEYS.length);
		msg.writer().writeByte(KEYS[0]);
		for (int i = 1; i < KEYS.length; i++) {
			msg.writer().writeByte(KEYS[i] ^ KEYS[i - 1]);
		}
		send_msg(msg);
		msg.cleanup();
		sendKeyComplete = true;
	}

	public void request_data_update(Message m) throws IOException {
		byte type = m.reader().readByte();
		Message m2 = new Message(-7);
		switch (type) {
			case 3: {
				if (p != null && p.clazz >= 1 && p.clazz <= 5) {
					m2.writer().writeByte(3);
					// m2.writer().write(Util.loadfile("data/msg/login/request/msg-7_3_clazz_" + p.clazz));
					this.p.write_data_skill(m2.writer());
					addmsg(m2);
					m2.cleanup();
					//
					m2 = new Message(-7);
					m2.writer().writeByte(27);
					m2.writer().writeByte(0);
				}
				break;
			}
			case 17: {
				m2.writer().writeByte(17);
				m2.writer().writeLong(System.currentTimeMillis());
				break;
			}
			case 29: {
				m2.writer().write(Util.loadfile("data/msg/login/request/msg-7_29"));
				break;
			}
			default: {
				m2.writer().writeByte(type);
				m2.writer().write(Util.loadfile("data/msg/login/request/msg-7_" + type));
				break;
			}
		}
		this.addmsg(m2);
		m2.cleanup();
	}

	public void send_data_from_server(Message m) throws IOException {
		if (this.version == null) {
		}
		this.zoomlv = m.reader().readByte();
		Thread send = new Thread(() -> {
			try {
				String path = "data/datafromsver/x" + this.zoomlv;
				File folder = new File(path);
				if (folder.isDirectory()) {
					File[] files = folder.listFiles();
					Arrays.sort(files, new Comparator<File>() {
						@Override
						public int compare(File o1, File o2) {
							int name1 = solve_name(o1.getName());
							int name2 = solve_name(o2.getName());
							return (name1 > name2) ? 1 : -1;
						}

						private int solve_name(String name) {
							String num = "";
							for (int i = 0; i < name.length(); i++) {
								if (name.charAt(i) == '_') {
									break;
								}
								num += name.charAt(i);
							}
							return Integer.parseInt(num);
						}
					});
					for (int i = 0; i < files.length; i++) {
						int cmd = Integer.parseInt(
						      files[i].getName().substring((files[i].getName().length() - 3), files[i].getName().length()));
						Service.send_msg_data(this, cmd, files[i].getAbsolutePath(), false);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		send.start();
	}

	public void login(Message m) throws IOException {
		byte type = m.reader().readByte();
		String user_ = m.reader().readUTF().replace(" ", "");
		String pass_ = m.reader().readUTF().replace(" ", "");
//                if (!user_.equals("admin")) {
//                    return;
//                }
		this.lock = 0;
		this.kichhoat = 0;
		if (type == 1) {
			// login_notice("không hỗ trợ chơi mới");
			// return;
		}
		list_char = new ArrayList<>();
		if (type == 0) {
			Pattern p = Pattern.compile("^[a-zA-Z0-9@.]{1,20}$");
			if (!p.matcher(user_).matches() || !p.matcher(pass_).matches()) {
				login_notice("Ký tự không hợp lệ");
				return;
			}
			if (SessionManager.time_login.containsKey(user_) && !user_.equals("admin")) {
				long time_login = SessionManager.time_login.get(user_);
				if (time_login > System.currentTimeMillis()) {
					login_notice("Đăng nhập lại sau " + (time_login - System.currentTimeMillis()) / 1000 + "s");
					return;
				}
			}
			//
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				conn = SQL.gI().getCon();
				st = conn.createStatement();
				rs = st.executeQuery("SELECT * FROM `accounts` WHERE BINARY `user` = '" + user_ + "' AND BINARY `pass` = '"
				      + pass_ + "' LIMIT 1;");
				if (!rs.next()) {
					login_notice("Tài khoản mật khẩu không chính xác");
					return;
				}
				this.lock = rs.getByte("lock");
				this.kichhoat = rs.getByte("kichhoat");
				if (this.lock == 1) {
					login_notice("Tài khoản bị khóa, liên hệ admin để biết thêm chi tiết");
					return;
				}
				//
				JSONArray js = (JSONArray) JSONValue.parse(rs.getString("char"));
				for (int i = 0; i < js.size(); i++) {
					list_char.add(js.get(i).toString());
				}
			} catch (SQLException e) {
				e.printStackTrace();
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
		} else {
			if (user_.equals("") && pass_.equals("")) {
				user_ = "htth_tuhoang_" + System.nanoTime();
				pass_ = "1";
				Connection conn = null;
				Statement st = null;
				try {
					conn = SQL.gI().getCon();
					st = conn.createStatement();
					st.execute("INSERT INTO `accounts` (`user`, `pass`, `coin`, `kichhoat`, `lock`) VALUES ('" + user_
					      + "', '1', 0, 0, 0)");
				} catch (SQLException e) {
					e.printStackTrace();
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
				Message m2 = new Message(-57);
				m2.writer().writeUTF(user_);
				addmsg(m2);
				m2.cleanup();
			} else {
				pass_ = "1";
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				try {
					conn = SQL.gI().getCon();
					st = conn.createStatement();
					rs = st.executeQuery("SELECT * FROM `accounts` WHERE BINARY `user` = '" + user_
					      + "' AND BINARY `pass` = '" + pass_ + "' LIMIT 1;");
					if (!rs.next()) {
						login_notice("Tài khoản mật khẩu không chính xác");
						return;
					}
					this.lock = rs.getByte("lock");
					this.kichhoat = rs.getByte("kichhoat");
					if (this.lock == 1) {
						login_notice("Tài khoản bị khóa, liên hệ admin để biết thêm chi tiết");
						return;
					}
					//
					JSONArray js = (JSONArray) JSONValue.parse(rs.getString("char"));
					for (int i = 0; i < js.size(); i++) {
						list_char.add(js.get(i).toString());
					}
				} catch (SQLException e) {
					e.printStackTrace();
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
			}
		}
		//
		zoomlv = m.reader().readByte();
		this.version = m.reader().readUTF();
		byte device = m.reader().readByte();
		byte IndexCharSelected = m.reader().readByte();
		String loginplus = m.reader().readUTF();
		m.reader().readUTF();
		//
		// System.out.println(type);
		// System.out.println(user);
		// System.out.println(pass);
		// System.out.println(zoomlv);
		// System.out.println(version);
		// System.out.println(device);
		// System.out.println(IndexCharSelected);
		// System.out.println(loginplus);
		//
		this.user = user_;
		this.pass = pass_;
		if (!this.check_onl()) {
			this.update_onl(1);
		} else {
			this.disconnect();
			for (int i = 0; i < SessionManager.CLIENT_ENTRYS.size(); i++) {
				Session ss = SessionManager.CLIENT_ENTRYS.get(i);
				if (ss.user != null && ss.user.equals(this.user)) {
					ss.disconnect();
				}
			}
			login_notice("Tài khoản đang đăng nhập máy khác!");
			return;
		}
		Service.send_msg_data(this, 72, "data/msg/login/x2msg_72_638026480839986666", false);
		Service.send_msg_data(this, 72, "data/msg/login/x2msg_72_638026480840482549", false);
		Service.send_msg_data(this, 72, "data/msg/login/x2msg_72_638026480840808702", false);
		//
		send_list_char();
		if (this.zoomlv < 2) {
			Message m22 = new Message(-7);
			m22.writer().writeByte(28);
			m22.writer().write(Util.loadfile("data/msg/login/request/msg-7_28"));
			this.addmsg(m22);
			m22.cleanup();
			//
			m22 = new Message(-7);
			m22.writer().writeByte(15);
			m22.writer().write(Util.loadfile("data/msg/login/request/msg-7_15"));
			this.addmsg(m22);
			m22.cleanup();
		}
		// register new acc
		// Message m2 = new Message(-59);
		// m2.writer().writeUTF(this.user);
		// m2.writer().writeUTF(this.pass);
		// addmsg(m2);
		// m2.cleanup();
	}

	private void send_list_char() throws IOException {
		Message m2 = new Message(-4);
		m2.writer().writeByte(list_char.size());
		for (int i = 0; i < list_char.size(); i++) {
			String name = list_char.get(i);
			Connection connection = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				connection = SQL.gI().getCon();
				ps = connection.prepareStatement(
				      "SELECT `clazz`, `level`, `body`, `it_body`, `fashion` FROM `players` WHERE `name` = '" + name
				            + "' LIMIT 1;");
				rs = ps.executeQuery();
				while (rs.next()) {
					List<ItemFashionP2> fashion = new ArrayList<>();
					List<ItemFashionP> itfashionP = new ArrayList<>();
					JSONArray js0 = (JSONArray) JSONValue.parse(rs.getString("fashion"));
					JSONArray js_temp_2 = (JSONArray) JSONValue.parse(js0.get(0).toString());
					for (int i0 = 0; i0 < js_temp_2.size(); i0++) {
						JSONArray js_temp = (JSONArray) JSONValue.parse(js_temp_2.get(i0).toString());
						ItemFashionP tempf = new ItemFashionP();
						tempf.category = Byte.parseByte(js_temp.get(0).toString());
						tempf.id = Short.parseShort(js_temp.get(1).toString());
						tempf.icon = Short.parseShort(js_temp.get(2).toString());
						tempf.is_use = Byte.parseByte(js_temp.get(3).toString()) == 1;
						itfashionP.add(tempf);
					}
					js_temp_2.clear();
					js_temp_2 = (JSONArray) JSONValue.parse(js0.get(1).toString());
					for (int i0 = 0; i0 < js_temp_2.size(); i0++) {
						JSONArray js_temp = (JSONArray) JSONValue.parse(js_temp_2.get(i0).toString());
						ItemFashionP2 tempf = new ItemFashionP2();
						tempf.id = Short.parseShort(js_temp.get(0).toString());
						tempf.is_use = Byte.parseByte(js_temp.get(1).toString()) == 1;
						tempf.data = new short[js_temp.size() - 2];
						for (int j = 2; j < js_temp.size(); j++) {
							tempf.data[j - 2] = Short.parseShort(js_temp.get(j).toString());
						}
						fashion.add(tempf);
					}
					js0.clear();
					short hair_ = -1;
					short head_ = -1;
					short[] fashion_ = null;
					for (int i0 = 0; i0 < fashion.size(); i0++) {
						if (fashion.get(i0).is_use) {
							fashion_ = fashion.get(i0).data;
						}
					}
					if (fashion_ != null && fashion_[6] != -1) {
						hair_ = -2;
						head_ = fashion_[6];
					} else {
						for (int i0 = 0; i0 < itfashionP.size(); i0++) {
							if (itfashionP.get(i0).category == 103 && itfashionP.get(i0).is_use) {
								hair_ = itfashionP.get(i0).icon;
							}
						}
						for (int i0 = 0; i0 < itfashionP.size(); i0++) {
							if (itfashionP.get(i0).category == 108 && itfashionP.get(i0).is_use) {
								head_ = itfashionP.get(i0).icon;
							}
						}
					}
					//
					m2.writer().writeShort(i);
					m2.writer().writeUTF(name);
					m2.writer().writeByte(rs.getByte("clazz"));
					m2.writer().writeShort(rs.getShort("level"));
					JSONArray js = (JSONArray) JSONValue.parse(rs.getString("body"));
					m2.writer().writeShort((head_ != -1) ? head_ : Short.parseShort(js.get(0).toString()));
					m2.writer().writeShort((hair_ != -1) ? hair_ : Short.parseShort(js.get(1).toString()));
					js.clear();
					m2.writer().writeShort(-1); // clan
					m2.writer().writeByte(6);
					//
					Item_wear[] it = new Item_wear[8];
					js = (JSONArray) JSONValue.parse(rs.getString("it_body"));
					for (int i1 = 0; i1 < js.size(); i1++) {
						JSONArray js2 = (JSONArray) JSONValue.parse(js.get(i1).toString());
						Item_wear temp = new Item_wear();
						Item.readUpdateItem(js2.toString(), temp);
						it[temp.index] = temp;
					}
					js.clear();
					for (int j = 0; j < 6; j++) {
						if (it[j] == null) {
							m2.writer().writeByte(0);
						} else {
							m2.writer().writeByte(1);
							if (fashion_ != null && fashion_[j] != -1) {
								m2.writer().writeShort(fashion_[j]);
							} else {
								m2.writer().writeShort(ItemTemplate3.get_it_by_id(it[j].id).part);
							}
						}
					}
					m2.writer().writeByte(0);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		addmsg(m2);
		m2.cleanup();
	}

	private void login_notice(String s) throws IOException {
		Message m = new Message(-11);
		m.writer().writeShort(0);
		m.writer().writeByte(0);
		m.writer().writeUTF("Thông báo");
		m.writer().writeUTF(s);
		m.writer().writeByte(0);
		addmsg(m);
		m.cleanup();
	}

	public void ReadPartNew(Message m2) throws IOException {
		short index = m2.reader().readShort();
		Message m = new Message(-82);
		m.writer().writeShort(index);
		m.writer().write(Util.loadfile("data/msg/readpartnew/" + index));
		addmsg(m);
		m.cleanup();
	}

	public void create_char(Message m2) throws IOException {
		if (list_char.size() >= 1) {
			login_notice("Tạo tối đa 1 nhân vật trên 1 acc!");
			return;
		}
		String name = m2.reader().readUTF().replace(" ", "");
		name = name.toLowerCase();
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
		if (!p.matcher(name).matches()) {
			login_notice("Tên không hợp lệ, nhập lại đi!!");
			return;
		}
		byte clazz = m2.reader().readByte();
		short head = m2.reader().readShort();
		short hair = m2.reader().readShort();
		Connection connection = null;
		Statement st = null;
		try {
			connection = SQL.gI().getCon();
			st = connection.createStatement();
			String query =
			      "INSERT INTO `players` (`name`, `body`, `level`, `exp`, `clazz`, `vang`, `ngoc`, `site`, `bag3`, `it_body`, `potential`, `bag47`, `rms`, `skill`, `friend`, `enemy`, `fashion`, `eff`, `box3`, `box47`, `quest_daily`, `date`) VALUES ('%s', '%s', %s, %s, %s, %s, %s, '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
			String body_wear = "";
			String skill_by_clazz = "";
			switch (clazz) {
				case 1: {
					body_wear =
					      "[[0,1,1,0,1,1,0,0,0,-1,-1,0,0,0,-1,[[1,48],[4,9]],[],0,[],0],[40,1,1,3,6,1,0,0,0,-1,-1,0,0,0,-1,[[3,5],[15,7]],[],0,[],3],[80,1,1,5,11,1,0,0,0,-1,-1,0,0,0,-1,[[3,8],[15,1]],[],0,[],5]]";
					skill_by_clazz =
					      "[[0,0,0,0],[20,1,0,0],[40,1,0,0],[375,1,0,0],[487,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
					break;
				}
				case 2: {
					body_wear =
					      "[[8,1,2,0,2,1,0,0,0,-1,-1,0,0,0,-1,[[1,76]],[],0,[],0],[48,1,2,3,7,1,0,0,0,-1,-1,0,0,0,-1,[[3,3],[15,5]],[],0,[],3],[88,1,2,5,12,1,0,0,0,-1,-1,0,0,0,-1,[[3,6],[15,4]],[],0,[],5]]";
					skill_by_clazz =
					      "[[60,0,0,0],[80,1,0,0],[100,1,0,0],[395,1,0,0],[492,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
					break;
				}
				case 3: {
					body_wear =
					      "[[16,1,3,0,3,1,0,0,0,-1,-1,0,0,0,-1,[[1,79]],[],0,[],0],[56,1,3,3,8,1,0,0,0,-1,-1,0,0,0,-1,[[3,6],[15,3]],[],0,[],3],[96,1,3,5,13,1,0,0,0,-1,-1,0,0,0,-1,[[3,8],[15,8]],[],0,[],5]]";
					skill_by_clazz =
					      "[[120,0,0,0],[140,1,0,0],[160,1,0,0],[415,1,0,0],[497,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
					break;
				}
				case 4: {
					body_wear =
					      "[[24,1,4,0,4,1,0,0,0,-1,-1,0,0,0,-1,[[1,51],[23,18]],[],0,[],0],[64,1,4,3,9,1,0,0,0,-1,-1,0,0,0,-1,[[3,7],[15,4]],[],0,[],3],[104,1,4,5,14,1,0,0,0,-1,-1,0,0,0,-1,[[3,7],[15,8]],[],0,[],5]]";
					skill_by_clazz =
					      "[[180,0,0,0],[200,1,0,0],[220,1,0,0],[435,1,0,0],[502,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
					break;
				}
				case 5: {
					body_wear =
					      "[[32,1,5,0,5,1,0,0,0,-1,-1,0,0,0,-1,[[1,69],[16,9]],[],0,[],0],[72,1,5,3,10,1,0,0,0,-1,-1,0,0,0,-1,[[3,9],[15,4]],[],0,[],3],[112,1,5,5,15,1,0,0,0,-1,-1,0,0,0,-1,[[3,2],[15,2]],[],0,[],5]]";
					skill_by_clazz =
					      "[[240,0,0,0],[260,0,0,0],[280,1,0,0],[455,1,0,0],[507,-1,0,0],[300,-1,0,0],[305,-1,0,0],[310,-1,0,0],[315,-1,0,0],[320,-1,0,0],[325,-1,0,0],[552,-1,0,0],[557,-1,0,0]]";
					break;
				}
			}
			query = String.format(query, name, "[" + head + "," + hair + "]", 1, 0, clazz, 100_000, // beri create
			      500, // ruby create
			      "[0,300,300]", "[]", body_wear, "[5,0,1,1,1,1,1]", "[]", "[[],[],[],[],[],[],[],[],[],[],[]]",
			      skill_by_clazz, "[]", "[]", "[[],[]]", "[]", "[]", "[]", "[-1,-1,0,0,100,1]",
			      Date.from(Instant.now()).toString());
			st.execute(query);
		} catch (SQLException e) {
			login_notice("tên đã được sử dụng!");
			return;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		list_char.add(name);
		flush();
		send_list_char();
	}

	private void flush() {
		JSONArray js = new JSONArray();
		for (int i = 0; i < list_char.size(); i++) {
			js.add(list_char.get(i));
		}
		Connection conn = null;
		Statement st = null;
		try {
			conn = SQL.gI().getCon();
			st = conn.createStatement();
			st.executeUpdate("UPDATE `accounts` SET `char` = '" + js.toJSONString() + "' WHERE BINARY `user` = '"
			      + this.user + "' AND BINARY `pass` = '" + this.pass + "';");
		} catch (SQLException e) {
			e.printStackTrace();
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
	}

	public synchronized void update_onl(int type) {
		Connection connection = null;
		Statement st = null;
		try {
			connection = SQL.gI().getCon();
			st = connection.createStatement();
			st.executeUpdate("UPDATE `accounts` SET `onl` = " + type + " WHERE `user` = '" + this.user + "' AND `pass` = '"
			      + this.pass + "' LIMIT 1;");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean check_onl() {
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			connection = SQL.gI().getCon();
			st = connection.createStatement();
			rs = st.executeQuery("SELECT `onl` FROM `accounts` WHERE BINARY `user` = '" + this.user
			      + "' AND BINARY `pass` = '" + this.pass + "' LIMIT 1;");
			if (!rs.next()) {
				return false;
			}
			if (rs.getBoolean("onl")) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void clear_network(Session ss) {
		if (ss.sendd != null) {
			ss.sendd.interrupt();
			ss.sendd = null;
		}
		if (ss.receiv != null) {
			ss.receiv.interrupt();
			ss.receiv = null;
		}
		try {
			if (!ss.socket.isClosed()) {
				ss.socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
