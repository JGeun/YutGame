//JavaObjServer.java ObjectStream ��� ä�� Server

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class YutGameServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	private boolean[] userConnect = new boolean[4];
	private int playTurnIdx = 0;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					YutGameServer frame = new YutGameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public YutGameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�

				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					AppendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					AppendText("���� ������ �� " + UserVec.size());
					System.out.println("���� ������ �� ���: " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String imagepath = "";
		public int userIdx = -1;
		public int[] userGameObjectPos = new int[] {-1, -1, -1, -1};
		public int restObjectCnt = 4;
		public boolean isOwner = false;
		public boolean isReady = false;
		private int userSelectObjCnt = 0;
		private List<Integer> rollResultList = new ArrayList();
		
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// �Ű������� �Ѿ�� �ڷ� ����
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void Login() {
			int index = 0;
			while (index < 4) {
				if (!userConnect[index])
					break;
				index += 1;
			}
			System.out.println("index: " + index);
			if (index != 4) {
				if (UserVec.size() == 1)
					this.isOwner = true;
				System.out.println("if�� ������ ����");
				userConnect[index] = true;
				userIdx = index;
				AppendText("���ο� ������ " + UserName + " ����.");
				WriteOne(UserName + "�� �Ѽ� �����̿� ���� ���� ȯ���մϴ�!\n"); // ����� ����ڿ��� ���������� �˸�
				SendUserIdx();
				String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
				WriteOthers(msg); // ���� user_vc�� ���� ������ user�� ���Ե��� �ʾҴ�.
				SendUserInfo();
			}
		}

		public void Logout() {
			String msg = "[" + UserName + "]���� ���� �Ͽ����ϴ�.\n";
			userConnect[this.userIdx] = false;
			boolean isLogoutUserHaveOwner = this.isOwner;
			UserVec.removeElement(this); // Logout�� ���� ��ü�� ���Ϳ��� �����
			WriteAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			this.client_socket = null;
			AppendText("����� " + "[" + UserName + "] ����. ���� ������ �� " + UserVec.size());
			if (UserVec.size() != 0 && isLogoutUserHaveOwner) {
				UserService user = (UserService) UserVec.elementAt(0);
				user.isOwner = true;
			}
			SendUserInfo();
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteOne(str);
			}
		}

		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void WriteAllObject(ChatMsg obj) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				user.WriteChatMsg(obj);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� WriteONe() �� ȣ���Ѵ�.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this)
					user.WriteOne(str);
			}
		}

		// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		public void SendUserIdx() {
			ChatMsg obcm = new ChatMsg("SERVER", "101", Integer.toString(userIdx));
			WriteChatMsg(obcm);
		}

		public void SendUserInfo() {
			System.out.println("SendUserIfno" + UserVec.size());
			StringBuilder data = new StringBuilder("");
			for (int i = 0; i < UserVec.size(); i++) {
				UserService user = (UserService) UserVec.elementAt(i);
				data.append(user.userIdx).append(' ').append(user.UserName).append(' ').append(user.isOwner).append(' ')
						.append(user.isReady).append(' ');
			}
			
			for (int i = 0; i < UserVec.size(); i++) {
				UserService user = (UserService) UserVec.elementAt(i);
				ChatMsg obcm = new ChatMsg("SERVER", "102", data.toString());
				user.WriteChatMsg(obcm);
			}
		}

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
		public void WriteOne(String msg) {
			ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
			System.out.println("obcm: " + obcm.data);
			WriteChatMsg(obcm);
		}

		// �ӼӸ� ����
		public void WritePrivate(String msg) {
			ChatMsg obcm = new ChatMsg("�ӼӸ�", "200", msg);
			WriteChatMsg(obcm);
		}

		//
		public void WriteChatMsg(ChatMsg obj) {
			try {
				oos.writeObject(obj.code);
				oos.writeObject(obj.UserName);
				oos.writeObject(obj.data);
//			    if (obj.code.equals("300")) {
//				    oos.writeObject(obj.imgbytes);
//				    //oos.writeObject(obj.bimg);
//			    }
			} catch (IOException e) {
				AppendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();

			}
		}

		public ChatMsg ReadChatMsg() {
			Object obj = null;
			String msg = null;
			ChatMsg cm = new ChatMsg("", "", "");
			// Android�� ȣȯ���� ���� ������ Field�� ���ε��� �д´�.
			try {
				obj = ois.readObject();
				cm.code = (String) obj;
				obj = ois.readObject();
				cm.UserName = (String) obj;
				obj = ois.readObject();
				cm.data = (String) obj;
				if (cm.code.equals("300")) {
					obj = ois.readObject();
					cm.imgbytes = (byte[]) obj;
//					obj = ois.readObject();
//					cm.bimg = (BufferedImage) obj;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Logout();
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logout();
				return null;
			}
			return cm;
		}

		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				ChatMsg cm = null;
				if (client_socket == null)
					break;
				cm = ReadChatMsg();
				if (cm == null)
					break;
				if (cm.code.length() == 0)
					break;
				AppendObject(cm);
				if (cm.code.matches("100")) {
					UserName = cm.UserName;
					System.out.println("100����");
					Login();
				} else if (cm.code.matches("103")) {
					this.isReady = !this.isReady;
					SendUserInfo();
				} else if (cm.code.matches("104")) {
					int readyCnt = 0;
					for (int i = 0; i < UserVec.size(); i++) {
						UserService user = (UserService) UserVec.elementAt(i);
						if (!user.isOwner && user.isReady)
							readyCnt += 1;
					}
					ChatMsg obcm = null;
					if (UserVec.size() == 1) {
						obcm = new ChatMsg("SERVER", "105", "false NoUser");
					} else if (readyCnt == UserVec.size() - 1) {
						obcm = new ChatMsg("SERVER", "105", "true");
					} else {
						obcm = new ChatMsg("SERVER", "105", "false NoReady");
					}

					// ������ ���� �� ���������.
					for (int i = 0; i < UserVec.size(); i++) {
						UserService user = (UserService) UserVec.elementAt(i);
						user.WriteChatMsg(obcm);
					}
				
					if (readyCnt == UserVec.size() - 1) {
						obcm = new ChatMsg("SERVER", "500",  cm.UserName + " " +playTurnIdx+ " true");
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							user.WriteChatMsg(obcm);
						}
					}

				} else if (cm.code.matches("200")) {
					String msg = String.format("[%s] %s", cm.UserName, cm.data);
					AppendText(msg); // server ȭ�鿡 ���
					String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
					if (args.length == 1) { // Enter key �� ���� ��� Wakeup ó���� �Ѵ�.
						// UserStatus = "O";
					} else if (args[1].matches("/exit")) {
						Logout();
						break;
					} else if (args[1].matches("/list")) {
						WriteOne("User list\n");
						WriteOne("Name\tStatus\n");
						WriteOne("-----------------------------\n");
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							WriteOne(user.UserName + "\n");
						}
						WriteOne("-----------------------------\n");
					} else if (args[1].matches("/to")) { // �ӼӸ�
						for (int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if (user.UserName.matches(args[2])) {
								String msg2 = "";
								for (int j = 3; j < args.length; j++) {// ���� message �κ�
									msg2 += args[j];
									if (j < args.length - 1)
										msg2 += " ";
								}
								// /to ����.. [�ӼӸ�] [user1] Hello user2..
								user.WritePrivate(args[0] + " " + msg2 + "\n");
								// user.WriteOne("[�ӼӸ�] " + args[0] + " " + msg2 + "\n");
								break;
							}
						}
					} else { // �Ϲ� ä�� �޽���
						// WriteAll(msg + "\n"); // Write All
						WriteAllObject(cm);
					}
				} else if (cm.code.matches("400")) { // logout message ó��
					Logout();
					break;
				} else if (cm.code.matches("300")) {
					WriteAllObject(cm);
				}else if(cm.code.matches("501")) {
					boolean hasBackShow = false;
					int yutRollValue = 1;
					int yutCnt = 0;
					String[] yutRollResult = cm.data.split(" ");
					for (int i = 0; i < 4; i++) {
						int yut = Integer.parseInt(yutRollResult[i]);
						if (yut != 1) {
							yutCnt+=1;
							if (yut == -1) {
								hasBackShow = true;
							}
						}
					}
					
					switch (yutCnt) {
					case 1:
						if (hasBackShow)
							yutRollValue = -1;
						else
							yutRollValue = 1;
						break;
					case 2:
						yutRollValue = 2;
						break;
					case 3:
						yutRollValue = 3;
						break;
					case 4:
						yutRollValue = 4;
						break;
					case 0:
						yutRollValue = 5;
						break;
					}
					
					rollResultList.add(yutRollValue);
					
					System.out.println("yutroll Server" + cm.data + yutRollValue);
					ChatMsg obcm = new ChatMsg("SERVER", "501", cm.data + yutRollValue + " " + UserName);
					for (int i = 0; i < UserVec.size(); i++){
						UserService user = (UserService) UserVec.elementAt(i);
						user.WriteChatMsg(obcm);
					}
					
					if(yutRollValue == 4 || yutRollValue == 5) {
						obcm = new ChatMsg("SERVER", "502", "roll again");
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							user.WriteChatMsg(obcm);
						}
					}else {
						StringBuilder sb = new StringBuilder("");
						for(int i=0; i<rollResultList.size(); i++) sb.append(rollResultList.get(i)).append(' ');
						obcm = new ChatMsg("SERVER", "503", sb.toString());
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							user.WriteChatMsg(obcm);
						}
					}			
				}else if(cm.code.matches("504")) {
					System.out.println("���� 504����");
					this.userSelectObjCnt+=1;
					System.out.println("UserSelectObjCnt: " + this.userSelectObjCnt +" size: " + rollResultList.size());
					
					System.out.println("data: " + cm.data);
					//������ �� ó��
					if(cm.data.contains("new")) {
						System.out.println("new object ����");
						String[] arrowResult = cm.data.split(" ");
						for(int i=0; i<4; i++) {
							if(userGameObjectPos[i] == -1) {
								userGameObjectPos[i] = Integer.parseInt(arrowResult[2]); break;
							}
						}
						
						
						StringBuilder allObjectMsg = new StringBuilder("");
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							allObjectMsg.append("user").append(' ').append(user.userIdx).append(' ');
							
							for(int j=0; j<user.userGameObjectPos.length; j++) {
								if(user.userGameObjectPos[j] != -1) {
									allObjectMsg.append(j).append(' ').append(user.userGameObjectPos[j]).append(' ');
								}
							}
						}
						System.out.println("504������ msg" +  allObjectMsg.toString());
						ChatMsg obcm = new ChatMsg("SERVER", "504",  allObjectMsg.toString());
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							user.WriteChatMsg(obcm);
						}
					}else {
						
					}
					
					if(userSelectObjCnt >= rollResultList.size()) {
						System.out.println("-------------------------------------");
						System.out.println("Cnt == Size");
						this.userSelectObjCnt = 0;
						this.rollResultList.clear();
						System.out.println("�ʱ�ȭ userCnt: " + userSelectObjCnt + " size: "+rollResultList.size());
						System.out.println("-------------------------------------");
						playTurnIdx += 1;
						int turn = playTurnIdx%UserVec.size();
						String nextUserName = "";
						for(int i=0; i<UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							if(user.userIdx == turn) {
								nextUserName = user.UserName;
								break;
							}
						}
						
						ChatMsg obcm = new ChatMsg("SERVER", "500",  nextUserName + " " +turn + " true");
						for (int i = 0; i < UserVec.size(); i++) {
							UserService user = (UserService) UserVec.elementAt(i);
							user.WriteChatMsg(obcm);
						}
					}
				}
			} // while
		} // run
	}
}
