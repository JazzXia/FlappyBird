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
	/** 游戏结束状态 */
	boolean gameOver;
	BufferedImage gameOverImage;
	/** 游戏开始状态 */
	boolean started;
	BufferedImage startImage;
	//分数
	int score;
	
	Sound hitSound;
	Sound flappySound;
	Sound scoreSound;
	Sound startSound;
	
	/** 初始化 BirdGame 的属性变量 */
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
	
	/** "重写(修改)"paint方法实现绘制 */
	public void paint(Graphics g){
		g.drawImage(background, 0, 0, null);
		g.drawImage(column1.image, 
			column1.x-column1.width/2, 
			column1.y-column1.height/2, null);
		g.drawImage(column2.image, 
			column2.x-column2.width/2, 
			column2.y-column2.height/2, null);
		//在paint方法中添加绘制分数的算法
		Font f = new Font(Font.SANS_SERIF,
				Font.BOLD, 40);
		g.setFont(f);
		g.drawString(""+score, 40, 60);
		g.setColor(Color.WHITE);
		g.drawString(""+score, 40-3, 60-3);
		
		g.drawImage(ground.image, ground.x, 
			ground.y, null);
		//旋转(rotate)绘图坐标系，是API方法
		Graphics2D g2 = (Graphics2D)g;
		g2.rotate(-bird.alpha, bird.x, bird.y);
		g.drawImage(bird.image, 
			bird.x-bird.width/2, 
			bird.y-bird.height/2, null);
		g2.rotate(bird.alpha, bird.x, bird.y);
		//在paint方法中添加显示游戏结束状态代码
		if(gameOver){
			g.drawImage(gameOverImage,0,0,null);
		}
		if(! started){
			g.drawImage(startImage, 0, 0, null);
		}
 
		//添加调试的方框
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
	}//paint方法的结束
	//BirdGame中添加方法action()
	public void action() throws Exception {
		MouseListener l=new MouseAdapter(){
			//Mouse 老鼠 Pressed按下
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
						//鸟向上飞扬
						bird.flappy();
						flappySound.play();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		//将l挂接到当前的面板（game）上
		addMouseListener(l);
		
		while(true){
			if( !gameOver ){//如果没有结束就走一步
				if(started){
					column1.step();
					column2.step();
					bird.step();
				}
				bird.fly();
				ground.step();
				//计分逻辑
				if( bird.x == column1.x ||
					bird.x == column2.x ){
					score++;
					scoreSound.play();
				}
			}
			synchronized(BirdGame.this){
				//如果鸟撞上地面游戏就结束了
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
	
	/** 启动软件的方法 */
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
/** 地面 */
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
	}//地面的构造器结束
	//地面的类体中,添加方法，地面移动一步
	public void step(){
		x--;
		if(x==-109){
			x = 0;
		}
	}
}//地面类的结束
/** 柱子类型，x,y是柱子的中心点的位置 */
class Column{
	BufferedImage image;
	int x,y;
	int width, height;
	/** 柱子中间的缝隙 */
	int gap;
	int distance;//距离，两个柱子之间的距离
	Random random = new Random();
	/** 构造器：初始化数据，n代表第几个柱子 */
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
	//在Column中添加方法 step，在action调用此方法
	public void step(){
		x--;
		if(x==-width/2){
			x = distance * 2 - width/2;
			y = random.nextInt(218)+132;
		}
	}
}//Column类的结束
/** 鸟类型, x,y是鸟类型中心的位置 */
class Bird{
	BufferedImage image;
	int x,y;
	int width, height;
	int size;//鸟的大小，用于碰撞检测
	
	//在Bird类中增加属性，用于计算鸟的位置
    double g;//  重力加速度
    double t;//  两次位置的间隔时间
    double v0;// 初始上抛速度
    double speed;// 是当前的上抛速度
    double s;//     是经过时间t以后的位移
    double alpha;// 是鸟的倾角 弧度单位
    //在Bird类中定义
    //定义一组（数组）图片，是鸟的动画帧
    BufferedImage[] images;
    //是动画帧数组元素的下标位置
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
		//创建数组,创建8个元素的数组
		//是8个空位置，没有图片对象，
		//8个位置的序号: 0 1 2 3 4 5 6 7
		images = new BufferedImage[8];
		for(int i=0; i<8; i++){
			//i = 0 1 2 3 4 5 6 7 
			images[i] = ImageIO.read(
  			  getClass().getResource(i+".png"));
		}
		index = 0;
	}
	//在Bird中添加飞翔(fly)的代码
	public void fly(){
		index++;
		image = images[(index/12) % 8];
	}
	//在Bird中添加鸟的移动方法
	public void step(){
		double v0 = speed;
		s = v0*t + g*t*t/2;//计算上抛运动位移
		y = y-(int)s;//计算鸟的坐标位置
		double v = v0 - g*t;//计算下次的速度
		speed = v;
//		if(y>=500){//如果到达地面，就重新抛起
//			y = 280;
//			speed = 35;
//		}
		//调用Java API提供的反正切函数，计算倾角
		alpha = Math.atan(s/8);
	}
	//在Bird中添加方法
	public void flappy(){
		//重新设置初始速度，重新向上飞
		speed = v0;
	}
	//在鸟中添加方法 hit
	//检测当前鸟是否碰到地面ground
	//如果返回true表示发送碰撞
	//否则返回false表示没有碰撞
	public boolean hit(Ground ground){
		boolean hit = y + size/2 > ground.y;
		if(hit){
			y = ground.y-size/2;
			alpha = -3.14159265358979323/2;
		}
		return hit;
	}
	//检测当前的鸟是否撞到柱子
	public boolean hit(Column column){
		//先检测是否在柱子的范围以内
		if(x>column.x-column.width/2-size/2 &&
		   x<column.x+column.width/2+size/2){
			//检测是否在缝隙中
			if(y>column.y-column.gap/2+size/2 &&
			   y<column.y+column.gap/2-size/2){
				return false;
			}
			return true;
		}
		return false;
	}
}






