import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;

class W_Shooting_frame extends Frame implements KeyListener, Runnable
{
	//�⺻ �����츦 �����ϴ� �������� �����
	//KeyListener : Ű���� �Է� �̺�Ʈ�� �޴´�
	//Runnable : �����带 �����ϰ� �Ѵ�

	public final static int UP_PRESSED		=0x001;
	public final static int DOWN_PRESSED	=0x002;
	public final static int LEFT_PRESSED	=0x004;
	public final static int RIGHT_PRESSED	=0x008;
	public final static int FIRE_PRESSED	=0x010;
	
	GameScreen gamescreen;

	Thread mainwork;//������ ��ü
	boolean roof=true;//������ ���� ����
	Random rnd = new Random();	 // ���� ����

	//���� ��� ���� ����
	int status;//������ ����
	int cnt;//���� ����� ��Ʈ�� ����
	int delay;//���� ������. 1/1000�� ����.
	long pretime;//���� ������ �����ϱ� ���� �ð� üũ��
	int keybuff;//Ű ���۰�


	//���ӿ� ����
	int score;//����
	int mylife;//���� ���
	int gamecnt;//���� �帧 ��Ʈ��
	int scrspeed=16;//��ũ�� �ӵ�
	int level;//���� ����

	int myx,myy;//�÷��̾� ��ġ
	int myspeed ;//�÷��̾� �̵� �ӵ�
	int mydegree;//�÷��̾� �̵� ����

	int mywidth, myheight;//�÷��̾� ĳ������ �ʺ� ����
	int mymode=1;//�÷��̾� ĳ������ ���� (0���� ������� ����,����(����),���÷���,������,���)
	int myimg;//�÷��̾� �̹���
	int mycnt;
	boolean myshoot=false;//�Ѿ� �߻簡 ������ �ִ°�
	int myshield;//�ǵ� ���� ����

	int gScreenWidth=640;//���� ȭ�� �ʺ�
	int gScreenHeight=480;//���� ȭ�� ����

	Vector bullets=new Vector();//�Ѿ� ����. 
	Vector enemies=new Vector();//�� ĳ���� ����.
	Vector effects=new Vector();//����Ʈ ����
	Vector items=new Vector();//������ ����

	
	W_Shooting_frame(){

		gamescreen=new GameScreen(this);
		gamescreen.setBounds(0,0,gScreenWidth,gScreenHeight);
		add(gamescreen);//Canvas ��ü�� �����ӿ� �ø���

		systeminit();
		initialize();

		setIconImage(makeImage("./rsc/icon.png"));
		setBackground(new Color(0xffffff));//������ �⺻ ���� ����
		setTitle("Game Start!");//������ �̸� ����
		setLayout(null);//�������� ���̾ƿ��� ������ ����
		setBounds(100,100,640,480);//�������� ���� ��ġ�� �ʺ� ���� ����
		setResizable(false);//�������� ũ�⸦ ������ �� ����
		setVisible(true);//������ ǥ��
		
		addKeyListener(this);//Ű �Է� �̺�Ʈ ������ Ȱ��ȭ
		addWindowListener(new MyWindowAdapter());//�������� �ݱ� ��ư Ȱ��ȭ
	}

	public void systeminit(){//���α׷� �ʱ�ȭ

		status=0;
		cnt=0;
		delay=25;   // 17/1000�� = 58 (������/��)
		keybuff=0;

		mainwork=new Thread(this);
		mainwork.start();  
	}
	public void initialize(){//���� �ʱ�ȭ

		Init_TITLE();
		gamescreen.repaint();
	}

