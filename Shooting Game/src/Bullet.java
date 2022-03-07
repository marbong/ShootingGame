import java.awt.Point;

class Bullet    // �Ѿ��� ó���ϱ� ���� Ŭ����
{
	
	Point dis;//�Ѿ��� ǥ�� ��ǥ
	Point pos;//�Ѿ��� ��� ��ǥ
	Point _pos;//�Ѿ��� ���� ��ǥ
	int degree;//�Ѿ��� ���� ���� (����)
	int speed;//�Ѿ��� �̵� �ӵ�
	int img_num;//�Ѿ��� �̹��� ��ȣ
	int from;//�Ѿ��� ���� �߻��ߴ°�
	Bullet(int x, int y, int img_num, int from, int degree, int speed){
		pos=new Point(x,y);
		dis=new Point(x/100,y/100);
		_pos=new Point(x,y);
		this.img_num=img_num;
		this.from=from;
		this.degree=degree;
		this.speed=speed;
	}
	public void move(){
		_pos=pos;  
		pos.x-=(speed*Math.sin(Math.toRadians(degree))*100);
		pos.y-=(speed*Math.cos(Math.toRadians(degree))*100);
		dis.x=pos.x/100;
		dis.y=pos.y/100;
	}
}