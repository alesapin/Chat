package ClientSide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;

public class Tester {
	public static void main(String[] args){
		Display display = Display.getDefault();
		ClientWork shell=new ClientWork(display,"Dada","localhost",5000);
		shell.setSize(800, 600);
		shell.setLayout(new FillLayout());
		final MainClientPanel p=new MainClientPanel(shell,SWT.NONE);
		p.setVisible(true);
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
		            display.sleep();
		}
		display.dispose();
	}
}