	// ������ ��Ʈ
	public void run(){
		try
		{
			while(roof){
				pretime=System.currentTimeMillis();

				gamescreen.repaint();//ȭ�� ������Ʈ
				process();//���� ó��
				keyprocess();//Ű ó��

				if(System.currentTimeMillis()-pretime<delay) Thread.sleep(delay-System.currentTimeMillis()+pretime);
					//���� ������ ó���ϴµ� �ɸ� �ð��� üũ�ؼ� �����̰����� �����Ͽ� �����̸� �����ϰ� �����Ѵ�.
					//���� ���� �ð��� ������ �ð����� ũ�ٸ� ���� �ӵ��� �������� �ȴ�.

				if(status!=4) cnt++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// Ű �̺�Ʈ ������ ó��
	public void keyPressed(KeyEvent e) {
		if(status==2){
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				keybuff|=FIRE_PRESSED;
				break;
			case KeyEvent.VK_LEFT:
				keybuff|=LEFT_PRESSED;  //��ƼŰ�� ������ ó��
				break;
			case KeyEvent.VK_UP:
				keybuff|=UP_PRESSED;
				break;
			case KeyEvent.VK_RIGHT:
				keybuff|=RIGHT_PRESSED;
				break;
			case KeyEvent.VK_DOWN:
				keybuff|=DOWN_PRESSED;
				break;
			case KeyEvent.VK_1:
				if(myspeed>1) myspeed--;
				break;
			case KeyEvent.VK_2:
				if(myspeed<9) myspeed++;
				break;
			case KeyEvent.VK_3:
				if(status==2) status=4;
				break;
			default:
				break;
			}
		} else if(status!=2) keybuff=e.getKeyCode();
	}
	public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				keybuff&=~FIRE_PRESSED;
				myshoot=true;
				break;
			case KeyEvent.VK_LEFT:
				keybuff&=~LEFT_PRESSED;  //��ƼŰ�� ���� ó��
				break;
			case KeyEvent.VK_UP:
				keybuff&=~UP_PRESSED;
				break;
			case KeyEvent.VK_RIGHT:
				keybuff&=~RIGHT_PRESSED;
				break;
			case KeyEvent.VK_DOWN:
				keybuff&=~DOWN_PRESSED;
				break;
			}
	}
	public void keyTyped(KeyEvent e) {
	}

	// ���� �Ǵ�, ������ �̺�Ʈ, CPU ���� ó��
	private void process(){
		switch(status){
		case 0://Ÿ��Ʋȭ��
			break;
		case 1://��ŸƮ
			process_MY();
			if(mymode==2) status=2;
			break;
		case 2://����ȭ��
			process_MY();
			process_ENEMY();
			process_BULLET();
			process_EFFECT();
			process_GAMEFLOW();
			process_ITEM();
			break;
		case 3://���ӿ���
			process_ENEMY();
			process_BULLET();
			process_GAMEFLOW();
			break;
		case 4://�Ͻ�����
			break;
		default:
			break;
		}
		if(status!=4) gamecnt++;
	}

	// Ű �Է� ó��
	private void keyprocess(){
		switch(status){
		case 0://Ÿ��Ʋȭ��
			if(keybuff==KeyEvent.VK_SPACE) {
				Init_GAME();
				Init_MY();
				status=1;
			}
			break;
		case 2://����ȭ��
			if(mymode==2||mymode==0){
				switch(keybuff){
				case 0:
					mydegree=-1;
					myimg=0;
					break;
				case FIRE_PRESSED:
					mydegree=-1;
					myimg=6;
					break;
				case UP_PRESSED:
					mydegree=0;
					myimg=2;
					break;
				case UP_PRESSED|FIRE_PRESSED:
					mydegree=0;
					myimg=6;
					break;
				case LEFT_PRESSED:
					mydegree=90;
					myimg=4;
					break;
				case LEFT_PRESSED|FIRE_PRESSED:
					mydegree=90;
					myimg=6;
					break;
				case RIGHT_PRESSED:
					mydegree=270;
					myimg=2;
					break;
				case RIGHT_PRESSED|FIRE_PRESSED:
					mydegree=270;
					myimg=6;
					break;
				case UP_PRESSED|LEFT_PRESSED:
					mydegree=45;
					myimg=4;
					break;
				case UP_PRESSED|LEFT_PRESSED|FIRE_PRESSED:
					mydegree=45;
					myimg=6;
					break;
				case UP_PRESSED|RIGHT_PRESSED:
					mydegree=315;
					myimg=2;
					break;
				case UP_PRESSED|RIGHT_PRESSED|FIRE_PRESSED:
					mydegree=315;
					myimg=6;
					break;
				case DOWN_PRESSED:
					mydegree=180;
					myimg=2;
					break;
				case DOWN_PRESSED|FIRE_PRESSED:
					mydegree=180;
					myimg=6;
					break;
				case DOWN_PRESSED|LEFT_PRESSED:
					mydegree=135;
					myimg=4;
					break;
				case DOWN_PRESSED|LEFT_PRESSED|FIRE_PRESSED:
					mydegree=135;
					myimg=6;
					break;
				case DOWN_PRESSED|RIGHT_PRESSED:
					mydegree=225;
					myimg=2;
					break;
				case DOWN_PRESSED|RIGHT_PRESSED|FIRE_PRESSED:
					mydegree=225;
					myimg=6;
					break;
				default:
					keybuff=0;
					mydegree=-1;
					myimg=0;
					break;
				}
			}
			break;
		case 3:
			if(gamecnt++>=200&&keybuff==KeyEvent.VK_SPACE){
				Init_TITLE();
				status=0;
				keybuff=0;
			}
			break;
		case 4:
			if(gamecnt++>=200&&keybuff==KeyEvent.VK_3) status=2;
			break;
		default:
			break;
		}
	}


