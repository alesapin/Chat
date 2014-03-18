package ClientSide;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import ServerSide.Server;

public class MainClientPanel extends Composite{
	private Text text;
	private Text entry;
	private List friends;
	private ClientWork parent;
	private Thread readingThread=new Thread(){
		@Override
		public void run(){
			while(true){
				final String receve=parent.readFromServer();
				if(receve!=null){
					parent.getDisplay().syncExec(new Runnable(){

						@Override
						public void run() {
							if(receve.charAt(0)==Server.NEW_USER_COME_TO_SERVER){
								System.out.println("Имя, полученное от сервера:"+receve);
								friends.add(receve.substring(1));
							}else if(receve.charAt(0)==3){
								parent.registerNewWritingCompanion(receve);
								
							}else{
								text.append("\n"+parent.getCurrentWritingCompanion()+":"+receve);
							}	
						}
						
					});					
				}
			}
		}
	};
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainClientPanel(ClientWork parent,int style) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		this.parent=parent;
		setLayout(null);
		
		text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		text.setBounds(10, 10, 300, 225);
		
		entry = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		entry.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		entry.setBounds(10, 241, 300, 49);
		
		friends= new List(this, SWT.BORDER | SWT.V_SCROLL);
		friends.setFont(SWTResourceManager.getFont("Dingbats", 13, SWT.NORMAL));
		friends.setBounds(316, 10, 124, 280);
		System.out.println("Я пошел");
		String[] data=parent.getCurrentFriends();
		System.out.println(Arrays.toString(data));
		if(data.length>0){
			for(String s:data){		
				friends.add(s);
			}
		}
		controlCompanion();
		controlPressEnter();
		readingThread.start();
	
	}
	private void controlCompanion(){
		friends.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int ind=friends.getSelectionIndex();
				String user=friends.getItem(ind);
				if(user!=MainClientPanel.this.parent.getCurrentCompanion()){
					MainClientPanel.this.parent.registerNewListeningCompanion(user);
				}
				
			}
		});

	}
	private void controlPressEnter(){
	    entry.addListener(SWT.Traverse, new Listener()
	    {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if(event.detail==SWT.TRAVERSE_RETURN){
					String currentMessage=entry.getText();
					if(currentMessage!=""){
						text.append("\n"+parent.getMyName()+":"+currentMessage);
						parent.sendNormalMessage(currentMessage);
				
						entry.setText("");
					}
				}
				
			}
	    });
	}

}
