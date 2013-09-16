package net.geral.slotcar.lapcounter.core;

import java.io.File;
import java.security.InvalidParameterException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class SoundController implements Runnable {
	private static final String EXTENSION = ".wav";
	private static final int MAX_DATA_SIZE = 1024 * 1024; // 1 MB

	private static File folder = new File("res", "sounds");

	private static AudioInputStream getAudioInputStream(File file) {
		try {
			return AudioSystem.getAudioInputStream(file);
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}

	private static int getDataSize(AudioInputStream ais, AudioFormat format) {
		long size = ais.getFrameLength() * format.getFrameSize();
		if (size > MAX_DATA_SIZE) {
			Logger.error("Audio data too big: " + size);
			return 0;
		}
		return (int) size;
	}

	public static String getFolder() {
		return folder.getAbsolutePath();
	}

	public static void setFolder(String path) {
		File newFolder = new File(path);
		if (!newFolder.isDirectory())
			throw new InvalidParameterException(
					"Path not found or is not a valid directory.");
		folder = newFolder;
	}

	private final String file;

	private AudioFormat format = null;

	private byte[] data = null;

	public SoundController(String file) {
		this.file = file;
		AudioInputStream ais = getAudioInputStream(new File(folder, file
				+ EXTENSION));
		if (ais == null)
			return;

		format = ais.getFormat();
		if (!loadData(ais)) {
			format = null;
			data = null;
			return;
		}
	}

	private boolean loadData(AudioInputStream ais) {
		int size = getDataSize(ais, format);
		if (size == 0)
			return false;

		Logger.log("Reading " + file + " (dataSize=" + size + ") ...");

		data = new byte[size];
		int read = 0;
		int pos = 0;
		try {
			while (read != -1) {
				read = ais.read(data, pos, data.length);
				if (read > 0)
					pos += read;
			}
		} catch (Exception e) {
			Logger.error(e);
			return false;
		} finally {
			try {
				ais.close();
			} catch (Exception ee) {
				Logger.log(ee);
			}
		}

		if (pos != size) {
			Logger.error("Corrupt data, read (" + pos + ") size (" + size
					+ ").");
			return false;
		}

		return true;
	}

	@Override
	public void run() {
		SourceDataLine sdl = null;

		try {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			sdl = (SourceDataLine) AudioSystem.getLine(info);
			sdl.open(format);
		} catch (Exception e) {
			if (sdl != null)
				sdl.close();
			Logger.error(e);
			return;
		}

		sdl.start();
		try {
			sdl.write(data, 0, data.length);
		} finally {
			sdl.drain();
			sdl.close();
		}
	}
}