	public void Init_TITLE(){  //�����̹��� ���
		int i;

		gamescreen.title=makeImage("./rsc/title.png");
		gamescreen.title_key=makeImage("./rsc/pushspace.png");

	}
	public void Init_GAME(){  // �̹����� �ִ¸޼ҵ�
		int i;

		
		gamescreen.bg=makeImage("./rsc/����.JPG");
		gamescreen.bg_f=makeImage("./rsc/bg_f.png");
		for(i=0;i<4;i++) gamescreen.cloud[i]=makeImage("./rsc/cloud"+i+".png");
		for(i=0;i<4;i++) gamescreen.bullet[i]=makeImage("./rsc/game/bullet_"+i+".png");
		gamescreen.enemy[0]=makeImage("./rsc/game/enemy0.png");
		gamescreen.explo=makeImage("./rsc/game/explode.png");
		gamescreen.item[0]=makeImage("./rsc/game/item0.png");
		gamescreen.item[1]=makeImage("./rsc/game/item1.png");
		gamescreen._start=makeImage("./rsc/game/start.png");
		gamescreen._over=makeImage("./rsc/game/gameover.png");
		gamescreen.shield=makeImage("./rsc/game/shield.png");
		keybuff=0;
		bullets.clear();
		enemies.clear();
		effects.clear();
		items.clear();
		level=0;
	}
	public void Init_MY(){  //ĳ���Ͱ� ������ ���ϰ� �ϴ� �޼ҵ�
		for(int i=0;i<9;i++){
			if(i<10)
				gamescreen.chr[i]=makeImage("./rsc/player/my_0"+i+".png");
			else
				gamescreen.chr[i]=makeImage("./rsc/player/my_"+i+".png");
		}
		Init_MYDATA();
	}
	public void Init_MYDATA(){  //������ ���� ���ǵ� ���� �����ϴ� �޼ҵ�
		score=0;
		myx=0;
		myy=23000;
		myspeed=4;
		mydegree=-1;
		mymode=1;
		myimg=2;
		mycnt=0;
		mylife=3;
		keybuff=0;
	}
	public void process_MY(){
		Bullet shoot;
		switch(mymode){
		case 1:
			myx+=200;
			if(myx>20000) mymode=2;
			break;
		case 0:
			if(mycnt--==0) {
				mymode=2;
				myimg=0;
			}
		case 2:
			if(mydegree>-1) {
				myx-=(myspeed*Math.sin(Math.toRadians(mydegree))*100);
				myy-=(myspeed*Math.cos(Math.toRadians(mydegree))*100);
			}
			if(myimg==6) {
				myx-=20;
				if(cnt%4==0||myshoot){
					myshoot=false;
					shoot=new Bullet(myx+2500, myy+1500, 0, 0, RAND(245,265), 8);
					bullets.add(shoot);
					shoot=new Bullet(myx+2500, myy+1500, 0, 0, RAND(268,272), 9);
					bullets.add(shoot);
					shoot=new Bullet(myx+2500, myy+1500, 0, 0, RAND(275,295), 8);
					bullets.add(shoot);
				}
			}
			break;
		case 3:
			myimg=8;
			if(mycnt--==0) {
				mymode=0;
				mycnt=50;
			}
			break;
		}
		if(myx<2000) myx=2000;
		if(myx>62000) myx=62000;
		if(myy<3000) myy=3000;
		if(myy>45000) myy=45000;
	}
	public void process_ENEMY(){
		int i;
		Enemy buff;
		for(i=0;i<enemies.size();i++){
			buff=(Enemy)(enemies.elementAt(i));
			if(!buff.move()) enemies.remove(i);
		}
	}
	public void process_BULLET(){
		Bullet buff;
		Enemy ebuff;
		Effect expl;
		int i,j, dist;
		for(i=0;i<bullets.size();i++){
			buff=(Bullet)(bullets.elementAt(i));
			buff.move();
			if(buff.dis.x<10||buff.dis.x>gScreenWidth+10||buff.dis.y<10||buff.dis.y>gScreenHeight+10) {
				bullets.remove(i);//ȭ�� ������ ������ �Ѿ� ����
				continue;
			}
			if(buff.from==0) {//�÷��̾ �� �Ѿ��� ������ ���� ����
				for(j=0;j<enemies.size();j++){
					ebuff=(Enemy)(enemies.elementAt(j));
					dist=GetDistance(buff.dis.x,buff.dis.y, ebuff.dis.x,ebuff.dis.y);
					if(dist<1500) {//�߰��� �Ÿ��� ���� ������ ������ ������ ���� ��
						if(ebuff.life--<=0){//�� ������ ����
							enemies.remove(j);//�� ĳ���� �Ұ�
							expl=new Effect(0, ebuff.pos.x, buff.pos.y, 0);
							effects.add(expl);//���� ����Ʈ �߰�
							Item tem=new Item(ebuff.pos.x, buff.pos.y, RAND(1,(level+1)*20)/((level+1)*20));//���� ����� �ִ밪�� ���� �����Ǵ� �������� 1�� �ȴ�
							items.add(tem);
						}
						expl=new Effect(0, ebuff.pos.x, buff.pos.y, 0);
						effects.add(expl);
						score++;//���� �߰�
						bullets.remove(i);//�Ѿ� �Ұ�
					}
				}
			} else { //���� �� �Ѿ��� �÷��̾�� ���� ����
				if(mymode!=2) continue;
				dist=GetDistance(myx/100,myy/100, buff.dis.x,buff.dis.y);
				if(dist<500) {
					if(myshield==0){
						mymode=3;
						mycnt=30;
						bullets.remove(i);
						expl=new Effect(0, myx-2000, myy, 0);
						effects.add(expl);
						if(--mylife<=0) {
							status=3;
							gamecnt=0;
						}
					} else {//�ǵ尡 ���� ���
						myshield--;
						bullets.remove(i);
					}
				}
			}
		}
	}
	public void process_EFFECT(){
		int i;
		Effect buff;
		for(i=0;i<effects.size();i++){
			buff=(Effect)(effects.elementAt(i));
			if(cnt%3==0) buff.cnt--;
			if(buff.cnt==0) effects.remove(i);
		}
	}
	public void process_GAMEFLOW(){
		int control=0;
		int newy=0, mode=0;
		if(gamecnt<500) control=1;
		else if(gamecnt<1000) control=2;
		else if(gamecnt<1300) control=0;
		else if(gamecnt<1700) control=1;
		else if(gamecnt<2000) control=2;
		else if(gamecnt<2400) control=3;
		else {
			gamecnt=0;
			level++;
		}
		if(control>0) {
			newy=RAND(30,gScreenHeight-30)*100;
			if(newy<24000) mode=0; else mode=1;
		}
		switch(control){
		case 1:
			if(gamecnt%90==0) {
				Enemy en=new Enemy(this, 0, gScreenWidth*100, newy, 0,mode);
				enemies.add(en);
			}
			break;
		case 2:
			if(gamecnt%50==0) {
				Enemy en=new Enemy(this, 0, gScreenWidth*100, newy, 0,mode);
				enemies.add(en);
			}
			break;
		case 3:
			if(gamecnt%20==0) {
				Enemy en=new Enemy(this, 0, gScreenWidth*100, newy, 0,mode);
				enemies.add(en);
			}
			break;
		}
	}
	public void process_ITEM(){
		int i, dist;
		Item buff;
		for(i=0;i<items.size();i++){
			buff=(Item)(items.elementAt(i));
			dist=GetDistance(myx/100,myy/100, buff.dis.x,buff.dis.y);
			if(dist<1000) {//������ ȹ��
				switch(buff.kind){
				case 0://�Ϲ� ����
					score+=100;
					break;
				case 1://�ǵ�
					myshield=5;
					break;
				}
				items.remove(i);
			} else
				if(buff.move()) items.remove(i);
		}
	}

