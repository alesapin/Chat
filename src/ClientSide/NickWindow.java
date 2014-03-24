package ClientSide;



import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NickWindow extends Shell {

	Button start;
	Text nickname;
	Text servername;
	PairOfStrings serverName;
	NickWindow(Display parent,PairOfStrings s){
		super(parent);
		serverName=s;
		this.setLayout(new GridLayout(2,false));
		Label title = new Label(this, SWT.NONE);
		title.setText("Nick");
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		title.setLayoutData(data);
		
		nickname = new Text(this, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		data.widthHint=200;
		nickname.setLayoutData(data);
		
		Label title1 = new Label(this, SWT.NONE);
		title1.setText("Server");
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		title1.setLayoutData(data);

		servername = new Text(this, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 2;
		servername.setLayoutData(data);
		
		start=new Button(this, SWT.PUSH);
		start.setText("Start");
		data = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
		start.setLayoutData(data);
		setStartListener();
	}
	private void setStartListener(){
		start.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				serverName.server=servername.getText();
				serverName.name=nickname.getText();
				NickWindow.this.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
