package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Client extends ClientGUI implements ActionListener{
		
	private static final long serialVersionUID = 1L;
	
	public int port = 8080;
	protected boolean isLogin=false;
	protected boolean isConnected=false;
	protected String IP="127.0.0.1";
	Socket socket = null;
	BufferedWriter bw;
	ClientLogin loginFrame;    //��¼����
	ClientRegister registerFrame;   //ע�����
	
	public static void main(String[] args) {
		new Client();
	}
		
	public Client() {
		super("�����ǿͻ���");
		tf.addActionListener(this); //�����ע��
		btn_connect.addActionListener(this); //���Ӱ�ťע��
		btn_send.addActionListener(this);//������Ϣ��ťע��
		btn_logout.addActionListener(this); //ע����ťע��
		btn_login.addActionListener(this);//��¼��ťע��
		btn_register.addActionListener(this); //ע�ᰴťע��
		//�����˳�ע��
		addWindowListener(new WindowAdapter() { 
	        public void windowClosing(WindowEvent e) {
	           int i=JOptionPane.showConfirmDialog(null, "ȷ��Ҫ�˳�ϵͳ��", "�˳�ϵͳ", JOptionPane.YES_NO_OPTION);
	           if(i==JOptionPane.YES_OPTION){	 					        	   
	        	   try {
	        		   if(isConnected) {
	        		   		sendMsg("%%QUIT#");
	        		   		socket.close();	  
	        		   }     		   		
					} catch (IOException e1) {
						System.exit(0);
						//e1.printStackTrace();
					}	 
	        	   System.exit(0);
	           }
	        }
	   });
		
		//������
		try {
			while(!isConnected) {			//�ȴ�����		
				Thread.sleep(1000);			
			}
//		    socket = new Socket(tf_ip1.getText()+"."+tf_ip2.getText()+"."+tf_ip3.getText()+"."+tf_ip4.getText(), 
//		    					Integer.parseInt(tf_port.getText()) );

		    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		    String msg1;
		    ta.append("hello"+"\n");
			while (true) {								
				Thread.sleep(10);
				
				//������Ϣ
				if((msg1 = br.readLine()) != null && !socket.isClosed()) {
					if(checkReceiveMsg(msg1) && isLogin)   
						ta.append(msg1+"\n");
					ta.setCaretPosition(ta.getText().length()); //�������Զ�����
				}		
			}
			
		} 
		//�����쳣��socket�رգ���ʾ�û��Ͽ�����
		catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showConfirmDialog(null, "�����������ʧ��", "ȷ��", JOptionPane.CLOSED_OPTION); 
		}		
	}
	
	//����������������Ϣ
	public boolean checkReceiveMsg(String msg1) {
		
		//��¼ע��������Ϣ���������������¼��ע������
		if(msg1.equals("%%SUCCESSLOGIN#")) {  //��¼�ɹ�
			loginFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILLOGIN#")) {   //��¼ʧ��
			loginFrame.receiver(msg1);
		}
		else if(msg1.equals("%%SUCCESSREGISTER#")) {    //ע��ɹ�
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILREGISTER#NAME")) {    //ע��ʧ�ܣ��ظ��û���
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILREGISTER#ACCOUNT")) {   //ע��ʧ�ܣ��ظ��˺�
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FULLUSER#")) {     //����������
			loginFrame.receiver(msg1);
		}
		
		//ˢ���û�����
		else if(msg1.contains("%%USERNUM#")) {     
			String str[]=msg1.split("#");
			int num=Integer.parseInt(str[1]);
			label_userNum.setText("�����û�"+num+"��");
		}
		//�������
		else if(msg1.equals("%%KEEPALIVE#")) {
			sendMsg("%%IMALIVE#");
		}
		//ֹͣ�����ź�
		else if(msg1.equals("%%STOP#")) {   
			try {
				cancelEnabled(panel2);
				JOptionPane.showConfirmDialog(null, "������ֹͣ����", "ȷ��", JOptionPane.CLOSED_OPTION);
				mySetEnabled(panel1);
				btn_login.setEnabled(false);
				btn_register.setEnabled(false);
				btn_logout.setEnabled(false);
				socket.close();
				isLogin=false;
				isConnected=false;
			} catch (IOException e) {
				ta.append("��������Ͽ�����...");
				e.printStackTrace();
			}
		}
		//����
		else if(msg1.equals("%%SILENCE#")) {    
			cancelEnabled(panel2);
			tf.setText("���ѱ�����������! ");
		}
		//ȡ������
		else if(msg1.equals("%%TALK#")){
			mySetEnabled(panel2);
			tf.setText("");
			//tf.setText("-----���Ľ�����ȡ��! -----");
		}
		//�߳������ң��رճ���
		else if(msg1.equals("%%KICKOUT#")) {
			JOptionPane.showConfirmDialog(null, "���ѱ�����Աǿ������", "ȷ��", JOptionPane.CLOSED_OPTION);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	System.exit(0);
		}
		//ˢ��˽����������û�
		else if(msg1.contains("%%USERNAME#")){
			 String name[]=msg1.split("#");
			 //����ѭ������
			 for(int i=1;i<cb.getItemCount(); ) {  //��ȥ��������Ӧ����ȥ������				 
				 if(!msg1.contains(cb.getItemAt(i))) {
					 cb.removeItemAt(i);
				 }					 
				 else
					 i++;
			 }
			 for(int j=1;j<name.length;j++) {   //����������Ӧ���е�����
				 boolean flag=true;
				 int i;
				 for(i=1;i<cb.getItemCount();i++) {
					 if(cb.getItemAt(i).equals(name[j])) {
						 flag=false;
						 break;
					 }
				 }
				 if(flag) {
					 cb.addItem(name[i]);
				 }
			 }
		}
		else {
			return true;
		}
	   return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �Զ����ɵķ������
		if(e.getSource()==btn_send || e.getSource()==tf) {  
			sendMsg(tf.getText());  
			tf.setText("");
		}
		else if(e.getSource()==btn_logout) {		 //ע��
			isLogin=false;
			cancelEnabled(panel2);
			btn_login.setEnabled(true);
			btn_register.setEnabled(true);
			btn_logout.setEnabled(false);
			sendMsg("%%LOGOUT#");
		}
		else if(e.getSource()==btn_login) {  
			loginFrame=new ClientLogin(this);  //������¼����
		}		
		else if(e.getSource()==btn_register) {
			registerFrame=new ClientRegister(this);   //����ע�����
		}
		
		//���ӷ�����
		else if(e.getSource()==btn_connect) {
			try {
				btn_connect.setEnabled(false);
//				socket = new Socket(tf_ip1.getText()+"."+tf_ip2.getText()+"."+tf_ip3.getText()+"."+tf_ip4.getText(), 
//						Integer.parseInt(tf_port.getText()) );			//��������	
				socket = new Socket("127.0.0.1",8080);
				//���ӳɹ��󣬹ر����ӿؼ�������ע���¼��ť
				isConnected=true;
				cancelEnabled(panel1);
				btn_login.setEnabled(true);
				btn_register.setEnabled(true);
				
				JOptionPane.showConfirmDialog(null, "���ӳɹ�", "ȷ��", JOptionPane.CLOSED_OPTION);
			} catch (Exception e1) {
				e1.printStackTrace();
				btn_connect.setEnabled(true);
				JOptionPane.showConfirmDialog(null, "�����������ʧ��", "ȷ��", JOptionPane.CLOSED_OPTION);				
			}
		}
	}
	
	//�����������tf�ı����ϵ���Ϣ
	public void sendMsg(String msg) {
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			
			//˽�Ĺ���
			if(!cb.getSelectedItem().equals("������") && !msg.contains("%%")) {
				pw.println("%%PRIVATETALK#"+cb.getSelectedItem()+"#"+msg);
			}
			else
				pw.println(msg);
		} catch (IOException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
			ta.append("Error:sendMsg"+"\n");
		}
	}

	

}

