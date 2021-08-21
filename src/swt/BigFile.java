package swt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class BigFile 
{
	Shell shell;
	
	int nSplit = 5;
	String urlName = null;
	URL url = null;
	String fileName = null;
	String tmpName = null;
	String suffix = null;
	
	CountDownLatch latch = null;
	
	long fileLength = 0l;
	long threadLength = 0l;
	long[] startPos;
	long[] endPos;
	
	boolean bool = false;
	
	BigFile(Shell shell)
	{
		this.shell = shell;
		
		Composite controls = new Composite(shell, SWT.NONE);
		
		controls.setLayout(new GridLayout(2, false));
		Label label1 = new Label(controls, SWT.NULL);
		label1.setText("������ԴURL��");
		Text urlName = new Text(controls, SWT.BORDER);
		urlName.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label2 = new Label(controls, SWT.NULL);
		label2.setText("�ļ����ƣ�");
		Text suffix = new Text(controls, SWT.BORDER);
		suffix.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Button button_mail = new Button(controls, SWT.PUSH);  
	    button_mail.setText("��ʼ����");
	    button_mail.addSelectionListener(new SelectionAdapter() 
	    {  
	    	public void widgetSelected(SelectionEvent event) 
	    	{
	    		init(urlName.getText(), suffix.getText());
	    		try {
					downloads();
					shell.dispose();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    });
	    shell.open();
	}
	
	void init(String urlName, String suffix)
	{
		this.urlName = urlName;
		this.fileName = suffix;
		this.tmpName = "tmp_" + suffix;
		this.startPos = new long[nSplit];
		this.endPos = new long[nSplit];
		this.latch = new CountDownLatch(nSplit);
	}
	
	class DownloadThread implements Runnable
	{
		long startPos;
		long endPos;
		BigFile task = null;
		RandomAccessFile downloadfile = null;
		RandomAccessFile rantmpfile = null;
		int id;
		CountDownLatch latch = null;
		File tmpFile = null;
		File file = null;
		
		DownloadThread(long startPos, long endPos, BigFile task, int id, File file, File tmpFile, CountDownLatch latch)
		{
			this.startPos = startPos;
	        this.endPos = endPos;
	        this.task = task;
	        this.tmpFile = tmpFile;
	        this.file = file;
	        try
	        {
				this.downloadfile = new RandomAccessFile(this.file, "rw");
				this.rantmpfile = new RandomAccessFile(this.tmpFile, "rw");
	        } 
	        catch (FileNotFoundException e) 
	        {
				e.printStackTrace();
			}
	        this.id = id;
	        this.latch = latch;
		}

		@Override
		public void run() 
		{
			while(true)
			{
				try 
				{
					HttpURLConnection httpconn = (HttpURLConnection) task.url.openConnection();
					setHeader(httpconn);
					httpconn.setReadTimeout(20000);
                    httpconn.setConnectTimeout(20000);
                    
                    if(startPos < endPos)
                    {
                    	httpconn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                    	downloadfile.seek(startPos);

                    	if (httpconn.getResponseCode() != HttpURLConnection.HTTP_OK && httpconn.getResponseCode() != HttpURLConnection.HTTP_PARTIAL)
                    	{
                    		task.bool = true;
                            httpconn.disconnect();
                            downloadfile.close();
                            System.out.println("�߳�" + id + "�����������");
                            latch.countDown();//�������Լ�
                            break;
                    	}
                    	InputStream in = httpconn.getInputStream();
                    	long count = 0l;
                    	int length = 0;
                    	byte buf[] = new byte[1024];
                    	
                    	// ���߳�����δ��� &&�������л��ж�ȡ����
                    	while(!task.bool && (length = in.read(buf)) != -1)
                    	{
                    		count += length;
                    		downloadfile.write(buf, 0, length);
                    		
                    		startPos += length;
                            rantmpfile.seek(8 * id + 8);
                            rantmpfile.writeLong(startPos);
                    	}
                    	
                    	System.out.println("�߳� " + id + " ����������: " + count);
                        
                        //�ر���
                        in.close();
                        httpconn.disconnect();
                        downloadfile.close();
                        rantmpfile.close();
                    }
                    latch.countDown();//�������Լ�
                    System.out.println("�߳�" + id + "�������");
                    break;
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		}
	}
	
	void downloads() throws IOException, InterruptedException
	{
		try 
		{
			File file = new File(fileName);
			File tmpFile = new File(tmpName);
			
			url = new URL(urlName);
			HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
			
			setHeader(httpconn);
			fileLength = httpconn.getContentLengthLong();
			threadLength = fileLength/nSplit;
			
			findBreakPoint(startPos, endPos, tmpFile);
			
			ExecutorService exec = Executors.newCachedThreadPool();
            for(int i = 0; i < nSplit; i++) 
            {
                exec.execute(new DownloadThread(startPos[i], endPos[i], this, i, file, tmpFile, latch));
            }
            latch.await();//����������Ϊ0֮ǰ�����ڴ˴�һֱ������
            exec.shutdown();
            
            if(file.length() == fileLength) 
    		{
                if(tmpFile.exists()) 
                {
                    System.out.println("ɾ����ʱ�ļ�");
                    tmpFile.delete();
                }
            }
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
	}
	
	void findBreakPoint(long[] startPos, long[] endPos, File tmpFile) throws IOException
	{
		RandomAccessFile raf = null;
		
		if(tmpFile.exists())
		{
			System.out.println("��ʱ�ļ��Ѵ��ڣ����ڼ������أ�");
			try 
			{
				raf = new RandomAccessFile(tmpFile, "rw");
				for (int i = 0; i < nSplit; i++) 
				{
                    raf.seek(8 * i + 8);
                    startPos[i] = raf.readLong();

                    raf.seek(8 * (i + 1000) + 16);
                    endPos[i] = raf.readLong();

                    System.out.println("�߳�" + (i + 1) + " ��ʼλ��:" + startPos[i] + ", ����λ��: " + endPos[i]);
                }
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("�ļ�δ���أ����ڿ�ʼ���أ�");
			raf = new RandomAccessFile(tmpFile, "rw");
			for (int i = 0; i < nSplit; i++) 
			{
                startPos[i] = threadLength * i;
                if (i == nSplit - 1) {
                    endPos[i] = fileLength;
                } else {
                    endPos[i] = threadLength * (i + 1) - 1;
                }

                raf.seek(8 * i + 8);
                raf.writeLong(startPos[i]);

                raf.seek(8 * (i + 1000) + 16);
                raf.writeLong(endPos[i]);

                System.out.println("�߳�" + (i + 1) + "��ʼλ��:" + startPos[i] + ", ����λ��: " + endPos[i]);
            }
		}
	}
	private void setHeader(HttpURLConnection con)
	{
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Referer", "http://www.skycn.com/soft/14857.html");
    }
}
