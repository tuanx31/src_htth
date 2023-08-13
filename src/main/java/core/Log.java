package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import client.Player;
import template.Log_template;

public class Log implements Runnable {
	private static Log instance;
	private final BlockingQueue<Log_template> list;
	private final Thread mythread;
	private boolean running;

	public Log() {
		list = new LinkedBlockingQueue<>();
		mythread = new Thread(this);
	}

	public synchronized static Log gI() {
		if (instance == null) {
			instance = new Log();
		}
		return instance;
	}

	@Override
	public void run() {
		while (this.running) {
			try {
				Log_template temp = list.take();
				if (temp != null) {
					try {
						this.save_log(temp.name, temp.text);
					} catch (IOException e) {
						System.err.println("save log err at " + temp.name + " !");
					}
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				System.err.println("exception at save log");
			}
		}
	}

	private void save_log(String name, String text) throws IOException {
		String path = "log/" + Util.fmt_save_log.format(Date.from(Instant.now())) + "/" + name + ".txt";
		File f = new File(path);
		f.getParentFile().mkdirs();
		if (!f.exists()) {
			if (!f.createNewFile()) {
				System.out.println("Tạo file " + name + ".txt xảy ra lỗi");
				return;
			}
		}
		FileWriter fwt = null;
		BufferedWriter bfwt = null;
		try {
			fwt = new FileWriter(f, StandardCharsets.UTF_8, true);
			bfwt = new BufferedWriter(fwt);
			bfwt.append((text + "\n"));
			bfwt.flush();
		} catch (IOException e) {
			System.out.println("Ghi file " + name + ".txt xảy ra lỗi");
		} finally {
			try {
				if (bfwt != null) {
					bfwt.close();
				}
				if (fwt != null) {
					fwt.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public void start_log() {
		this.running = true;
		this.mythread.start();
	}

	public void close_log() {
		this.running = false;
		this.mythread.interrupt();
	}

	public void add_log(Player p, String txt) {
		String time = "[" + Util.get_now_by_time() + "]  ";
		this.list.add(new Log_template(p.name, (time + txt)));
	}
}
