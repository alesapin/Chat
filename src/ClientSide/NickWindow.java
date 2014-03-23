package ClientSide;



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NickWindow extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			NickWindow shell = new NickWindow(display);
			shell.setLayout(new GridLayout(2,false));
			Label title = new Label(shell, SWT.NONE);
			title.setText("Nick");
			GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			data.horizontalSpan = 2;
			title.setLayoutData(data);
			
			Text text = new Text(shell, SWT.BORDER);
			data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			data.horizontalSpan = 2;
			text.setLayoutData(data);
			
			Label title1 = new Label(shell, SWT.NONE);
			title1.setText("Server");
			data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			data.horizontalSpan = 2;
			title1.setLayoutData(data);

			Text text1 = new Text(shell, SWT.BORDER);
			data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			data.horizontalSpan = 2;
			text1.setLayoutData(data);
			Button b=new Button(shell, SWT.PUSH);
			b.setText("Start");
			data = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
			b.setLayoutData(data);
			shell.pack();
			shell.open();
			//shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public NickWindow(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
