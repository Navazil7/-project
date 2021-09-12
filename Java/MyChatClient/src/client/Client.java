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
	ClientLogin loginFrame;    //登录界面
	ClientRegister registerFrame;   //注册界面
	
	public static void main(String[] args) {
		new Client();
	}
		
	public Client() {
		super("这里是客户机");
		tf.addActionListener(this); //输入框注册
		btn_connect.addActionListener(this); //连接按钮注册
		btn_send.addActionListener(this);//发送消息按钮注册
		btn_logout.addActionListener(this); //注销按钮注册
		btn_login.addActionListener(this);//登录按钮注册
		btn_register.addActionListener(this); //注册按钮注册
		//窗口退出注册
		addWindowListener(new WindowAdapter() { 
	        public void windowClosing(WindowEvent e) {
	           int i=JOptionPane.showConfirmDialog(null, "确定要退出系统吗？", "退出系统", JOptionPane.YES_NO_OPTION);
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
		
		//主程序
		try {
			while(!isConnected) {			//等待连接		
				Thread.sleep(1000);			
			}
//		    socket = new Socket(tf_ip1.getText()+"."+tf_ip2.getText()+"."+tf_ip3.getText()+"."+tf_ip4.getText(), 
//		    					Integer.parseInt(tf_port.getText()) );

		    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		    String msg1;
		    ta.append("hello"+"\n");
			while (true) {								
				Thread.sleep(10);
				
				//接收消息
				if((msg1 = br.readLine()) != null && !socket.isClosed()) {
					if(checkReceiveMsg(msg1) && isLogin)   
						ta.append(msg1+"\n");
					ta.setCaretPosition(ta.getText().length()); //滚动条自动下拉
				}		
			}
			
		} 
		//常见异常：socket关闭，提示用户断开连接
		catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showConfirmDialog(null, "与服务器连接失败", "确定", JOptionPane.CLOSED_OPTION); 
		}		
	}
	
	//检查服务器发来的信息
	public boolean checkReceiveMsg(String msg1) {
		
		//登录注册界面的消息，均传输参数给登录、注册类解决
		if(msg1.equals("%%SUCCESSLOGIN#")) {  //登录成功
			loginFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILLOGIN#")) {   //登录失败
			loginFrame.receiver(msg1);
		}
		else if(msg1.equals("%%SUCCESSREGISTER#")) {    //注册成功
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILREGISTER#NAME")) {    //注册失败：重复用户名
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FAILREGISTER#ACCOUNT")) {   //注册失败：重复账号
			registerFrame.receiver(msg1);
		}
		else if(msg1.equals("%%FULLUSER#")) {     //聊天室满人
			loginFrame.receiver(msg1);
		}
		
		//刷新用户数量
		else if(msg1.contains("%%USERNUM#")) {     
			String str[]=msg1.split("#");
			int num=Integer.parseInt(str[1]);
			label_userNum.setText("在线用户"+num+"人");
		}
		//心跳检测
		else if(msg1.equals("%%KEEPALIVE#")) {
			sendMsg("%%IMALIVE#");
		}
		//停止服务信号
		else if(msg1.equals("%%STOP#")) {   
			try {
				cancelEnabled(panel2);
				JOptionPane.showConfirmDialog(null, "服务器停止服务", "确定", JOptionPane.CLOSED_OPTION);
				mySetEnabled(panel1);
				btn_login.setEnabled(false);
				btn_register.setEnabled(false);
				btn_logout.setEnabled(false);
				socket.close();
				isLogin=false;
				isConnected=false;
			} catch (IOException e) {
				ta.append("与服务器断开连接...");
				e.printStackTrace();
			}
		}
		//禁言
		else if(msg1.equals("%%SILENCE#")) {    
			cancelEnabled(panel2);
			tf.setText("您已被服务器禁言! ");
		}
		//取消禁言
		else if(msg1.equals("%%TALK#")){
			mySetEnabled(panel2);
			tf.setText("");
			//tf.setText("-----您的禁言已取消! -----");
		}
		//踢出聊天室，关闭程序
		else if(msg1.equals("%%KICKOUT#")) {
			JOptionPane.showConfirmDialog(null, "您已被管理员强制下线", "确定", JOptionPane.CLOSED_OPTION);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	System.exit(0);
		}
		//刷新私聊下拉框的用户
		else if(msg1.contains("%%USERNAME#")){
			 String name[]=msg1.split("#");
			 //两次循环遍历
			 for(int i=1;i<cb.getItemCount(); ) {  //除去下拉框内应当除去的名字				 
				 if(!msg1.contains(cb.getItemAt(i))) {
					 cb.removeItemAt(i);
				 }					 
				 else
					 i++;
			 }
			 for(int j=1;j<name.length;j++) {   //增加下拉框应当有的名字
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
		// TODO 自动生成的方法存根
		if(e.getSource()==btn_send || e.getSource()==tf) {  
			sendMsg(tf.getText());  
			tf.setText("");
		}
		else if(e.getSource()==btn_logout) {		 //注销
			isLogin=false;
			cancelEnabled(panel2);
			btn_login.setEnabled(true);
			btn_register.setEnabled(true);
			btn_logout.setEnabled(false);
			sendMsg("%%LOGOUT#");
		}
		else if(e.getSource()==btn_login) {  
			loginFrame=new ClientLogin(this);  //弹出登录界面
		}		
		else if(e.getSource()==btn_register) {
			registerFrame=new ClientRegister(this);   //弹出注册界面
		}
		
		//连接服务器
		else if(e.getSource()==btn_connect) {
			try {
				btn_connect.setEnabled(false);
//				socket = new Socket(tf_ip1.getText()+"."+tf_ip2.getText()+"."+tf_ip3.getText()+"."+tf_ip4.getText(), 
//						Integer.parseInt(tf_port.getText()) );			//建立连接	
				socket = new Socket("127.0.0.1",8080);
				//连接成功后，关闭连接控件，亮起注册登录按钮
				isConnected=true;
				cancelEnabled(panel1);
				btn_login.setEnabled(true);
				btn_register.setEnabled(true);
				
				JOptionPane.showConfirmDialog(null, "连接成功", "确定", JOptionPane.CLOSED_OPTION);
			} catch (Exception e1) {
				e1.printStackTrace();
				btn_connect.setEnabled(true);
				JOptionPane.showConfirmDialog(null, "与服务器连接失败", "确定", JOptionPane.CLOSED_OPTION);				
			}
		}
	}
	
	//向服务器发送tf文本框上的消息
	public void sendMsg(String msg) {
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			
			//私聊功能
			if(!cb.getSelectedItem().equals("所有人") && !msg.contains("%%")) {
				pw.println("%%PRIVATETALK#"+cb.getSelectedItem()+"#"+msg);
			}
			else
				pw.println(msg);
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
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
//	Label label=new Label("输入聊天信息");
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
//        //开启一个线程监听服务端的消息
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
//        //主线程负责发送消息
//        //System.out.println("请输入你的用户名：");
//        ta.append("请输入你的用户名：");
//        //scan = new Scanner(System.in);
//        String name=new String(tf.getText());
//        out.println(name);
//        //System.out.println(name+",欢迎进入聊天室，输入quit退出");
//        ta.append(name+",欢迎进入聊天室，输入quit退出");
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
//		// TODO 自动生成的方法存根
//		String str=new String(tf.getText());
//		byte buf[]=str.getBytes();
//		tf.setText("");
//		
//	}
//}