//import java.awt.Frame;
//import java.awt.Label;
//import java.awt.Panel;
//import java.awt.TextArea;
//import java.awt.TextField;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Scanner;
//
//public class Client extends Frame implements ActionListener{
//	Label label=new Label("����������Ϣ");
//	TextField tf=new TextField(20);
//	TextArea ta=new TextArea();
//	Panel panel=new Panel();
//	
//    private PrintWriter out;
//    //private BufferedReader br;
//    private Scanner scan;
//    private Boolean flag=true;
//    private Socket s;
//    private InputStream is;
//    
//    public Client() throws UnknownHostException, IOException {
//    	super("Client");
//		setSize(300,180);
//		panel.add(label);
//		panel.add(tf);
//		tf.addActionListener(this);
//		add("North",panel);
//		add("Center",ta);
//		addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//				
//			}
//		});
//		show();
//		
//        s=new Socket("127.0.0.1", 5001);
//        is=s.getInputStream();
//    }
//    
//    public static void main(String []args) throws UnknownHostException, IOException {
//        Client client =new Client();
//        client.startup();
//    }
//    public void startup() throws UnknownHostException, IOException {
//        out = new PrintWriter(s.getOutputStream(), true);  
//        
//        //����һ���̼߳�������˵���Ϣ
//        Thread ct=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true) {
//                    if(!flag) break;
//                    try {
//                        receive();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        ct.start();
//        //���̸߳�������Ϣ
//        //System.out.println("����������û�����");
//        ta.append("����������û�����");
//        //scan = new Scanner(System.in);
//        String name=new String(tf.getText());
//        out.println(name);
//        //System.out.println(name+",��ӭ���������ң�����quit�˳�");
//        ta.append(name+",��ӭ���������ң�����quit�˳�");
//        while(flag) {
//            String read=tf.getText();
//            if(read.equalsIgnoreCase("quit")) {
//                flag=false;
//            }
//            //System.out.println(read);
//            out.println(read);
//        }
//        s.close();
//    }
//    
//    public void receive() throws IOException {
//        byte ss[]=new byte[1024];
//        int length=s.getInputStream().read(ss);
//        System.out.println(new String(ss,0,length));
//    }
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO �Զ����ɵķ������
//		String str=new String(tf.getText());
//		byte buf[]=str.getBytes();
//		tf.setText("");
//		
//	}
//}