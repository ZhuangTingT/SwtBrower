package swt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Mail
{
	Shell shell;
	
	Mail(Shell shell)
	{
		this.shell = shell;
		
		Composite controls = new Composite(shell, SWT.NONE);
		
		controls.setLayout(new GridLayout(2, false));
		Label label1 = new Label(controls, SWT.NULL);
		label1.setText("发送方邮箱：");
		Text SendUser = new Text(controls, SWT.BORDER);
		SendUser.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label2 = new Label(controls, SWT.NULL);
		label2.setText("发送方邮箱密码：");
		Text SendPassword = new Text(controls, SWT.BORDER);
		SendPassword.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label3 = new Label(controls, SWT.NULL);
		label3.setText("接受方邮箱：");
		Text ReceiveUser = new Text(controls, SWT.BORDER);
		ReceiveUser.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label5 = new Label(controls, SWT.NULL);
		label5.setText("邮件标题：");
		Text Title = new Text(controls, SWT.BORDER);
		Title.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label4 = new Label(controls, SWT.NULL);
		label4.setText("正文：");
		
		Button button_mail = new Button(controls, SWT.PUSH);  
	    button_mail.setText("发送");
	    Text content = new Text(shell, SWT.BORDER);
		content.setLayoutData(new GridData(500, 500));
	    button_mail.addSelectionListener(new SelectionAdapter() 
	    {  
	    	public void widgetSelected(SelectionEvent event) 
	    	{
	    		sendMail(SendUser.getText(), SendPassword.getText(), ReceiveUser.getText(), Title.getText(), content.getText());
	    		shell.dispose();
	    	}
	    });
	    shell.open();
	}
	
	void sendMail(String str1, String str2, String str3, String str4, String str5)
    {
        String SendUser = str1;// 发送邮箱
        String SendPassword = str2; // 密码
        String ReceiveUser = str3; // 接收邮箱
        String content = str5;

        int index1 = SendUser.indexOf("@");
        int index2 = SendUser.indexOf(".");
        String str = SendUser.substring(index1+1, index2);
        /*
         *对用户名和密码进行Base64编码 
         */
        String UserBase64 = Base64Utile_cc.EncodeBase64(SendUser.getBytes());
        String PasswordBase64 = Base64Utile_cc.EncodeBase64(SendPassword.getBytes());
        try 
        {
            /*
             *远程连接服务器的25号端口
             *并定义输入流和输出流(输入流读取服务器返回的信息、输出流向服务器发送相应的信息) 
            */
            Socket socket=new Socket("smtp." + str + ".com", 25); // 建立连接
            InputStream inputStream = socket.getInputStream(); //读取服务器返回信息的流
            InputStreamReader isr = new InputStreamReader(inputStream); //字节解码为字符
            BufferedReader br = new BufferedReader(isr); //字符缓冲

            OutputStream outputStream=socket.getOutputStream();//向服务器发送相应信息
            PrintWriter pw=new PrintWriter(outputStream, true);//true代表自带flush
            System.out.println(br.readLine());

            /*
             *向服务器发送信息以及返回其相应结果 
             */

            //helo
            pw.println("helo myxulinjie");
            System.out.println(br.readLine());

            //auth login
            pw.println("auth login");
            System.out.println(br.readLine());
            pw.println(UserBase64);
            System.out.println(br.readLine());
            pw.println(PasswordBase64);
            System.out.println(br.readLine());

            //Set "mail from" and  "rcpt to"
            pw.println("mail from:<"+SendUser+">");
            System.out.println(br.readLine());
            pw.println("rcpt to:<"+ReceiveUser+">");
            System.out.println(br.readLine());

            //Set "data"
            pw.println("data");
            System.out.println(br.readLine());

            //正文主体(包括标题,发送方,接收方,内容,点)
            pw.println("subject:"+str4);
            pw.println("from:"+SendUser);
            pw.println("to:"+ReceiveUser);
            pw.println("Content-Type: text/plain;charset=\"gb2312\"");//设置编码格式可发送中文内容
            pw.println();
            pw.println(content);
            pw.println(".");
            pw.print("");
            System.out.println(br.readLine());

            /*
             *发送完毕,中断与服务器连接 
             */
            pw.println("rset");
            System.out.println(br.readLine());
            pw.println("quit");
            System.out.println(br.readLine());
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}


/*
sina->163
a351213937@sina.com
82387999a
a351213937@163.com
庄婷婷 2017192036 信息与计算科学

163->sina
a351213937@163.com
82217268a
a351213937@sina.com
庄婷婷 2017192036 信息与计算科学

163->qq
a351213937@163.com
82217268a
351213937@qq.com
庄婷婷 2017192036 信息与计算科学
*/
