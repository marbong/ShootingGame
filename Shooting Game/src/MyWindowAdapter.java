import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MyWindowAdapter extends WindowAdapter
{
	// �����츦 �ݱ� ���� �ΰ� Ŭ����
	MyWindowAdapter(){
		
		
	}
	public void windowClosing(WindowEvent e) {
		Window wnd = e.getWindow();
		wnd.setVisible(false);  //���� �ݴ� ����
		wnd.dispose();  //���� �ݴ� ����
		System.exit(0);  //���� �ݴ� ����
	}

}