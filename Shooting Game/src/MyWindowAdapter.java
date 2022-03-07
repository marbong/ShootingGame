import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MyWindowAdapter extends WindowAdapter
{
	// 윈도우를 닫기 위한 부가 클래스
	MyWindowAdapter(){
		
		
	}
	public void windowClosing(WindowEvent e) {
		Window wnd = e.getWindow();
		wnd.setVisible(false);  //실제 닫는 동작
		wnd.dispose();  //실제 닫는 동작
		System.exit(0);  //실제 닫는 동작
	}

}