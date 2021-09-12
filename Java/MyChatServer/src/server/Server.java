package server;
import java.net.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;


public class Server extends ServerGUI implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	int maxUserNum=1;
	int nowUserNum=0;
	int port;
	boolean isStart=false;
    List<ClientThread> clientsList;
    List<String> allUserList;
    ServerSocket server;
    FileWriter fw_ch;    //聊天记录文件流
    BufferedWriter bw_ch;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
    	super("服务器");
    	
    	//从D盘txt文件中读取用户数据
    	try {
    		allUserList=new ArrayList<String>();
    		FileReader fr_ac=new FileReader("D:/user.txt");
    		BufferedReader br_ac=new BufferedReader(fr_ac);

			for(;br_ac.ready();) {
				allUserList.add(br_ac.readLine());
			}
			
			ta_sys.append("用户信息总表：" + "\n"); 
            for(String i:allUserList) {
            	ta_sys.append(i+"\n");
            }		
        	br_ac.close();
        	fr_ac.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}

    	//打开chat history文件写入流，准备好写入聊天记录和时间信息
    	try {
			fw_ch=new FileWriter("D:/chat history.txt",true);
			bw_ch=new BufferedWriter(fw_ch);
			bw_ch.newLine();
			bw_ch.write("----------"+myNowTime()+"----------");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	cb.addActionListener(this); //下拉框注册
    	tf.addActionListener(this); //输入框注册
		btn_send.addActionListener(this);//发送消息按钮注册
		btn_startServer.addActionListener(this);//开始服务消息按钮注册
		btn_stopServer.addActionListener(this);//停止服务消息按钮注册
		btn_silence.addActionListener(this);//禁言按钮
		btn_talk.addActionListener(this);//取消禁言按钮
		btn_kickout.addActionListener(this);//踢出聊天室按钮
    	//注册关闭窗口事件，关闭前将数据写入txt文件
    	addWindowListener(new WindowAdapter() { 
	        public void windowClosing(WindowEvent e) {
	           int i=JOptionPane.showConfirmDialog(null, "确定要退出服务器吗？", "退出系统", JOptionPane.YES_NO_OPTION);
	           if(i==JOptionPane.YES_OPTION){	
	        	   //关闭程序前的处理
	        	   try {
	        		   //清除用户连接
	        		   sendMsgAll("%%STOP#");
//	       				for(ClientThread ct:clientsList) {	       				
//	       					ct.socket.close();
//	       					clientsList.remove(ct);	       								
//	       				}
	        		   
	        		   //数据保存到文件中
	        		   bw_ch.newLine();
	        		   bw_ch.write(myNowTime()+":服务器关闭");	
	        		   bw_ch.newLine();
	        		   bw_ch.close();
	        		   fw_ch.close();
	        	   } catch (IOException e1) {
	        		   e1.printStackTrace();
	        	   }
	        	   
			       System.exit(0);	        	  
	           }
	        }
	   });
    	
    	//外部主程序
        try {
        	while(true) {
        		Thread.sleep(10);    //让程序休眠1毫秒，防止循环消耗资源过大
        		
        		//程序开始前的准备工作
        		if(isStart) {      //isStart表示停止服务或开始服务       			
        			ta_sys.append("服务器开始服务\n");  
	                port = 8080; //8080
	                clientsList = new ArrayList<ClientThread>();  
	                server = new ServerSocket(port);	
	                Thread userNum=new Thread(new WatchDogThread());
	                userNum.start();	
	                
	                //内部主程序，ServerSocket对象开始接收客户端的Socket对象
	                while (true) {	                	
	                	Thread.sleep(10);	
	                	if(isStart==false)
	                		break; 	                	
	                	Socket socket = server.accept(); //程序会停留于此
	                    ClientThread clientthread=new ClientThread(socket); //为每个客户新开一个线程
	                    clientsList.add(clientthread);
	                    Thread thread = new Thread(clientthread);
	                    thread.start();
	                }              
                }
        	}
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
    }

    //客户端线程类
    class ClientThread implements Runnable {
        Socket socket;   //客户端Socket类
        BufferedReader br;  //读取客户端发送的数据
        boolean isLogin=false;   //客户端是否登录
        double unusedTime = 0;   //记录线程闲置时间，在run方法内刷新，当时间超过5分钟则调用keepAlive方法进行心跳检测
        
        public String msg;  
        private String clientName="";  //设置为空来检测进程是否已登录
        

        public ClientThread(Socket s) {
            socket = s; 
        }

        //主程序
        public void run() {

            try {
            	//当客户端连接时的准备工作
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ta_sys.append("线程："+clientName+"#"+socket.getInetAddress()+" 开启\n");
                
               //接收客户端消息
                while (true) {   
                	Thread.sleep(10);               	
                	if((msg = br.readLine()) != null  ) {//&& !socket.isClosed()
                		unusedTime=0;  //刷新程序未响应时间

                		//checkReceiveMsg函数检查发送的消息，若为系统消息则做出处理并返回false，若为聊天消息返回true
	                	if(checkReceiveMsg(msg)) {
	                		ta_msg.append("【" + clientName  + "】说：\n"+ msg + "\n");  //聊天消息输出到聊天框
	                		ta_msg.setCaretPosition(ta_msg.getText().length());  //滚动条自动下拉
	                		msg = "【" +  clientName + "】说：\n" + msg;
	                		sendMsgAll(msg);   //聊天消息发送给其他用户
	                		
	                		bw_ch.newLine();   
	                		bw_ch.write(myNowTime()+":"+msg);    //聊天记录存储
	                	}
	                	//系统消息输出在系统消息框
	                	else {
	                		if(!msg.equals("%%IMALIVE#")) {   //心跳检测发送信号就不必显示了
	                			ta_sys.append("【" + clientName + "#" + socket.getInetAddress() + "】："+ msg + "\n");  //若发送的信息不是聊天，则在系统窗口显示
	                			ta_sys.setCaretPosition(ta_sys.getText().length()); //滚动条自动下拉
	                		}
                			
	                	}
	                	//若连接关闭，线程自我终止    
	                	if(socket.isClosed()) {    
	                		break;
	                	}
                	}
                	else {               		
                		keepAlive();     //没有收到消息则做心跳检测          		
                	}               	
                }                
               
            } catch (Exception ex) {
            	ex.printStackTrace();
            }            
            ta_sys.append("线程："+clientName+"#"+socket.getInetAddress()+" 已经终止\n");
            
    		//用户在线列表框JList删除用户信息
    		for(int i=0;i<dlm.size();i++) {
    			if(dlm.get(i).split("#")[0].equals(clientName)) {
    				dlm.remove(i);
    				userList.setModel(dlm);
    			}
    		}
            
        }
      
        //核心代码，对发送过来的消息检测，若为系统消息则作处理。
        public boolean checkReceiveMsg(String msg) {
        	try {
        		
        		//客户端关闭
	        	if(msg.equalsIgnoreCase("%%QUIT#")) {
	        		if(!clientName.equals("")) {    //如果名字为空，说明是注销后再退出的，不必再发送一次退出群聊广播
	        			//用户在线列表框JList删除用户信息
	        			for(int i=0;i<dlm.size();i++) {
		        			if(dlm.get(i).split("#")[0].equals(clientName)) {
		        				dlm.remove(i);
		        				userList.setModel(dlm);
		        			}
		        		}
	        			sendMsgAll( "【" + clientName + "】" +"退出群聊"); 
	        		}
	        		
	        		//关闭socket连接并将线程移出线程List。	
	        		socket.close();
	        		for(int i=0;i<clientsList.size();i++) {
	        			if(clientName.equals(clientsList.get(i).clientName)) {
	        				clientsList.remove(i);	       				
	        			}
	        		}
	        		clientName="";
	        		
	        	}
	        	
	        	//客户端注销
	        	else if(msg.contains("%%LOGOUT#")) {
	        		isLogin=false;
	        		sendMsgAll( "【" + clientName + "】" +"退出群聊");
	        		//用户在线列表框JList删除用户信息	        		
	        		for(int i=0;i<dlm.size();i++) {
	        			if(dlm.get(i).split("#")[0].equals(clientName)) {
	        				dlm.remove(i);
	        				userList.setModel(dlm);
	        			}
	        		}
	        		clientName="";
	        	}
	        	
	        	//登录
	        	else if(msg.contains("%%LOGIN#")) {
	        		Boolean flag=false;   //flag检查账号密码是否正确
	        		
	            	if(nowUserNum>=maxUserNum) {	       //当聊天室满人时，向客户端提示
	            		sendMsgSelf("%%FULLUSER#",socket);	    				
	            	}
	            	else {
	            		String str[]=msg.split("#");		        		
		        		for(int i=0;i<allUserList.size();i++) {     //与userList逐个对比账号密码
		        			String user[]=allUserList.get(i).split("#");
		        			if(str[1].equals(user[1])&&str[2].equals(user[2])) {    //账号密码比对成功

		        				sendMsgSelf("%%SUCCESSLOGIN#",socket);    //向客户端发送登录成功信号
		        				
		        				dlm.addElement(user[0]+"#"+socket.getInetAddress());   //更新客户在线列表JList
		        				userList.setModel(dlm);
		        				
		        				//登录成功后的工作
		        				isLogin=true;  
		        				clientName=user[0];  
		        				msg = "欢迎【" + clientName + "】进入聊天室！当前聊天室有【"
		        							+ clientsList.size() + "】人";    
		        				sendMsgAll(msg);	        				
		        				flag=true;
		        				break;
		        			}
		        		}
	            	}
	        		
	            	//登录不成功，发送登录失败信号
	        		if(!flag) {
	        			sendMsgSelf("%%FAILLOGIN#",socket);
	        		}
	        	}
	        	
	        	//注册
	        	else if(msg.contains("%%REGISTER#")) {  
	        		String str[]=msg.split("#");
	        		Boolean flag=true;  //flag表示是否已存在的账号和昵称
	        		
	        		//查找比对是否已存在的账号和昵称
	        		for(int i=0;i<allUserList.size();i++) {
	        			String user[]=allUserList.get(i).split("#");
	        			
	        			if(str[1].equals(user[0])) {
	        				sendMsgSelf("%%FAILREGISTER#NAME",socket);   //重复昵称
	        				flag=false;
	        				break;
	        			}else if(str[2].equals(user[1])) {
	        				sendMsgSelf("%%FAILREGISTER#ACCOUNT",socket);  //重复账号
	        				flag=false;
	        				break;
	        			}
	        		}
	        		//当flag为true，表示无重名，注册成功
	        		if(flag) {
	        			sendMsgSelf("%%SUCCESSREGISTER#", socket);   //向客户端发送注册成功信号
	    				allUserList.add(str[1]+"#"+str[2]+"#"+str[3]);
	    				
	    				//注册信息存入硬盘的用户名单
	    				FileWriter fw_ac=new FileWriter("D:/user.txt",true);
	    				BufferedWriter bw_ac=new BufferedWriter(fw_ac);
	    				bw_ac.newLine();
	    				bw_ac.write(str[1]+"#"+str[2]+"#"+str[3]);   
	    				bw_ac.flush();
	    				bw_ac.close();
	    				fw_ac.close();
	    				
	    				clientName=str[1];
	        		}
	        		                		
	        	}
	        	
	        	//用户私聊
	        	else if(msg.contains("%%PRIVATETALK#")){
					 String str[]=msg.split("#");
					 int i;
					 
					 //找到要私聊的用户在clientList的下标
					 for(i=0;i<clientsList.size();i++) {
						 if(clientsList.get(i).clientName.equals(str[1])) {
							 break;
						 }
					 }
					 
					 //对双方发送私密消息
					 sendMsgSelf("你悄悄对【" + str[1] + "】说："+str[2], socket);
					 sendMsgSelf("【" + clientName + "】悄悄对你说："+str[2], clientsList.get(i).socket);
				}
	        	
	        	//心跳检测
	        	else if(msg.equals("%%IMALIVE#")) {
	        		unusedTime=0;     //收到消息，刷新未响应时间
	        	}
	        	
	        	//发送的消息不为系统信号，认定为聊天消息，返回true
	        	else {
	        		return true;
	        	}
	        	
        	} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        	return false;
        }
   
        //心跳检测函数
        public void keepAlive() {
        	unusedTime+=10;   //在run函数内每1毫秒循环一次，则每次运行未响应时间加1毫秒
        	
        	//当未响应时间超过5秒，发送信号检测连接是否正常。
        	if(unusedTime>=5000.0)    
        		sendMsgSelf("%%KEEPALIVE#", socket);
        	
        	//未响应时间超过10秒，认定该连接不正常，关闭该连接并结束进程
        	else if(unusedTime>=10000.0) {
        		try {
					socket.close();
					//移出在线用户JList
					for(int i=0;i<dlm.size();i++) {
	        			if(dlm.get(i).split("#")[0].equals(clientName)) {
	        				dlm.remove(i);
	        				userList.setModel(dlm);
	        			}
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        	}
		}
    }

    //该线程用于发送用户数量信息和心跳检测
    class WatchDogThread implements Runnable{
    	int num=0;
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			while(true) {
				num=0;
				try {					
					//对在线用户JList实时更新
//					for(int i=0;i<dlm.size();i++) {
//						boolean flag=false;
//	        			for(int j=0;j<clientsList.size();j++) {
//	        				if(dlm.get(i).split("#")[0].equals(clientsList.get(j).clientName)) {
//	        					flag=true;
//	        					break;
//	        				}
//	        				if(!flag) {
//	        					dlm.remove(i);
//	        				}
//	        			}
//	        		}
					
					//userName字符串整理后发送到每个客户端，更新他们的可私聊用户列表
					String userName = "%%USERNAME";
					for(int i=0;i<clientsList.size();i++) {
						sendMsgSelf("%%KEEPALIVE#", clientsList.get(i).socket);  //心跳检测放入该循环，节省资源
						
						if(clientsList.get(i).isLogin==true) {    //当用户已登录，则在字符串加入名字
							num++;
							userName += "#" + clientsList.get(i).clientName;
						}
                    	if(clientsList.get(i).socket.isClosed()) {    //异常连接处理
                    		clientsList.remove(i);	                    		
                    	}
					}
					
					//发送在线用户数量和他们的名字给客户端
					nowUserNum=num;
					sendMsgAll("%%USERNUM#"+num);
					sendMsgAll(userName);
					label_userNum.setText("在线用户"+num+"人");
					Thread.sleep(5000);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
			}
		}
    	
    }

    //单独发送消息给socket连接的客户端
    protected void sendMsgSelf(String msg,Socket socket) {
    	try {
    		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //广播消息
    protected void sendMsgAll(String msg) {
        try {     	
            for (int i = clientsList.size() - 1; i >= 0; i--) {

            	PrintWriter pw = new PrintWriter(clientsList.get(i).socket.getOutputStream(), true);//clientsList.get(i).getOutputStream()               	
                pw.println(msg);
//                pw.flush();
//                pw.close();
            }
        } catch (Exception ex) {
        }
    }
    
    //返回规范时间字符串
    protected String myNowTime() {
    	Calendar c=Calendar.getInstance();
		String s=String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", c);
		return s;
    }
        
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		
		//发送消息
		if(e.getSource()==btn_send || e.getSource()==tf) {			
			try {
				String msg="【 服务器广播 】：" + tf.getText();
				ta_msg.append(msg+"\n");
				bw_ch.newLine();
				bw_ch.write(myNowTime()+":"+msg);    //聊天记录存储
				sendMsgAll(msg);
				tf.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//最大聊天人数的下拉框，不允许低于当前聊天人数
		else if(e.getSource()==cb) {
			Integer n=(Integer) cb.getSelectedItem();
			if(n<nowUserNum) {
				JOptionPane.showConfirmDialog(null, "最大值不能少于当前聊天人数", "确定", JOptionPane.CLOSED_OPTION);
			}else {
				maxUserNum=n;
			}		
		}
		
		//开启服务按钮
		else if(e.getSource()==btn_startServer) {
			isStart=true;       //让主程序开始工作
			btn_startServer.setEnabled(false);
			btn_stopServer.setEnabled(true);
		}
		
		//停止服务按钮
		else if(e.getSource()==btn_stopServer) {
			isStart=false;
			sendMsgAll("%%STOP#");
			try {
				for(int i=0; i<clientsList.size() ;i++) {				
					clientsList.get(i).socket.close();						
					clientsList.remove(i);
				}
	     		   bw_ch.newLine();
	     		   bw_ch.write(myNowTime()+":服务器关闭");	
	     		   bw_ch.newLine();
	     		   bw_ch.close();
	     		   fw_ch.close();
			} catch (IOException e1) {				
				e1.printStackTrace();
			}
					
			JOptionPane.showConfirmDialog(null, "服务器停止服务", "确定", JOptionPane.CLOSED_OPTION);	
			System.exit(0);
		}
		
		//禁言按钮
		else if(e.getSource()==btn_silence){
			String str=userList.getSelectedValue();
			String name=str.split("#")[0];
			for(int i=0;i<clientsList.size();i++) {
				if(clientsList.get(i).clientName.equals(name)) {
					sendMsgSelf("%%SILENCE#", clientsList.get(i).socket);
					break;
				}				
			}
			for(int i=0;i<dlm.size();i++) {
				if(dlm.get(i).split("#")[0].equals(name)) {
					dlm.setElementAt(dlm.get(i)+"----禁言中", i);
					userList.setModel(dlm);
					break;
				}
			}
			sendMsgAll("------"+name+"已被管理员禁言 ----\n");
		}
		
		//取消禁言按钮
		else if(e.getSource()==btn_talk) {
			String str=userList.getSelectedValue();
			String name=str.split("#")[0];
			for(int i=0;i<clientsList.size();i++) {
				if(clientsList.get(i).clientName.equals(name)) {
					sendMsgSelf("%%TALK#", clientsList.get(i).socket);
					break;
				}				
			}
			for(int i=0;i<dlm.size();i++) {
				if(dlm.get(i).split("#")[0].equals(name)) {
					int index=dlm.get(i).indexOf("----");
					dlm.setElementAt(dlm.get(i).substring(0,index), i);
					userList.setModel(dlm);
					break;
				}
			}
			sendMsgAll("------"+name+"已被管理员取消禁言 ----\n");
		}
		
		//踢出聊天室按钮
		else if(e.getSource()==btn_kickout) {
			String str=userList.getSelectedValue();
			String name=str.split("#")[0];
			for(int i=0;i<clientsList.size();i++) {
				if(clientsList.get(i).clientName.equals(name)) {
					sendMsgSelf("%%KICKOUT#", clientsList.get(i).socket);
					try {
						clientsList.get(i).socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					clientsList.remove(i);
					break;
				}				
			}
			for(int i=0;i<dlm.size();i++) {
				if(dlm.get(i).split("#")[0].equals(name)) {
					dlm.remove(i);
					userList.setModel(dlm);
					break;
				}
			}
			sendMsgAll("------"+name+"已被管理员踢出群聊 ----\n");
		}
	}

}

 
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Server {
//    private List<ThreadServer> clients=new ArrayList<ThreadServer>();
//    public void startup() throws IOException {
//        System.out.println("监听5001端口");
//        ServerSocket ss=new ServerSocket(5001);
//        while(true){
//            Socket socket=ss.accept();
//            System.out.println("发现新用户");
//            Thread st=new Thread(new ThreadServer(socket));
//            st.start();
//        }
//    }
//    
//    public class ThreadServer implements Runnable{
//        private Socket socket;
//        private BufferedReader br;
//        private PrintWriter out;
//        private String name;
//        private Boolean flag=true;
//        public ThreadServer(Socket socket) throws IOException {
//            this.socket=socket;
//            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out=new PrintWriter(socket.getOutputStream(),true);
//            String str=br.readLine();
//            name=str+"["+socket.getInetAddress().getHostAddress()+":"+socket.getPort()+"]";
//            System.out.println(name+"加入该聊天室");
//            send(name+"加入该聊天室");
//            clients.add(this);
//        }
//        private void send(String message) {
//            for (ThreadServer threadServer : clients) {
//                System.out.println("-->已向线程"+threadServer.name+"发送消息");
//                threadServer.out.print(message);
//                threadServer.out.flush();
//            }
//        }
//        private void receive() throws IOException {
//            String message;
//            while(flag=true) {
//                message=br.readLine();
//                if(message.equalsIgnoreCase("quit")) {
//                    System.out.println("用户"+name+"退出了");
//                    out.println("quit");
//                    out.flush();
//                    clients.remove(this);
//                    flag=false;
//                }
//                System.out.println(name+":"+message);
//                send(name+":"+message);
//            }
//        }
//        @Override
//        public void run() {
//            try {
//                while(flag=true) {
//                    receive();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    
//    public static void main(String []args) throws IOException {
//        Server server=new Server();
//        System.out.println("服务器开启");
//        server.startup();
//    }
//    
//}