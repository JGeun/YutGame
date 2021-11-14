
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;

public class YutGameClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 900;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	private Image mainBackground = new ImageIcon(YutGameClientView.class.getResource("images/bg_main.jpg")).getImage();
	private Image background = new ImageIcon(YutGameClientView.class.getResource("images/bg_gamepanel.jpg")).getImage();
	private ImageIcon gameSmallSpot = new ImageIcon(
			YutGameClientView.class.getResource("images/ic_game_small_spot.png"));
	private ImageIcon gameBigSpot = new ImageIcon(YutGameClientView.class.getResource("images/ic_game_big_spot.png"));

	/**
	 * Create the frame.
	 */
	public YutGameClientView(String username, String ip_addr, String port_no) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, SCREEN_WIDTH, SCREEN_HEIGHT);
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(mainBackground, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(background, 0, 0, null);
				// ��
				g.drawImage(gameBigSpot.getImage(), 40, 30, 80, 80, this);
				g.drawImage(gameSmallSpot.getImage(), 180, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 300, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 420, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 540, 40, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 660, 30, 80, 80, this);

				// ����
				g.drawImage(gameSmallSpot.getImage(), 50, 150, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 250, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 350, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 450, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 40, 550, 80, 80, this);

				// �Ʒ�
				g.drawImage(gameSmallSpot.getImage(), 180, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 300, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 420, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 540, 560, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 660, 550, 80, 80, this);

				// ������
				g.drawImage(gameSmallSpot.getImage(), 670, 150, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 250, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 350, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 450, 60, 60, this);

				// �밢�� ���� -> ������
				g.drawImage(gameSmallSpot.getImage(), 170, 140, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 260, 210, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 350, 280, 80, 80, this);
				g.drawImage(gameSmallSpot.getImage(), 460, 370, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 550, 450, 60, 60, this);

				// �밢�� ������ -> ����
				g.drawImage(gameSmallSpot.getImage(), 550, 140, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 460, 210, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 260, 370, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 170, 450, 60, 60, this);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		panel.setBounds(24, 10, 780, 650);
		contentPane.add(panel);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(24, 680, 135, 169);
		contentPane.add(panel_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(243, 680, 135, 169);
		contentPane.add(panel_2);

		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBounds(458, 680, 135, 169);
		contentPane.add(panel_2_1);

		JPanel panel_2_1_1 = new JPanel();
		panel_2_1_1.setBounds(669, 680, 135, 169);
		contentPane.add(panel_2_1_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(828, 538, 424, 181);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(true);
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));

		txtInput = new JTextField();
		txtInput.setBounds(890, 729, 281, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.setBounds(1183, 728, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("����", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(828, 778, 62, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("����", Font.PLAIN, 16));
		imgBtn.setBounds(828, 727, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("�� ��");
		btnNewButton.setFont(new Font("����", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendChatMsg(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(1183, 778, 69, 40);
		contentPane.add(btnNewButton);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(829, 22, 423, 353);
		contentPane.add(panel_3);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		chckbxNewCheckBox.setBounds(828, 487, 115, 23);
		contentPane.add(chckbxNewCheckBox);
		
		JButton btnNewButton_1 = new JButton("New button");
		btnNewButton_1.setBounds(1115, 487, 97, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("New button");
		btnNewButton_2.setBounds(993, 396, 97, 23);
		contentPane.add(btnNewButton_2);

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendChatMsg(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
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
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			AppendText("ReadChatMsg Error");
			e.printStackTrace();
			try {
				oos.close();
				socket.close();
				ois.close();
				socket = null;
				return null;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				try {
					oos.close();
					socket.close();
					ois.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				socket = null;
				return null;
			}

			// textArea.append("�޼��� �۽� ����!!\n");
			// System.exit(0);
		}

		return cm;
	}

	// Server Message�� �����ؼ� ȭ�鿡 ǥ��
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				ChatMsg cm = ReadChatMsg();
				if (cm == null)
					break;
				if (socket == null)
					break;
				String msg;
				msg = String.format("[%s] %s", cm.UserName, cm.data);
				switch (cm.code) {
				case "200": // chat message
					AppendText(msg);
					break;
				case "300": // Image ÷��
					AppendText("[" + cm.UserName + "]" + " " + cm.data);
					// AppendImage(cm.img);
					AppendImageBytes(cm.imgbytes);

					break;
				}

			}
		}
	}

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				if (msg.contains("/exit")) // ���� ó��
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == imgBtn) {
				frame = new Frame("�̹���÷��");
				fd = new FileDialog(frame, "�̹��� ����", FileDialog.LOAD);
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", fd.getFile());
//					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
//					obcm.img = img;
//					SendChatMsg(obcm);

					BufferedImage bImage = null;
					String filename = fd.getDirectory() + fd.getFile();
					try {
						bImage = ImageIO.read(new File(filename));
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					try {
						ImageIO.write(bImage, "jpg", bos);
						byte[] data = bos.toByteArray();
						bos.close();
						obcm.imgbytes = data;
						// AppendImageBytes(data);

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					SendChatMsg(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// ������ �̵�
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// ȭ�鿡 ���
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();
		// ������ �̵�
		textArea.setCaretPosition(len);
		textArea.replaceSelection(msg + "\n");
	}

	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image�� �ʹ� ũ�� �ִ� ���� �Ǵ� ���� 200 �������� ��ҽ�Ų��.
		if (width > 200 || height > 200) {
			if (width > height) { // ���� ����
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // ���� ����
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			Image new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);

		} else
			textArea.insertIcon(ori_icon);
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
	}

	public void AppendImageBytes(byte[] imgbytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(imgbytes);
		BufferedImage ori_img = null;
		try {
			ori_img = ImageIO.read(bis);
			bis.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageIcon new_icon = new ImageIcon(ori_img);
		AppendImage(new_icon);
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
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server���� network���� ����
	public void SendMessage(String msg) {
		ChatMsg obcm = new ChatMsg(UserName, "200", msg);
		SendChatMsg(obcm);
	}

	// �ϳ��� Message ������ �Լ�
	// Android�� ȣȯ���� ���� code, UserName, data ��� ���� �����Ѵ�.
	public void SendChatMsg(ChatMsg obj) {
		try {
			oos.writeObject(obj.code);
			oos.writeObject(obj.UserName);
			oos.writeObject(obj.data);
			if (obj.code.equals("300")) { // �̹��� ÷�� �ִ� ���
				oos.writeObject(obj.imgbytes);
			}
			oos.flush();
		} catch (IOException e) {
			AppendText("SendChatMsg Error");
			e.printStackTrace();
			try {
				oos.close();
				socket.close();
				ois.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// textArea.append("�޼��� �۽� ����!!\n");
			// System.exit(0);
		}
	}
}
