import java.awt.Point;

class Bullet    // 총알을 처리하기 위한 클래스
{
	
	Point dis;//총알의 표시 좌표
	Point pos;//총알의 계산 좌표
	Point _pos;//총알의 직전 좌표
	int degree;//총알의 진행 방향 (각도)
	int speed;//총알의 이동 속도
	int img_num;//총알의 이미지 번호
	int from;//총알을 누가 발사했는가
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