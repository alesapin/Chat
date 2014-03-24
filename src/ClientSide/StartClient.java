package ClientSide;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;

public class StartClient {
	public static void main(String[] args){
		Display display = Display.getDefault();
		PairOfStrings p=new PairOfStrings();
		NickWindow w=new NickWindow(display,p);
		w.setSize(400, 200);
		w.pack();
		w.open();
		while (!w.isDisposed())
		{
			if (!display.readAndDispatch())
		            display.sleep();
		}
		if(p.name==null || p.server==null) System.exit(0);
		ClientWork work=new ClientWork(display,p.name,p.server,5000);
		work.setSize(800, 600);
		work.setLayout(new FillLayout());
		MainClientPanel panel=new MainClientPanel(work,SWT.NONE);
		
		panel.setVisible(true);
		work.pack();
		work.open();
		while (!work.isDisposed())
		{
			if (!display.readAndDispatch())
		            display.sleep();
		}
		display.dispose();
	}
	
}
