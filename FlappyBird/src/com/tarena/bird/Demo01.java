package com.tarena.bird;

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Demo01 {
	public static void main(String[] args) throws Exception {
//		Type[] types = AudioSystem.getAudioFileTypes();
//		for (Type type : types) {
//			System.out.println(type); 
//		}
		//Mixer mixer = AudioSystem.getMixer(null);
//		System.out.println(mixer.getLineInfo());
//		//System.out.println(mixer);
//		System.out.println(mixer.getMixerInfo());
//		Info[] infos = mixer.getSourceLineInfo();
//		for (Info info : infos) {
//			System.out.println(info);
//		}
//		
//		SourceDataLine l = (SourceDataLine) mixer.getLine(infos[0]);
//		System.out.println(l); 
//		l.open();
//		
//		System.out.println(mixer.getMaxLines(infos[1]));
//		
//		System.out.println("lines"); 
//		Line[] lines = mixer.getSourceLines();
//		for (Line line : lines) {
//			System.out.println(line + ", "+line.getClass().getName());
//		}
		String fileName = "sfx_swooshing.wav";
		AudioInputStream in = 
				AudioSystem.getAudioInputStream(
						Demo01.class.getResource(fileName));
		//AudioFormat format = in.getFormat();
		//int length = (int)in.getFrameLength();
//		byte[] data = new byte[length];
//		in.read(data);
//		in.close();
		
//		System.out.println("Write Sound"+length);
//		l.open();
//		l.write(data, 0, length);
//		l.flush();
//		l.drain();
//		l.close();
		
		Clip clip = AudioSystem.getClip();
		//clip.open(format,data,0,length);
		
		clip.open(in);
		clip.start();
		clip.drain();
		clip.close();
		//System.in.read();
	}

}
