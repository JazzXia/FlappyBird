package com.tarena.bird;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

public class Sound {
	byte[] data;
	AudioFormat format;
	int length;
	
	//static Executor pool = Executors.newCachedThreadPool();
	
	public Sound(String name) throws Exception  {
		AudioInputStream in = 
				AudioSystem.getAudioInputStream(
						getClass().getResource(name));
		format = in.getFormat();
		length = (int)in.getFrameLength();
		data = new byte[length];
		in.read(data);
		in.close();
	}
	public void play(){
		Runnable runner = new Runnable(){
			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					clip.open(format, data, 0, length);
					clip.start();
					clip.drain();
					clip.stop();
					clip.close();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		};
		//pool.execute(runner);
		new Thread(runner).start();
	}
}

