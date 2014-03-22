package ClientSide;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
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
	private Text text_1;
	private Runnable updateText = new Runnable() {
		@Override
		public void run() {
			text.append("\n" 
					+ parent.getCurrentWritingCompanion() + ":" + receve);

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
				if (receve != null) { // совместить с остальными
					switch (receve.charAt(0)) {
					case Server.NEW_USER_COME_TO_SERVER:
						parent.getDisplay().syncExec(addToList);
						break;
					case Server.NEW_USER_WRITING_TO_YOU:
						parent.registerNewWritingCompanion(receve.substring(1));
						break;
					case Server.USER_LEFT_SERVER:
						if (receve.substring(1).equals(
								parent.getCurrentCompanion()))
							parent.registerNewListeningCompanion(parent
									.getMyName());
						parent.getDisplay().syncExec(deleteFromList);
						break;
					case 4:
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
		setLayout(null);

		text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL
				| SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		text.setBounds(10, 49, 300, 233);

		entry = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		entry.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		entry.setBounds(10, 288, 300, 49);

		friends = new List(this, SWT.BORDER | SWT.V_SCROLL);
		friends.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		friends.setBounds(316, 10, 164, 327);

		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(10, 15, 158, 28);
		text_1.setText(parent.getMyName());
		
		setFriends(parent.getCurrentFriends());
		controlCompanion();
		controlPressEnter();
		addOnExitListener();
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
				String user = friends.getItem(ind);
				if (user != MainClientPanel.this.parent.getCurrentCompanion()) {
					MainClientPanel.this.parent
							.registerNewListeningCompanion(user);
				}

			}
		});

	}

	private void addOnExitListener() {
		this.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				parent.sendNormalMessage((char) 2 + "");
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

						entry.setText("");
					}
				}

			}
		});
	}
}
