package net.zhuoweizhang.boardwalk.potato;

import java.io.*;
import android.content.*;
import net.zhuoweizhang.boardwalk.util.*;

public class ExtractRuntime implements Runnable {
	private Context context;
	private File runtimeDir;
	public ExtractRuntime(Context context) {
		this.context = context;
		this.runtimeDir = context.getDir("runtime", 0);
	}

	private void extractAsset(String name) throws IOException {
		File outf = new File(runtimeDir, name);
		AssetsUtil.extractFileFromAssets(context, name, outf);
		outf.setExecutable(true);
	}

	public void run() {
		try {
			File versionFile = new File(runtimeDir, "version");
			versionFile.delete();
			extractAsset("busybox");
			extractTar("/sdcard/jre.tar.xz", new File(runtimeDir, "jvm").getAbsolutePath());
			extractTar("/sdcard/newglibc.tar.xz", new File(runtimeDir, "newglibc").getAbsolutePath());
			extractAsset("libboardwalk_preload.so");
			extractAsset("liblwjgl.so");
			extractAsset("libGLESv1_CM.so");
			extractAsset("libglshim.so");
			versionFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extractExtras() {
		try {
			extractAsset("libgcc_s.so.1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extractTar(String tar, String out) throws Exception {
		File outFile = new File(out);
		outFile.mkdirs();
		String[] argsNew = new String[]{new File(runtimeDir, "busybox").getAbsolutePath(), "tar", "xJf", tar, "-C", out};
		doExec(argsNew);
	}

	public static void doExec(String[] argsNew) throws Exception {
		Process p = new ProcessBuilder(argsNew).redirectErrorStream(true).start();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		StringBuffer buf = new StringBuffer();
		while((line = in.readLine()) != null) {
			System.out.println(line);
			buf.append(line);
			buf.append('\n');
		}
		p.waitFor();
	}
}
