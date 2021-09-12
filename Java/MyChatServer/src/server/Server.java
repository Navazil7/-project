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
    FileWriter fw_ch;    //�����¼�ļ���
    BufferedWriter bw_ch;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
    	super("������");
    	
    	//��D��txt�ļ��ж�ȡ�û�����
    	try {
    		allUserList=new ArrayList<String>();
    		FileReader fr_ac=new FileReader("D:/user.txt");
    		BufferedReader br_ac=new BufferedReader(fr_ac);

			for(;br_ac.ready();) {
				allUserList.add(br_ac.readLine());
			}
			
			ta_sys.append("�û���Ϣ�ܱ�" + "\n"); 
            for(String i:allUserList) {
            	ta_sys.append(i+"\n");
            }		
        	br_ac.close();
        	fr_ac.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}

    	//��chat history�ļ�д������׼����д�������¼��ʱ����Ϣ
    	try {
			fw_ch=new FileWriter("D:/chat history.txt",true);
			bw_ch=new BufferedWriter(fw_ch);
			bw_ch.newLine();
			bw_ch.write("----------"+myNowTime()+"----------");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	cb.addActionListener(this); //������ע��
    	tf.addActionListener(this); //�����ע��
		btn_send.addActionListener(this);//������Ϣ��ťע��
		btn_startServer.addActionListener(this);//��ʼ������Ϣ��ťע��
		btn_stopServer.addActionListener(this);//ֹͣ������Ϣ��ťע��
		btn_silence.addActionListener(this);//���԰�ť
		btn_talk.addActionListener(this);//ȡ�����԰�ť
		btn_kickout.addActionListener(this);//�߳������Ұ�ť
    	//ע��رմ����¼����ر�ǰ������д��txt�ļ�
    	addWindowListener(new WindowAdapter() { 
	        public void windowClosing(WindowEvent e) {
	           int i=JOptionPane.showConfirmDialog(null, "ȷ��Ҫ�˳���������", "�˳�ϵͳ", JOptionPane.YES_NO_OPTION);
	           if(i==JOptionPane.YES_OPTION){	
	        	   //�رճ���ǰ�Ĵ���
	        	   try {
	        		   //����û�����
	        		   sendMsgAll("%%STOP#");
//	       				for(ClientThread ct:clientsList) {	       				
//	       					ct.socket.close();
//	       					clientsList.remove(ct);	       								
//	       				}
	        		   
	        		   //���ݱ��浽�ļ���
	        		   bw_ch.newLine();
	        		   bw_ch.write(myNowTime()+":�������ر�");	
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
    	
    	//�ⲿ������
        try {
        	while(true) {
        		Thread.sleep(10);    //�ó�������1���룬��ֹѭ��������Դ����
        		
        		//����ʼǰ��׼������
        		if(isStart) {      //isStart��ʾֹͣ�����ʼ����       			
        			ta_sys.append("��������ʼ����\n");  
	                port = 8080; //8080
	                clientsList = new ArrayList<ClientThread>();  
	                server = new ServerSocket(port);	
	                Thread userNum=new Thread(new WatchDogThread());
	                userNum.start();	
	                
	                //�ڲ�������ServerSocket����ʼ���տͻ��˵�Socket����
	                while (true) {	                	
	                	Thread.sleep(10);	
	                	if(isStart==false)
	                		break; 	                	
	                	Socket socket = server.accept(); //�����ͣ���ڴ�
	                    ClientThread clientthread=new ClientThread(socket); //Ϊÿ���ͻ��¿�һ���߳�
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

    //�ͻ����߳���
    class ClientThread implements Runnable {
        Socket socket;   //�ͻ���Socket��
        BufferedReader br;  //��ȡ�ͻ��˷��͵�����
        boolean isLogin=false;   //�ͻ����Ƿ��¼
        double unusedTime = 0;   //��¼�߳�����ʱ�䣬��run������ˢ�£���ʱ�䳬��5���������keepAlive���������������
        
        public String msg;  
        private String clientName="";  //����Ϊ�����������Ƿ��ѵ�¼
        

        public ClientThread(Socket s) {
            socket = s; 
        }

        //������
        public void run() {

            try {
            	//���ͻ�������ʱ��׼������
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ta_sys.append("�̣߳�"+clientName+"#"+socket.getInetAddress()+" ����\n");
                
               //���տͻ�����Ϣ
                while (true) {   
                	Thread.sleep(10);               	
                	if((msg = br.readLine()) != null  ) {//&& !socket.isClosed()
                		unusedTime=0;  //ˢ�³���δ��Ӧʱ��

                		//checkReceiveMsg������鷢�͵���Ϣ����Ϊϵͳ��Ϣ��������������false����Ϊ������Ϣ����true
	                	if(checkReceiveMsg(msg)) {
	                		ta_msg.append("��" + clientName  + "��˵��\n"+ msg + "\n");  //������Ϣ����������
	                		ta_msg.setCaretPosition(ta_msg.getText().length());  //�������Զ�����
	                		msg = "��" +  clientName + "��˵��\n" + msg;
	                		sendMsgAll(msg);   //������Ϣ���͸������û�
	                		
	                		bw_ch.newLine();   
	                		bw_ch.write(myNowTime()+":"+msg);    //�����¼�洢
	                	}
	                	//ϵͳ��Ϣ�����ϵͳ��Ϣ��
	                	else {
	                		if(!msg.equals("%%IMALIVE#")) {   //������ⷢ���źžͲ�����ʾ��
	                			ta_sys.append("��" + clientName + "#" + socket.getInetAddress() + "����"+ msg + "\n");  //�����͵���Ϣ�������죬����ϵͳ������ʾ
	                			ta_sys.setCaretPosition(ta_sys.getText().length()); //�������Զ�����
	                		}
                			
	                	}
	                	//�����ӹرգ��߳�������ֹ    
	                	if(socket.isClosed()) {    
	                		break;
	                	}
                	}
                	else {               		
                		keepAlive();     //û���յ���Ϣ�����������          		
                	}               	
                }                
               
            } catch (Exception ex) {
            	ex.printStackTrace();
            }            
            ta_sys.append("�̣߳�"+clientName+"#"+socket.getInetAddress()+" �Ѿ���ֹ\n");
            
    		//�û������б��JListɾ���û���Ϣ
    		for(int i=0;i<dlm.size();i++) {
    			if(dlm.get(i).split("#")[0].equals(clientName)) {
    				dlm.remove(i);
    				userList.setModel(dlm);
    			}
    		}
            
        }
      
        //���Ĵ��룬�Է��͹�������Ϣ��⣬��Ϊϵͳ��Ϣ��������
        public boolean checkReceiveMsg(String msg) {
        	try {
        		
        		//�ͻ��˹ر�
	        	if(msg.equalsIgnoreCase("%%QUIT#")) {
	        		if(!clientName.equals("")) {    //�������Ϊ�գ�˵����ע�������˳��ģ������ٷ���һ���˳�Ⱥ�Ĺ㲥
	        			//�û������б��JListɾ���û���Ϣ
	        			for(int i=0;i<dlm.size();i++) {
		        			if(dlm.get(i).split("#")[0].equals(clientName)) {
		        				dlm.remove(i);
		        				userList.setModel(dlm);
		        			}
		        		}
	        			sendMsgAll( "��" + clientName + "��" +"�˳�Ⱥ��"); 
	        		}
	        		
	        		//�ر�socket���Ӳ����߳��Ƴ��߳�List��	
	        		socket.close();
	        		for(int i=0;i<clientsList.size();i++) {
	        			if(clientName.equals(clientsList.get(i).clientName)) {
	        				clientsList.remove(i);	       				
	        			}
	        		}
	        		clientName="";
	        		
	        	}
	        	
	        	//�ͻ���ע��
	        	else if(msg.contains("%%LOGOUT#")) {
	        		isLogin=false;
	        		sendMsgAll( "��" + clientName + "��" +"�˳�Ⱥ��");
	        		//�û������б��JListɾ���û���Ϣ	        		
	        		for(int i=0;i<dlm.size();i++) {
	        			if(dlm.get(i).split("#")[0].equals(clientName)) {
	        				dlm.remove(i);
	        				userList.setModel(dlm);
	        			}
	        		}
	        		clientName="";
	        	}
	        	
	        	//��¼
	        	else if(msg.contains("%%LOGIN#")) {
	        		Boolean flag=false;   //flag����˺������Ƿ���ȷ
	        		
	            	if(nowUserNum>=maxUserNum) {	       //������������ʱ����ͻ�����ʾ
	            		sendMsgSelf("%%FULLUSER#",socket);	    				
	            	}
	            	else {
	            		String str[]=msg.split("#");		        		
		        		for(int i=0;i<allUserList.size();i++) {     //��userList����Ա��˺�����
		        			String user[]=allUserList.get(i).split("#");
		        			if(str[1].equals(user[1])&&str[2].equals(user[2])) {    //�˺�����ȶԳɹ�

		        				sendMsgSelf("%%SUCCESSLOGIN#",socket);    //��ͻ��˷��͵�¼�ɹ��ź�
		        				
		        				dlm.addElement(user[0]+"#"+socket.getInetAddress());   //���¿ͻ������б�JList
		        				userList.setModel(dlm);
		        				
		        				//��¼�ɹ���Ĺ���
		        				isLogin=true;  
		        				clientName=user[0];  
		        				msg = "��ӭ��" + clientName + "�����������ң���ǰ�������С�"
		        							+ clientsList.size() + "����";    
		        				sendMsgAll(msg);	        				
		        				flag=true;
		        				break;
		        			}
		        		}
	            	}
	        		
	            	//��¼���ɹ������͵�¼ʧ���ź�
	        		if(!flag) {
	        			sendMsgSelf("%%FAILLOGIN#",socket);
	        		}
	        	}
	        	
	        	//ע��
	        	else if(msg.contains("%%REGISTER#")) {  
	        		String str[]=msg.split("#");
	        		Boolean flag=true;  //flag��ʾ�Ƿ��Ѵ��ڵ��˺ź��ǳ�
	        		
	        		//���ұȶ��Ƿ��Ѵ��ڵ��˺ź��ǳ�
	        		for(int i=0;i<allUserList.size();i++) {
	        			String user[]=allUserList.get(i).split("#");
	        			
	        			if(str[1].equals(user[0])) {
	        				sendMsgSelf("%%FAILREGISTER#NAME",socket);   //�ظ��ǳ�
	        				flag=false;
	        				break;
	        			}else if(str[2].equals(user[1])) {
	        				sendMsgSelf("%%FAILREGISTER#ACCOUNT",socket);  //�ظ��˺�
	        				flag=false;
	        				break;
	        			}
	        		}
	        		//��flagΪtrue����ʾ��������ע��ɹ�
	        		if(flag) {
	        			sendMsgSelf("%%SUCCESSREGISTER#", socket);   //��ͻ��˷���ע��ɹ��ź�
	    				allUserList.add(str[1]+"#"+str[2]+"#"+str[3]);
	    				
	    				//ע����Ϣ����Ӳ�̵��û�����
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
	        	
	        	//�û�˽��
	        	else if(msg.contains("%%PRIVATETALK#")){
					 String str[]=msg.split("#");
					 int i;
					 
					 //�ҵ�Ҫ˽�ĵ��û���clientList���±�
					 for(i=0;i<clientsList.size();i++) {
						 if(clientsList.get(i).clientName.equals(str[1])) {
							 break;
						 }
					 }
					 
					 //��˫������˽����Ϣ
					 sendMsgSelf("�����Ķԡ�" + str[1] + "��˵��"+str[2], socket);
					 sendMsgSelf("��" + clientName + "�����Ķ���˵��"+str[2], clientsList.get(i).socket);
				}
	        	
	        	//�������
	        	else if(msg.equals("%%IMALIVE#")) {
	        		unusedTime=0;     //�յ���Ϣ��ˢ��δ��Ӧʱ��
	        	}
	        	
	        	//���͵���Ϣ��Ϊϵͳ�źţ��϶�Ϊ������Ϣ������true
	        	else {
	        		return true;
	        	}
	        	
        	} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
        	return false;
        }
   
        //������⺯��
        public void keepAlive() {
        	unusedTime+=10;   //��run������ÿ1����ѭ��һ�Σ���ÿ������δ��Ӧʱ���1����
        	
        	//��δ��Ӧʱ�䳬��5�룬�����źż�������Ƿ�������
        	if(unusedTime>=5000.0)    
        		sendMsgSelf("%%KEEPALIVE#", socket);
        	
        	//δ��Ӧʱ�䳬��10�룬�϶������Ӳ��������رո����Ӳ���������
        	else if(unusedTime>=10000.0) {
        		try {
					socket.close();
					//�Ƴ������û�JList
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

    //���߳����ڷ����û�������Ϣ���������
    class WatchDogThread implements Runnable{
    	int num=0;
		@Override
		public void run() {
			// TODO �Զ����ɵķ������
			while(true) {
				num=0;
				try {					
					//�������û�JListʵʱ����
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
					
					//userName�ַ���������͵�ÿ���ͻ��ˣ��������ǵĿ�˽���û��б�
					String userName = "%%USERNAME";
					for(int i=0;i<clientsList.size();i++) {
						sendMsgSelf("%%KEEPALIVE#", clientsList.get(i).socket);  //�����������ѭ������ʡ��Դ
						
						if(clientsList.get(i).isLogin==true) {    //���û��ѵ�¼�������ַ�����������
							num++;
							userName += "#" + clientsList.get(i).clientName;
						}
                    	if(clientsList.get(i).socket.isClosed()) {    //�쳣���Ӵ���
                    		clientsList.remove(i);	                    		
                    	}
					}
					
					//���������û����������ǵ����ָ��ͻ���
					nowUserNum=num;
					sendMsgAll("%%USERNUM#"+num);
					sendMsgAll(userName);
					label_userNum.setText("�����û�"+num+"��");
					Thread.sleep(5000);
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}
			}
		}
    	
    }

    //����������Ϣ��socket���ӵĿͻ���
    protected void sendMsgSelf(String msg,Socket socket) {
    	try {
    		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //�㲥��Ϣ
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
    
    //���ع淶ʱ���ַ���
    protected String myNowTime() {
    	Calendar c=Calendar.getInstance();
		String s=String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", c);
		return s;
    }
        
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO �Զ����ɵķ������
		
		//������Ϣ
		if(e.getSource()==btn_send || e.getSource()==tf) {			
			try {
				String msg="�� �������㲥 ����" + tf.getText();
				ta_msg.append(msg+"\n");
				bw_ch.newLine();
				bw_ch.write(myNowTime()+":"+msg);    //�����¼�洢
				sendMsgAll(msg);
				tf.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//������������������򣬲�������ڵ�ǰ��������
		else if(e.getSource()==cb) {
			Integer n=(Integer) cb.getSelectedItem();
			if(n<nowUserNum) {
				JOptionPane.showConfirmDialog(null, "���ֵ�������ڵ�ǰ��������", "ȷ��", JOptionPane.CLOSED_OPTION);
			}else {
				maxUserNum=n;
			}		
		}
		
		//��������ť
		else if(e.getSource()==btn_startServer) {
			isStart=true;       //��������ʼ����
			btn_startServer.setEnabled(false);
			btn_stopServer.setEnabled(true);
		}
		
		//ֹͣ����ť
		else if(e.getSource()==btn_stopServer) {
			isStart=false;
			sendMsgAll("%%STOP#");
			try {
				for(int i=0; i<clientsList.size() ;i++) {				
					clientsList.get(i).socket.close();						
					clientsList.remove(i);
				}
	     		   bw_ch.newLine();
	     		   bw_ch.write(myNowTime()+":�������ر�");	
	     		   bw_ch.newLine();
	     		   bw_ch.close();
	     		   fw_ch.close();
			} catch (IOException e1) {				
				e1.printStackTrace();
			}
					
			JOptionPane.showConfirmDialog(null, "������ֹͣ����", "ȷ��", JOptionPane.CLOSED_OPTION);	
			System.exit(0);
		}
		
		//���԰�ť
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
					dlm.setElementAt(dlm.get(i)+"----������", i);
					userList.setModel(dlm);
					break;
				}
			}
			sendMsgAll("------"+name+"�ѱ�����Ա���� ----\n");
		}
		
		//ȡ�����԰�ť
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
			sendMsgAll("------"+name+"�ѱ�����Աȡ������ ----\n");
		}
		
		//�߳������Ұ�ť
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
			sendMsgAll("------"+name+"�ѱ�����Ա�߳�Ⱥ�� ----\n");
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
//        System.out.println("����5001�˿�");
//        ServerSocket ss=new ServerSocket(5001);
//        while(true){
//            Socket socket=ss.accept();
//            System.out.println("�������û�");
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
//            System.out.println(name+"�����������");
//            send(name+"�����������");
//            clients.add(this);
//        }
//        private void send(String message) {
//            for (ThreadServer threadServer : clients) {
//                System.out.println("-->�����߳�"+threadServer.name+"������Ϣ");
//                threadServer.out.print(message);
//                threadServer.out.flush();
//            }
//        }
//        private void receive() throws IOException {
//            String message;
//            while(flag=true) {
//                message=br.readLine();
//                if(message.equalsIgnoreCase("quit")) {
//                    System.out.println("�û�"+name+"�˳���");
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
//        System.out.println("����������");
//        server.startup();
//    }
//    
//}