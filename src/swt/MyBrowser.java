package swt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MyBrowser
{
	final ArrayList urls = new ArrayList();
	boolean canRemove = false;

	Browser brow;
	
	MyBrowser(Browser browser)
	{
		this.brow = browser;
		
	}
	
	void initialize(final Display display, Browser brow, HashMap<String, String> u, Combo url_) 
	{
		HashMap<String, String> urlList = u;
		Combo url = url_;
		
		brow.addListener(SWT.MouseDown, new Listener() 
		{
	        public void handleEvent(Event event) 
	        {
	          System.out.println("event.time:" + event.time);
	        }
	    });
		
		brow.addOpenWindowListener(new OpenWindowListener() 
		{
	        public void open(WindowEvent event) 
	        {
	        	final Shell shell = new Shell(display);
	        	shell.setText("ä¯ÀÀÆ÷");
	        	Image image = new Image(shell.getDisplay(), "Favicon.ico");
	        	shell.setImage(image);
	        	shell.setLayout(new FormLayout());
	          
	        	///////////
	        	Composite controls = new Composite(shell, SWT.NONE);  
	        	FormData data = new FormData();  
	        	data.top = new FormAttachment(0, 0);  
	        	data.left = new FormAttachment(0, 0);  
	        	data.right = new FormAttachment(100, 0);  
	        	controls.setLayoutData(data);
		      
	        	Label status = new Label(shell, SWT.NONE);  
	        	data = new FormData();  
	        	data.left = new FormAttachment(0, 0);  
	        	data.right = new FormAttachment(100, 0);  
	        	data.bottom = new FormAttachment(100, 0);  
	        	status.setLayoutData(data);
		      
	        	final Browser browser = new Browser(shell, SWT.NONE);
	        	MyBrowser mybrowser = new MyBrowser(browser);
	        	mybrowser.initialize(display, browser, urlList, url);
	        	event.browser = browser;
	        	event.display.asyncExec(new Runnable() 
	        	{
	        		public void run() 
	        		{
	        			String url = browser.getUrl();
	        			System.out.println(url);
	        			System.out.println(browser.getText());
	        		}
	        	});
		      
	        	data = new FormData();  
			    data.top = new FormAttachment(controls);  
			    data.bottom = new FormAttachment(status);  
			    data.left = new FormAttachment(0, 0);  
			    data.right = new FormAttachment(100, 0);  
			    browser.setLayoutData(data);
			      
			    controls.setLayout(new GridLayout(7, false));
		        
			    Button button_back = new Button(controls, SWT.PUSH);  
			    button_back.setText("¡û");  
			    button_back.addSelectionListener(new SelectionAdapter() 
			    {  
			    	public void widgetSelected(SelectionEvent event) 
			    	{  
			    		browser.back();  
			    	}  
			    });

			    Button button_forward = new Button(controls, SWT.PUSH);  
			    button_forward.setText("¡ú");  
			    button_forward.addSelectionListener(new SelectionAdapter() 
			    {  
			    	public void widgetSelected(SelectionEvent event) 
			    	{  
			    		browser.forward();  
			    	}  
			    });

			    Button button_refresh = new Button(controls, SWT.PUSH);  
			    button_refresh.setText("Ë¢ÐÂ");  
			    button_refresh.addSelectionListener(new SelectionAdapter() 
			    {  
			    	public void widgetSelected(SelectionEvent event) 
			    	{  
			    		browser.refresh();  
			    	}  
			    });  
		  
			    Button button_stop = new Button(controls, SWT.PUSH);  
			    button_stop.setText("Í£Ö¹");  
			    button_stop.addSelectionListener(new SelectionAdapter() 
			    {  
			    	public void widgetSelected(SelectionEvent event) 
			    	{  
			    		browser.stop();
			    	}  
			    });
		      
			    Button button_mark = new Button(controls, SWT.PUSH);  
			    button_mark.setText("¡ï");
			    Text markText = new Text(controls, SWT.BORDER);
				button_mark.addSelectionListener(new SelectionAdapter() 
			    {
			    	public void widgetSelected(SelectionEvent event) 
			    	{
			    		FileWriter fw;
						try 
						{
							String markName = markText.getText();
							String markURL = browser.getUrl();
				    		int flag = 1;
				    		if(urlList.containsKey(markURL))
				    		{
				    			flag = 0;
				    		}
				    		if(flag == 1)
				    		{
				    			fw = new FileWriter("mark.txt", true);
								BufferedWriter bw = new BufferedWriter(fw);
								fw.write(markName + "\r\n");
								fw.write(markURL + "\r\n");
								fw.close();
				    			urlList.put(markName, markURL);
				    			url.add(markURL);
				    		}
						} 
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}  
			    });
	        }
	      });
	      
	      brow.addVisibilityWindowListener(new VisibilityWindowListener() 
	      {
	        public void hide(WindowEvent event) 
	        {
	          Browser browser = (Browser) event.widget;
	          Shell shell = browser.getShell();
	          shell.setVisible(false);
	        }
	 
	        public void show(WindowEvent event) 
	        {
	          Browser browser = (Browser) event.widget;
	          Shell shell = browser.getShell();
	          if (event.location != null)
	              shell.setLocation(event.location);
	          if (event.size != null) 
	          {
	              Point size = event.size;
	              shell.setSize(shell.computeSize(size.x, size.y));
	          }
	          if (event.addressBar || event.menuBar || event.statusBar || event.toolBar) 
	          {
	          }
	          shell.open();
	        }
	      });
	      
	      brow.addDisposeListener(new DisposeListener()
	      {
	        public void widgetDisposed(DisposeEvent event)
	        {
	          Browser browser = (Browser) event.widget;
	          if(canRemove)
	              urls.remove(browser.getUrl());
	          Shell shell = browser.getShell();
	        }
	      });
	}
}