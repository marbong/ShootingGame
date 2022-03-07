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
	//기본 윈도우를 형성하는 프레임을 만든다
	//KeyListener : 키보드 입력 이벤트를 받는다
	//Runnable : 스레드를 가능하게 한다

	public final static int UP_PRESSED		=0x001;
	public final static int DOWN_PRESSED	=0x002;
	public final static int LEFT_PRESSED	=0x004;
	public final static int RIGHT_PRESSED	=0x008;
	public final static int FIRE_PRESSED	=0x010;
	
	GameScreen gamescreen;

	Thread mainwork;//스레드 객체
	boolean roof=true;//스레드 루프 정보
	Random rnd = new Random();	 // 랜덤 선언

	//게임 제어를 위한 변수
	int status;//게임의 상태
	int cnt;//루프 제어용 컨트롤 변수
	int delay;//루프 딜레이. 1/1000초 단위.
	long pretime;//루프 간격을 조절하기 위한 시간 체크값
	int keybuff;//키 버퍼값


	//게임용 변수
	int score;//점수
	int mylife;//남은 목숨
	int gamecnt;//게임 흐름 컨트롤
	int scrspeed=16;//스크롤 속도
	int level;//게임 레벨

	int myx,myy;//플레이어 위치
	int myspeed ;//플레이어 이동 속도
	int mydegree;//플레이어 이동 방향

	int mywidth, myheight;//플레이어 캐릭터의 너비 높이
	int mymode=1;//플레이어 캐릭터의 상태 (0부터 순서대로 무적,등장(무적),온플레이,데미지,사망)
	int myimg;//플레이어 이미지
	int mycnt;
	boolean myshoot=false;//총알 발사가 눌리고 있는가
	int myshield;//실드 남은 수비량

	int gScreenWidth=640;//게임 화면 너비
	int gScreenHeight=480;//게임 화면 높이

	Vector bullets=new Vector();//총알 관리. 
	Vector enemies=new Vector();//적 캐릭터 관리.
	Vector effects=new Vector();//이펙트 관리
	Vector items=new Vector();//아이템 관리

	
	W_Shooting_frame(){

		gamescreen=new GameScreen(this);
		gamescreen.setBounds(0,0,gScreenWidth,gScreenHeight);
		add(gamescreen);//Canvas 객체를 프레임에 올린다

		systeminit();
		initialize();

		setIconImage(makeImage("./rsc/icon.png"));
		setBackground(new Color(0xffffff));//윈도우 기본 배경색 지정
		setTitle("Game Start!");//윈도우 이름 지정
		setLayout(null);//윈도우의 레이아웃을 프리로 설정
		setBounds(100,100,640,480);//윈도우의 시작 위치와 너비 높이 지정
		setResizable(false);//윈도우의 크기를 변경할 수 없음
		setVisible(true);//윈도우 표시
		
		addKeyListener(this);//키 입력 이벤트 리스너 활성화
		addWindowListener(new MyWindowAdapter());//윈도우의 닫기 버튼 활성화
	}

	public void systeminit(){//프로그램 초기화

		status=0;
		cnt=0;
		delay=25;   // 17/1000초 = 58 (프레임/초)
		keybuff=0;

		mainwork=new Thread(this);
		mainwork.start();  
	}
	public void initialize(){//게임 초기화

		Init_TITLE();
		gamescreen.repaint();
	}

	// 스레드 파트
	public void run(){
		try
		{
			while(roof){
				pretime=System.currentTimeMillis();

				gamescreen.repaint();//화면 리페인트
				process();//각종 처리
				keyprocess();//키 처리

				if(System.currentTimeMillis()-pretime<delay) Thread.sleep(delay-System.currentTimeMillis()+pretime);
					//게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
					//루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.

				if(status!=4) cnt++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// 키 이벤트 리스너 처리
	public void keyPressed(KeyEvent e) {
		if(status==2){
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				keybuff|=FIRE_PRESSED;
				break;
			case KeyEvent.VK_LEFT:
				keybuff|=LEFT_PRESSED;  //멀티키의 누르기 처리
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
				keybuff&=~LEFT_PRESSED;  //멀티키의 떼기 처리
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

	// 각종 판단, 변수나 이벤트, CPU 관련 처리
	private void process(){
		switch(status){
		case 0://타이틀화면
			break;
		case 1://스타트
			process_MY();
			if(mymode==2) status=2;
			break;
		case 2://게임화면
			process_MY();
			process_ENEMY();
			process_BULLET();
			process_EFFECT();
			process_GAMEFLOW();
			process_ITEM();
			break;
		case 3://게임오버
			process_ENEMY();
			process_BULLET();
			process_GAMEFLOW();
			break;
		case 4://일시정지
			break;
		default:
			break;
		}
		if(status!=4) gamecnt++;
	}

	// 키 입력 처리
	private void keyprocess(){
		switch(status){
		case 0://타이틀화면
			if(keybuff==KeyEvent.VK_SPACE) {
				Init_GAME();
				Init_MY();
				status=1;
			}
			break;
		case 2://게임화면
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


	public void Init_TITLE(){  //메인이미지 출력
		int i;

		gamescreen.title=makeImage("./rsc/title.png");
		gamescreen.title_key=makeImage("./rsc/pushspace.png");

	}
	public void Init_GAME(){  // 이미지를 넣는메소드
		int i;

		
		gamescreen.bg=makeImage("./rsc/우주.JPG");
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
	public void Init_MY(){  //캐릭터가 총을쏠때 변하게 하는 메소드
		for(int i=0;i<9;i++){
			if(i<10)
				gamescreen.chr[i]=makeImage("./rsc/player/my_0"+i+".png");
			else
				gamescreen.chr[i]=makeImage("./rsc/player/my_"+i+".png");
		}
		Init_MYDATA();
	}
	public void Init_MYDATA(){  //현재의 점수 스피드 등을 저장하는 메소드
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
				bullets.remove(i);//화면 밖으로 나가면 총알 제거
				continue;
			}
			if(buff.from==0) {//플레이어가 쏜 총알이 적에게 명중 판정
				for(j=0;j<enemies.size();j++){
					ebuff=(Enemy)(enemies.elementAt(j));
					dist=GetDistance(buff.dis.x,buff.dis.y, ebuff.dis.x,ebuff.dis.y);
					if(dist<1500) {//중간점 거리가 명중 판정이 가능한 범위에 왔을 때
						if(ebuff.life--<=0){//적 라이프 감소
							enemies.remove(j);//적 캐릭터 소거
							expl=new Effect(0, ebuff.pos.x, buff.pos.y, 0);
							effects.add(expl);//폭발 이펙트 추가
							Item tem=new Item(ebuff.pos.x, buff.pos.y, RAND(1,(level+1)*20)/((level+1)*20));//난수 결과가 최대값일 때만 생성되는 아이템이 1이 된다
							items.add(tem);
						}
						expl=new Effect(0, ebuff.pos.x, buff.pos.y, 0);
						effects.add(expl);
						score++;//점수 추가
						bullets.remove(i);//총알 소거
					}
				}
			} else { //적이 쏜 총알이 플레이어에게 명중 판정
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
					} else {//실드가 있을 경우
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
			if(dist<1000) {//아이템 획득
				switch(buff.kind){
				case 0://일반 득점
					score+=100;
					break;
				case 1://실드
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
			// getImage로 읽어들인 이미지가 로딩이 완료됐는지 확인하는 부분
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	
		return img;
	}
	public int GetDistance(int x1,int y1,int x2,int y2){
		return Math.abs((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
	}
	public int RAND(int startnum, int endnum) //랜덤범위(startnum부터 ramdom까지), 랜덤값이 적용될 변수.
	{
		int a, b;
		if(startnum<endnum)
			b = endnum - startnum; //b는 실제 난수 발생 폭
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