	public Image makeImage(String furl){
		Image img;
		Toolkit tk=Toolkit.getDefaultToolkit();
		img=tk.getImage(furl);
		try {
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForID(0);
			// getImage�� �о���� �̹����� �ε��� �Ϸ�ƴ��� Ȯ���ϴ� �κ�
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	
		return img;
	}
	public int GetDistance(int x1,int y1,int x2,int y2){
		return Math.abs((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
	}
	public int RAND(int startnum, int endnum) //��������(startnum���� ramdom����), �������� ����� ����.
	{
		int a, b;
		if(startnum<endnum)
			b = endnum - startnum; //b�� ���� ���� �߻� ��
		else
			b = startnum - endnum;
		a = Math.abs(rnd.nextInt()%(b+1));
		return (a+startnum);
	}
	int getAngle(int sx, int sy, int dx, int dy){
		int vx=dx-sx;
		int vy=dy-sy;
		double rad=Math.atan2(vx,vy);
		int degree=(int)((rad*180)/Math.PI);
		return (degree+180);
	}

	public boolean readGameFlow(String fname){
		String buff;
		try
		{
			BufferedReader fin=new BufferedReader(new FileReader(fname));
			if((buff=fin.readLine())!=null) {
				System.out.println(Integer.parseInt(buff));
			}
			fin.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}