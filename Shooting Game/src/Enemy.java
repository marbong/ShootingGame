import java.awt.Point;

class Enemy
{
	//���ӿ� �����ϴ� �� ĳ���� ���� Ŭ����
	W_Shooting_frame main;
	Point pos;
	Point _pos;
	Point dis;
	int img;
	int kind;
	int life;
	int mode;
	int cnt;
	int shoottype;
	Bullet bul;
	Enemy(W_Shooting_frame main, int img, int x, int y, int kind, int mode){
		this.main=main;
		pos=new Point(x,y);
		_pos=new Point(x,y);
		dis=new Point(x/100,y/100);
		this.kind=kind;
		this.img=img;
		this.mode=mode;
		life=6+main.RAND(0,5)*main.level;//���� ������ ���� �������� ź�� ��� �ð��� ª������
		cnt=main.RAND(main.level*5,80);
		shoottype=main.RAND(0,4);
	}
	public boolean move(){
		boolean ret=true;

		switch(shoottype){//���� ���¿� ���� ���� �ٸ� ������ �Ѵ�.
		case 0://�÷��̾ ���� 3���� �����Ѵ�
			if(cnt%100==0||cnt%103==0||cnt%106==0) {//cnt�� ���� ������ üũ�Ѵ�
				bul=new Bullet(pos.x, pos.y, 2, 1, main.getAngle(pos.x,pos.y,main.myx,main.myy), 6);
				main.bullets.add(bul);
			}
			break;
		case 1://Ÿ�̸ӿ� ���� 4����ź�� �߻��Ѵ�
			if(cnt%90==0||cnt%100==0||cnt%110==0) {
				bul=new Bullet(pos.x, pos.y, 2, 1, (0+(cnt%36)*10)%360, 6);
				main.bullets.add(bul);
				bul=new Bullet(pos.x, pos.y, 2, 1, (30+(cnt%36)*10)%360, 6);
				main.bullets.add(bul);
				bul=new Bullet(pos.x, pos.y, 2, 1, (60+(cnt%36)*10)%360, 6);
				main.bullets.add(bul);
				bul=new Bullet(pos.x, pos.y, 2, 1, (90+(cnt%36)*10)%360, 6);
				main.bullets.add(bul);
			}
			break;
		case 2://ª�� �������� �÷��̾� ��ó�� ���� �� �߾� �߻��Ѵ�
			if(cnt%30==0||cnt%60==0||cnt%90==0||cnt%120==0||cnt%150==0||cnt%180==0) {
				bul=new Bullet(pos.x, pos.y, 2, 1, (main.getAngle(pos.x,pos.y,main.myx,main.myy)+main.RAND(-20,20))%360, 6);
				main.bullets.add(bul);
			}
			break;
		case 3://�÷��̾ ���� 3����ź�� �߻��Ѵ�
			if(cnt%90==0||cnt%110==0||cnt%130==0){
				bul=new Bullet(pos.x, pos.y, 2, 1, main.getAngle(pos.x,pos.y,main.myx,main.myy), 6);
				main.bullets.add(bul);
				bul=new Bullet(pos.x, pos.y, 2, 1, (main.getAngle(pos.x,pos.y,main.myx,main.myy)-20)%360, 6);
				main.bullets.add(bul);
				bul=new Bullet(pos.x, pos.y, 2, 1, (main.getAngle(pos.x,pos.y,main.myx,main.myy)+20)%360, 6);
				main.bullets.add(bul);
			}
			break;
		case 4://�ƹ��� ���ݵ� �����ʴ´�
			break;
		}

		switch(kind){
		case 0:
			switch(mode){
			case 0:
				pos.x-=500;
				pos.y+=80;
				if(pos.x<main.myx) mode=2;
				break;
			case 1:
				pos.x-=500;
				pos.y-=80;
				if(pos.x<main.myx) mode=3;
				break;
			case 2:
				pos.x+=600;
				pos.y+=240;
				break;
			case 3:
				pos.x+=600;
				pos.y-=240;
				break;
			}
			break;
		case 1:
			break;
		}
		dis.x=pos.x/100;
		dis.y=pos.y/100;
		if(dis.x<0||dis.x>640||dis.y<0||dis.y>480) ret=false;

		cnt++;
		return ret;
	}
}