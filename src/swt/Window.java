package swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Window
{
	static HashMap<String, String> urlList = new HashMap<String, String>();

	public static void main(String[] args) throws IOException
	{
	    Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setText("ä¯ÀÀÆ÷");
	    Image image = new Image(shell.getDisplay(), "Favicon.ico");
	    shell.setImage(image);
	    shell.setLayout(new FormLayout()); // ³äÂú
	    
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
	    
	    Browser browser = new Browser(shell, SWT.NONE); // ä¯ÀÀÆ÷
	    MyBrowser myBrowser = new MyBrowser(browser);

	    data = new FormData();  
	    data.top = new FormAttachment(controls);  
	    data.bottom = new FormAttachment(status);  
	    data.left = new FormAttachment(0, 0);  
	    data.right = new FormAttachment(100, 0);  
	    browser.setLayoutData(data);

	    controls.setLayout(new GridLayout(10, false));

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
	    

	    
	    
	    Combo url = new Combo(controls, SWT.ARROW_DOWN);
	    myBrowser.initialize(display, browser, urlList, url);
	    url.setLayoutData(new GridData(296, SWT.DEFAULT));
	    url.setSize(600, url.getSize().y);
	    url.setFocus();
	    url.addListener(SWT.DefaultSelection, new Listener() 
	    {
	    	public void handleEvent(Event e) 
	    	{
	    		String str = url.getText();  
	    		int flag = 1;
	    		if(urlList.containsKey(str))
	    		{
	    			str = urlList.get(str);
		    		browser.setUrl(str);
		    		url.setText(str);;
		    		flag = 0;
	    		}
	    		if(flag == 1)
	    		{
	    			urlList.put(str, str);
	    			browser.setUrl(str);
	    			url.add(str);
	    		}
	    	}
	    });
	    
	    FileReader reader = new FileReader("mark.txt");
		BufferedReader br = new BufferedReader(reader);
		String name = null;
		String markURL = null;
		while((name = br.readLine()) != null)
		{
			markURL = br.readLine();
			urlList.put(name, markURL);
			url.add(name);
		}
		br.close();

	    Button button = new Button(controls, SWT.PUSH);  
	    button.setText("Go");
	    button.addSelectionListener(new SelectionAdapter() 
	    {
	    	public void widgetSelected(SelectionEvent event) 
	    	{
	    		String str = url.getText();  
	    		int flag = 1;
	    		if(urlList.containsKey(str))
	    		{
	    			str = urlList.get(str);
		    		browser.setUrl(str);
		    		url.setText(str);;
		    		flag = 0;
	    		}
	    		if(flag == 1)
	    		{
	    			urlList.put(str, str);
	    			browser.setUrl(str);
	    			url.add(str);
	    		}
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
		    			url.add(markName);
		    		}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}  
	    });
		
		Button button_mail = new Button(controls, SWT.PUSH);  
	    button_mail.setText("·¢ËÍÓÊ¼þ");  
	    button_mail.addSelectionListener(new SelectionAdapter() 
	    {  
	    	public void widgetSelected(SelectionEvent event) 
	    	{  
	    		final Shell shell = new Shell(display);
		        shell.setText("·¢ËÍÓÊ¼þ");
		        Image image = new Image(shell.getDisplay(), "Favicon.ico");
			    shell.setImage(image);
		        shell.setLayout(new GridLayout());
		        new Mail(shell);
	    	}
	    });
	    
	    Button button_down = new Button(controls, SWT.PUSH);  
	    button_down.setText("URLÏÂÔØ");  
	    button_down.addSelectionListener(new SelectionAdapter() 
	    {  
	    	public void widgetSelected(SelectionEvent event) 
	    	{  
	    		final Shell shell = new Shell(display);
		        shell.setText("URLÏÂÔØ");
		        Image image = new Image(shell.getDisplay(), "Favicon.ico");
			    shell.setImage(image);
		        shell.setLayout(new GridLayout());
		        new BigFile(shell);
	    	}
	    });

	    shell.open();
	    browser.setUrl("www.baidu.com");
	    while(!shell.isDisposed()) 
	    {
	    	if(!display.readAndDispatch())
	    		display.sleep();
	    }
	    display.dispose();
	}
}
//"http://www.roseindia.net"