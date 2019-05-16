package com.tarena.bird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
 
public class BirdGame extends JPanel {
	Bird bird;
	Column column1, column2; 
	Ground ground;
	BufferedImage background;
	/** ��Ϸ����״̬ */
	boolean gameOver;
	BufferedImage gameOverImage;
	/** ��Ϸ��ʼ״̬ */
	boolean started;
	BufferedImage startImage;
	//����
	int score;
	
	Sound hitSound;
	Sound flappySound;
	Sound scoreSound;
	Sound startSound;
	
	/** ��ʼ�� BirdGame �����Ա��� */
	public BirdGame() throws Exception {
		started = false;
		startImage = ImageIO.read(
			getClass().getResource("start.png"));
		gameOver = false;
		gameOverImage = ImageIO.read(
			getClass().getResource(
					"gameover.png"));
		score = 0;
		bird = new Bird();
		column1 = new Column(1);
		column2 = new Column(2);
		ground = new Ground();
		background = ImageIO.read(
			getClass().getResource("bg.png")); 
		
		hitSound = new Sound("hit.wav");
		flappySound = new Sound("flappy.wav");
		scoreSound = new Sound("score.wav");
		startSound = new Sound("start.wav");
	}
	
	/** "��д(�޸�)"paint����ʵ�ֻ��� */
	public void paint(Graphics g){
		g.drawImage(background, 0, 0, null);
		g.drawImage(column1.image, 
			column1.x-column1.width/2, 
			column1.y-column1.height/2, null);
		g.drawImage(column2.image, 
			column2.x-column2.width/2, 
			column2.y-column2.height/2, null);
		//��paint��������ӻ��Ʒ������㷨
		Font f = new Font(Font.SANS_SERIF,
				Font.BOLD, 40);
		g.setFont(f);
		g.drawString(""+score, 40, 60);
		g.setColor(Color.WHITE);
		g.drawString(""+score, 40-3, 60-3);
		
		g.drawImage(ground.image, ground.x, 
			ground.y, null);
		//��ת(rotate)��ͼ����ϵ����API����
		Graphics2D g2 = (Graphics2D)g;
		g2.rotate(-bird.alpha, bird.x, bird.y);
		g.drawImage(bird.image, 
			bird.x-bird.width/2, 
			bird.y-bird.height/2, null);
		g2.rotate(bird.alpha, bird.x, bird.y);
		//��paint�����������ʾ��Ϸ����״̬����
		if(gameOver){
			g.drawImage(gameOverImage,0,0,null);
		}
		if(! started){
			g.drawImage(startImage, 0, 0, null);
		}
 
		//��ӵ��Եķ���
//		g.drawRect(bird.x-bird.size/2, 
//				bird.y-bird.size/2, 
//				bird.size, bird.size);
//		g.drawRect(column1.x-column1.width/2, 
//				column1.y-column1.height/2, 
//				column1.width, 
//				column1.height/2-column1.gap/2);
//		g.drawRect(column1.x-column1.width/2, 
//				column1.y+column1.gap/2, 
//				column1.width, 
//				column1.height/2-column1.gap/2);		
	}//paint�����Ľ���
	//BirdGame����ӷ���action()
	public void action() throws Exception {
		MouseListener l=new MouseAdapter(){
			//Mouse ���� Pressed����
			public void mousePressed(
					MouseEvent e){
				try{
					if(gameOver){
						synchronized(BirdGame.this){
					    	column1 = new Column(1);
					    	column2 = new Column(2);
					    	bird = new Bird();
					    	started = false;
					    	gameOver = false;
					    	score = 0;
					    	startSound.play();
						}
					}else{
						started = true;
						//�����Ϸ���
						bird.flappy();
						flappySound.play();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		//��l�ҽӵ���ǰ����壨game����
		addMouseListener(l);
		
		while(true){
			if( !gameOver ){//���û�н�������һ��
				if(started){
					column1.step();
					column2.step();
					bird.step();
				}
				bird.fly();
				ground.step();
				//�Ʒ��߼�
				if( bird.x == column1.x ||
					bird.x == column2.x ){
					score++;
					scoreSound.play();
				}
			}
			synchronized(BirdGame.this){
				//�����ײ�ϵ�����Ϸ�ͽ�����
				if( bird.hit(ground)  ||
					bird.hit(column1) ||
					bird.hit(column2) ){
					gameOver = true;
					hitSound.play();
				}
			}

			repaint();
			Thread.sleep(1000/60);
		}
	}
	
	/** ��������ķ��� */
	public static void main(String[] args)
		throws Exception {
		JFrame frame = new JFrame();
		BirdGame game = new BirdGame();
		frame.add(game);
		frame.setSize(440, 670);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(
				JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		game.action();
	}
}
/** ���� */
class Ground{
	BufferedImage image;
	int x, y;
	int width;
	int height;
	public Ground() throws Exception {
		image = ImageIO.read(
		  getClass().getResource("ground.png"));
		width = image.getWidth();
		height = image.getHeight();
		x = 0;
		y = 500;
	}//����Ĺ���������
	//�����������,��ӷ����������ƶ�һ��
	public void step(){
		x--;
		if(x==-109){
			x = 0;
		}
	}
}//������Ľ���
/** �������ͣ�x,y�����ӵ����ĵ��λ�� */
class Column{
	BufferedImage image;
	int x,y;
	int width, height;
	/** �����м�ķ�϶ */
	int gap;
	int distance;//���룬��������֮��ľ���
	Random random = new Random();
	/** ����������ʼ�����ݣ�n����ڼ������� */
	public Column(int n) throws Exception {
		image=ImageIO.read(
		  getClass().getResource("column.png"));
		width = image.getWidth();
		height = image.getHeight();
		gap=144;
		distance = 245;
		x = 550+(n-1)*distance;
		y = random.nextInt(218)+132;
	}
	//��Column����ӷ��� step����action���ô˷���
	public void step(){
		x--;
		if(x==-width/2){
			x = distance * 2 - width/2;
			y = random.nextInt(218)+132;
		}
	}
}//Column��Ľ���
/** ������, x,y�����������ĵ�λ�� */
class Bird{
	BufferedImage image;
	int x,y;
	int width, height;
	int size;//��Ĵ�С��������ײ���
	
	//��Bird�����������ԣ����ڼ������λ��
    double g;//  �������ٶ�
    double t;//  ����λ�õļ��ʱ��
    double v0;// ��ʼ�����ٶ�
    double speed;// �ǵ�ǰ�������ٶ�
    double s;//     �Ǿ���ʱ��t�Ժ��λ��
    double alpha;// �������� ���ȵ�λ
    //��Bird���ж���
    //����һ�飨���飩ͼƬ������Ķ���֡
    BufferedImage[] images;
    //�Ƕ���֡����Ԫ�ص��±�λ��
    int index;
    
	public Bird() throws Exception {
		image=ImageIO.read(
			getClass().getResource("0.png"));
		width = image.getWidth();
		height = image.getHeight();
		x = 132;
		y = 280;
		size = 40;
		g = 4;
		v0 = 20;
		t = 0.25;
		speed = v0;
		s = 0;
		alpha=0;
		//��������,����8��Ԫ�ص�����
		//��8����λ�ã�û��ͼƬ����
		//8��λ�õ����: 0 1 2 3 4 5 6 7
		images = new BufferedImage[8];
		for(int i=0; i<8; i++){
			//i = 0 1 2 3 4 5 6 7 
			images[i] = ImageIO.read(
  			  getClass().getResource(i+".png"));
		}
		index = 0;
	}
	//��Bird����ӷ���(fly)�Ĵ���
	public void fly(){
		index++;
		image = images[(index/12) % 8];
	}
	//��Bird���������ƶ�����
	public void step(){
		double v0 = speed;
		s = v0*t + g*t*t/2;//���������˶�λ��
		y = y-(int)s;//�����������λ��
		double v = v0 - g*t;//�����´ε��ٶ�
		speed = v;
//		if(y>=500){//���������棬����������
//			y = 280;
//			speed = 35;
//		}
		//����Java API�ṩ�ķ����к������������
		alpha = Math.atan(s/8);
	}
	//��Bird����ӷ���
	public void flappy(){
		//�������ó�ʼ�ٶȣ��������Ϸ�
		speed = v0;
	}
	//��������ӷ��� hit
	//��⵱ǰ���Ƿ���������ground
	//�������true��ʾ������ײ
	//���򷵻�false��ʾû����ײ
	public boolean hit(Ground ground){
		boolean hit = y + size/2 > ground.y;
		if(hit){
			y = ground.y-size/2;
			alpha = -3.14159265358979323/2;
		}
		return hit;
	}
	//��⵱ǰ�����Ƿ�ײ������
	public boolean hit(Column column){
		//�ȼ���Ƿ������ӵķ�Χ����
		if(x>column.x-column.width/2-size/2 &&
		   x<column.x+column.width/2+size/2){
			//����Ƿ��ڷ�϶��
			if(y>column.y-column.gap/2+size/2 &&
			   y<column.y+column.gap/2-size/2){
				return false;
			}
			return true;
		}
		return false;
	}
}






