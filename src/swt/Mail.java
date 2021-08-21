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
		label1.setText("���ͷ����䣺");
		Text SendUser = new Text(controls, SWT.BORDER);
		SendUser.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label2 = new Label(controls, SWT.NULL);
		label2.setText("���ͷ��������룺");
		Text SendPassword = new Text(controls, SWT.BORDER);
		SendPassword.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label3 = new Label(controls, SWT.NULL);
		label3.setText("���ܷ����䣺");
		Text ReceiveUser = new Text(controls, SWT.BORDER);
		ReceiveUser.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label5 = new Label(controls, SWT.NULL);
		label5.setText("�ʼ����⣺");
		Text Title = new Text(controls, SWT.BORDER);
		Title.setLayoutData(new GridData(300, SWT.DEFAULT));
		
		Label label4 = new Label(controls, SWT.NULL);
		label4.setText("���ģ�");
		
		Button button_mail = new Button(controls, SWT.PUSH);  
	    button_mail.setText("����");
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
        String SendUser = str1;// ��������
        String SendPassword = str2; // ����
        String ReceiveUser = str3; // ��������
        String content = str5;

        int index1 = SendUser.indexOf("@");
        int index2 = SendUser.indexOf(".");
        String str = SendUser.substring(index1+1, index2);
        /*
         *���û������������Base64���� 
         */
        String UserBase64 = Base64Utile_cc.EncodeBase64(SendUser.getBytes());
        String PasswordBase64 = Base64Utile_cc.EncodeBase64(SendPassword.getBytes());
        try 
        {
            /*
             *Զ�����ӷ�������25�Ŷ˿�
             *�������������������(��������ȡ���������ص���Ϣ��������������������Ӧ����Ϣ) 
            */
            Socket socket=new Socket("smtp." + str + ".com", 25); // ��������
            InputStream inputStream = socket.getInputStream(); //��ȡ������������Ϣ����
            InputStreamReader isr = new InputStreamReader(inputStream); //�ֽڽ���Ϊ�ַ�
            BufferedReader br = new BufferedReader(isr); //�ַ�����

            OutputStream outputStream=socket.getOutputStream();//�������������Ӧ��Ϣ
            PrintWriter pw=new PrintWriter(outputStream, true);//true�����Դ�flush
            System.out.println(br.readLine());

            /*
             *�������������Ϣ�Լ���������Ӧ��� 
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

            //��������(��������,���ͷ�,���շ�,����,��)
            pw.println("subject:"+str4);
            pw.println("from:"+SendUser);
            pw.println("to:"+ReceiveUser);
            pw.println("Content-Type: text/plain;charset=\"gb2312\"");//���ñ����ʽ�ɷ�����������
            pw.println();
            pw.println(content);
            pw.println(".");
            pw.print("");
            System.out.println(br.readLine());

            /*
             *�������,�ж������������ 
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
ׯ���� 2017192036 ��Ϣ������ѧ

163->sina
a351213937@163.com
82217268a
a351213937@sina.com
ׯ���� 2017192036 ��Ϣ������ѧ

163->qq
a351213937@163.com
82217268a
351213937@qq.com
ׯ���� 2017192036 ��Ϣ������ѧ
*/
