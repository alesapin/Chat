package ClientSide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import ServerSide.Server;

public class MainClientPanel extends Composite {
	private Text text;
	private Text entry;
	private List friends;
	private ClientWork parent;
	private String receve;
	private Label text_1;
	private Runnable updateText = new Runnable() {
		@Override
		public void run() {
			System.out.println("Говно:"+parent.getFromWriters(receve.charAt(0)));
			String message=receve.substring(1);
			if(parent.getFromWriters(receve.charAt(0)).equals(parent.getCurrentListeningCompanion())){
				text.append("\n" 
					+ parent.getFromWriters(receve.charAt(0)) + ":" + message);
				parent.appendInHistory(parent.getFromWriters(receve.charAt(0)),"\n" 
						+ parent.getFromWriters(receve.charAt(0)) + ":" + message );
			}else{
				parent.appendInHistory(parent.getFromWriters(receve.charAt(0)),"\n" 
						+ parent.getFromWriters(receve.charAt(0)) + ":" + message );
			}

		}
	};
	private Runnable deleteFromList = new Runnable() {

		@Override
		public void run() {
			friends.remove(receve.substring(1));
		}

	};
	private Runnable addToList = new Runnable() {

		@Override
		public void run() {
			friends.add(receve.substring(1));

		}

	};
	private Thread myThread = new Thread() {

		@Override
		public void run() {
			outer: while (true) {
				receve = parent.readFromServer();
				if (receve != null) { 
					switch (receve.charAt(0)) {
					case Server.NEW_USER_COME_TO_SERVER:
						parent.getDisplay().syncExec(addToList);
						parent.appendInHistory(receve.substring(1),"");
						break;
					case Server.USER_WRITING_TO_YOU:
						System.out.println("Тебе пишет:"+receve.substring(2));
						System.out.println("Под номером:"+receve.charAt(1));
						parent.setToWriters( receve.charAt(1),receve.substring(2));
						parent.registerNewWritingCompanion(receve.substring(2));
						break;
					case Server.USER_LEFT_SERVER:
						parent.removeFromWriters(receve.charAt(1));
						if (receve.substring(1).equals(
								parent.getCurrentListeningCompanion()))
							parent.registerNewListeningCompanion(parent
									.getMyName());
						parent.getDisplay().syncExec(deleteFromList);
						break;
					case Server.USER_FINISH_WRITING:
						parent.removeFromWriters(receve.charAt(1));
						break;
					case Server.YOU_CAN_CLOSE:
						parent.closeAll();
						break outer;
					default:
						parent.getDisplay().syncExec(updateText);
					}
				}
			}
		}
	};

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MainClientPanel(ClientWork parent, int style) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		this.parent = parent;
		setLayout(new GridLayout(2,false));
		
		text_1 = new Label(this, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		text_1.setText(parent.getMyName());
		text_1.setLayoutData(data);
		
		friends = new List(this, SWT.BORDER | SWT.V_SCROLL);
		friends.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		data=new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
	    data.verticalSpan = 3;
	    int listHeight = friends.getItemHeight() * 12;
	    Rectangle trim = friends.computeTrim(0, 0, 0, listHeight);
	    data.heightHint = trim.height;
	    data.widthHint = 100;
		friends.setLayoutData(data);
		
		text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL
				| SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
	    data.verticalAlignment = GridData.FILL;
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
		data.widthHint=300;
		data.heightHint=400;
		text.setLayoutData(data);

		entry = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		entry.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.heightHint=100;
		entry.setLayoutData(data);

	
		
		setFriends(parent.getCurrentFriends());
		controlCompanion();
		controlPressEnter();
		addOnExitListener();
		parent.registerNewListeningCompanion(parent.getMyName());
		myThread.start();

	}
	private void setFriends(String[] data){
		if (data.length > 0) {
			for (String s : data) { // в функцию
				friends.add(s);
			}
		}
	}
	private void controlCompanion() {
		friends.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int ind = friends.getSelectionIndex();
				if(ind==-1) return;
				String user = friends.getItem(ind);
				if (!user.equals(MainClientPanel.this.parent.getCurrentListeningCompanion())) {
					MainClientPanel.this.parent
							.registerNewListeningCompanion(user);
					text.setText(parent.getFromHistory(user));
				}

			}
		});

	}

	private void addOnExitListener() {
		this.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				parent.sendNormalMessage(Server.USER_LEFT_SERVER + "");
				// readingThread.setFinish();
				// parent.closeAll();
			}

		});
	}

	private void controlPressEnter() {
		entry.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					String currentMessage = entry.getText();
					if (currentMessage != "") {
						text.append("\n" + parent.getMyName() + ":"
								+ currentMessage);
						parent.sendNormalMessage(currentMessage);
						parent.appendInHistory(parent.getCurrentListeningCompanion(), "\n" + parent.getMyName() + ":"
								+ currentMessage);
						entry.setText("");
					}
				}

			}
		});
	}
}
