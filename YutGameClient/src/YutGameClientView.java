
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.*;

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
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
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
	private JButton btnGameStart;
	private JButton btnRollYut;

	private Image mainBackground = new ImageIcon(YutGameClientView.class.getResource("images/bg_main.jpg")).getImage();
	private Image yutBackground = new ImageIcon(YutGameClientView.class.getResource("images/bg_yut.png")).getImage();
	private Image background = new ImageIcon(YutGameClientView.class.getResource("images/bg_gamepanel.jpg")).getImage();
	private ImageIcon gameSmallSpot = new ImageIcon(
			YutGameClientView.class.getResource("images/ic_game_small_spot.png"));
	private ImageIcon gameBigSpot = new ImageIcon(YutGameClientView.class.getResource("images/ic_game_big_spot.png"));
	private ImageIcon character1 = new ImageIcon(YutGameClientView.class.getResource("images/character1.png"));
	private ImageIcon character2 = new ImageIcon(YutGameClientView.class.getResource("images/character2.png"));
	private ImageIcon character3 = new ImageIcon(YutGameClientView.class.getResource("images/character3.png"));
	private ImageIcon character4 = new ImageIcon(YutGameClientView.class.getResource("images/character4.png"));

	private ImageIcon gameStartImage = new ImageIcon(YutGameClientView.class.getResource("images/img_game_start.png"));
	private ImageIcon gameStartHoverImage = new ImageIcon(
			YutGameClientView.class.getResource("images/img_game_start_hover.png"));
	private ImageIcon gameReadyImage = new ImageIcon(YutGameClientView.class.getResource("images/img_game_ready.png"));
	private ImageIcon gameReadyHoverImage = new ImageIcon(
			YutGameClientView.class.getResource("images/img_game_ready_hover.png"));
	private ImageIcon gameReadyFinishImage = new ImageIcon(
			YutGameClientView.class.getResource("images/img_game_ready_finish.png"));
	private ImageIcon gameReadyFinishHoverImage = new ImageIcon(
			YutGameClientView.class.getResource("images/img_game_ready_finish_hover.png"));
	private ImageIcon userReadyImage = new ImageIcon(YutGameClientView.class.getResource("images/img_user_ready.png"));
	private ImageIcon rollImage = new ImageIcon(YutGameClientView.class.getResource("images/img_roll.png"));
	private ImageIcon rollHoverImage = new ImageIcon(YutGameClientView.class.getResource("images/img_roll_hover.png"));
	private ImageIcon yutFrontImage = new ImageIcon(YutGameClientView.class.getResource("images/img_yut_front.png"));
	private ImageIcon yutBackImage = new ImageIcon(YutGameClientView.class.getResource("images/img_yut_back.png"));
	private ImageIcon yutSpecialImage = new ImageIcon(
			YutGameClientView.class.getResource("images/img_yut_special.png"));
	private ImageIcon crownImage = new ImageIcon(YutGameClientView.class.getResource("images/img_crown.png"));

	private ImageIcon img_gameStart = new ImageIcon(
			gameStartImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_gameStartHover = new ImageIcon(
			gameStartHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_gameReady = new ImageIcon(
			gameReadyImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_gameReadyHover = new ImageIcon(
			gameReadyHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_gameReadyFinish = new ImageIcon(
			gameReadyFinishImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_gameReadyFinishHover = new ImageIcon(
			gameReadyFinishHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_userReady = new ImageIcon(
			userReadyImage.getImage().getScaledInstance(135, 30, Image.SCALE_SMOOTH));

	private ImageIcon img_crown = new ImageIcon(crownImage.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

	private ImageIcon img_roll = new ImageIcon(rollImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_rollHover = new ImageIcon(
			rollHoverImage.getImage().getScaledInstance(280, 70, Image.SCALE_SMOOTH));
	private ImageIcon img_yutFront = new ImageIcon(
			yutFrontImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));
	private ImageIcon img_yutBack = new ImageIcon(
			yutBackImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));
	private ImageIcon img_yutSpecial = new ImageIcon(
			yutSpecialImage.getImage().getScaledInstance(70, 280, Image.SCALE_SMOOTH));

	private JPanel[] userPanel = new JPanel[4];
	private JLabel[] userImageLabel = new JLabel[4];
	private JLabel[] userObjectLabel = new JLabel[4];
	private JLabel[] userObjectCntLabel = new JLabel[4];
	private JLabel[] userNameText = new JLabel[4];
	private int[] userObjectCnt = new int[] { 4, 4, 4, 4 };
	private JLabel[] yutObjectLabel = new JLabel[4];
	private JLabel[] userReadyLabel = new JLabel[4];
	private JLabel[] crownLabel = new JLabel[4];

	private int userIdx = 3;
	private int[] yutList = new int[4];
	private UserData[] userList = new UserData[4];
	private boolean Owner = true;
	private boolean[] userReadyList = new boolean[4];
	private boolean isRoll = false;

	private boolean isPlaying = false;
	private ArrayList<Integer> rollResult = new ArrayList();

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
				// 위
				g.drawImage(gameBigSpot.getImage(), 40, 30, 80, 80, this);
				g.drawImage(gameSmallSpot.getImage(), 180, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 300, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 420, 40, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 540, 40, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 660, 30, 80, 80, this);

				// 왼쪽
				g.drawImage(gameSmallSpot.getImage(), 50, 150, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 250, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 350, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 50, 450, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 40, 550, 80, 80, this);

				// 아래
				g.drawImage(gameSmallSpot.getImage(), 180, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 300, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 420, 560, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 540, 560, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 660, 550, 80, 80, this);

				// 오른쪽
				g.drawImage(gameSmallSpot.getImage(), 670, 150, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 250, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 350, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 670, 450, 60, 60, this);

				// 대각선 왼쪽 -> 오른쪽
				g.drawImage(gameSmallSpot.getImage(), 170, 140, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 260, 210, 60, 60, this);
				g.drawImage(gameBigSpot.getImage(), 350, 280, 80, 80, this);
				g.drawImage(gameSmallSpot.getImage(), 460, 370, 60, 60, this);
				g.drawImage(gameSmallSpot.getImage(), 550, 450, 60, 60, this);

				// 대각선 오른쪽 -> 왼쪽
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

		for (int i = 0; i < 4; i++) {
			userReadyLabel[i] = new JLabel();
			userReadyLabel[i].setIcon(img_userReady);
			if (i == 0) {
				userReadyLabel[i].setBounds(24, 800, 135, 30);
			} else if (i == 1) {
				userReadyLabel[i].setBounds(243, 800, 135, 30);
			} else if (i == 2) {
				userReadyLabel[i].setBounds(457, 800, 135, 30);
			} else {
				userReadyLabel[i].setBounds(669, 800, 135, 30);
			}
			userReadyLabel[i].setVisible(userReadyList[i]);
			contentPane.add(userReadyLabel[i]);
		}

		for (int i = 0; i < 4; i++) {
			crownLabel[i] = new JLabel();
			crownLabel[i].setIcon(img_crown);
			if (i == 0) {
				crownLabel[i].setBounds(115, 810, 40, 40);
			} else if (i == 1) {
				crownLabel[i].setBounds(334, 810, 40, 40);
			} else if (i == 2) {
				crownLabel[i].setBounds(548, 810, 40, 40);
			} else {
				crownLabel[i].setBounds(760, 810, 40, 40);
			}
			crownLabel[i].setVisible(true);
			contentPane.add(crownLabel[i]);
		}

		for (int i = 0; i < 4; i++) {
			userList[i] = new UserData(-1, "");
			userPanel[i] = new JPanel() {
				public void paintComponent(Graphics g) {
					g.drawImage(yutBackground, 0, 0, null);
					setOpaque(false);
					super.paintComponent(g);
				}
			};
			userImageLabel[i] = new JLabel();
			userNameText[i] = new JLabel();
			userObjectCntLabel[i] = new JLabel();
			for (int j = 0; j < 4; j++)
				userObjectLabel[i] = new JLabel();
			if (i == 0) {
				userPanel[i].setBounds(24, 680, 135, 169);
				userImageLabel[i].setBounds(28, 720, 80, 125);
				userImageLabel[i]
						.setIcon(new ImageIcon(character1.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
				userNameText[i].setBounds(30, 685, 135, 20);
				userNameText[i].setText("Username1");
				userObjectLabel[i].setBounds(115, 705, 20, 30);
				userObjectLabel[i]
						.setIcon(new ImageIcon(character1.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
				userObjectCntLabel[i].setBounds(140, 710, 20, 20);
				userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
				userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
			} else if (i == 1) {
				userPanel[i].setBounds(243, 680, 135, 169);
				userImageLabel[i].setBounds(248, 720, 80, 125);
				userImageLabel[i]
						.setIcon(new ImageIcon(character2.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
				userNameText[i].setBounds(254, 685, 135, 20);
				userNameText[i].setText("Username2");
				userObjectLabel[i].setBounds(334, 705, 20, 30);
				userObjectLabel[i]
						.setIcon(new ImageIcon(character2.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
				userObjectCntLabel[i].setBounds(359, 710, 20, 20);
				userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
				userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
			} else if (i == 2) {
				userPanel[i].setBounds(458, 680, 135, 169);
				userImageLabel[i].setBounds(462, 720, 80, 125);
				userImageLabel[i]
						.setIcon(new ImageIcon(character3.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
				userNameText[i].setBounds(464, 685, 135, 20);
				userNameText[i].setText("Username3");
				userObjectLabel[i].setBounds(549, 705, 20, 30);
				userObjectLabel[i]
						.setIcon(new ImageIcon(character3.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
				userObjectCntLabel[i].setBounds(574, 710, 20, 20);
				userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
				userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
			} else {
				userPanel[i].setBounds(669, 680, 135, 169);
				userImageLabel[i].setBounds(674, 720, 80, 125);
				userImageLabel[i]
						.setIcon(new ImageIcon(character4.getImage().getScaledInstance(80, 125, Image.SCALE_SMOOTH)));
				userNameText[i].setBounds(676, 685, 135, 20);
				userNameText[i].setText("Username4");
				userObjectLabel[i].setBounds(760, 705, 20, 30);
				userObjectLabel[i]
						.setIcon(new ImageIcon(character4.getImage().getScaledInstance(20, 30, Image.SCALE_SMOOTH)));
				userObjectCntLabel[i].setBounds(785, 710, 20, 20);
				userObjectCntLabel[i].setText(Integer.toString(userObjectCnt[i]));
				userObjectCntLabel[i].setFont(new Font("bold", Font.PLAIN, 30));
			}
			userNameText[i].setVisible(false);
			userImageLabel[i].setVisible(false);
			userObjectLabel[i].setVisible(false);
			userObjectCntLabel[i].setVisible(false);
			contentPane.add(userObjectCntLabel[i]);
			contentPane.add(userObjectLabel[i]);
			contentPane.add(userImageLabel[i]);
			contentPane.add(userNameText[i]);
			contentPane.add(userPanel[i]);
		}

		for (int i = 0; i < 4; i++) {
			yutObjectLabel[i] = new JLabel();
			yutObjectLabel[i].setBounds(840 + 100 * i, 60, 70, 280);
			yutObjectLabel[i].setIcon(img_yutBack);
			contentPane.add(yutObjectLabel[i]);
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(828, 538, 424, 181);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));

		txtInput = new JTextField();
		txtInput.setBounds(890, 729, 281, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(1183, 728, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(828, 778, 62, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(828, 727, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("종 료");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendChatMsg(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(1183, 778, 69, 40);
		contentPane.add(btnNewButton);

		JPanel yutPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(yutBackground, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		yutPanel.setBounds(829, 22, 423, 364);
		contentPane.add(yutPanel);

		if (Owner) {
			btnGameStart = new JButton(img_gameStart);
		} else if (!userReadyList[userIdx]) {
			btnGameStart = new JButton(img_gameReady);
		} else {
			btnGameStart = new JButton(img_gameReadyFinish);
		}

		btnGameStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // 마우스가 올라갔을 때
				if (Owner) {
					btnGameStart.setIcon(img_gameStartHover);
				} else if (!userReadyList[userIdx]) {
					btnGameStart.setIcon(img_gameReadyHover);
				} else {
					btnGameStart.setIcon(img_gameReadyFinishHover);
				}
				btnGameStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) { // 마우스가 눌렸을 때
				if (Owner) {
					btnGameStart.setIcon(img_gameStartHover);
				} else if (!userReadyList[userIdx]) {
					btnGameStart.setIcon(img_gameReadyHover);
				} else {
					btnGameStart.setIcon(img_gameReadyFinishHover);
				}
				btnGameStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
				System.out.println(userIdx+" Owner: " +Owner);
				if (Owner) {
					// gamestart
					ChatMsg obcm = new ChatMsg(UserName, "104", userIdx + " IGameStart"); // 로그인
					SendChatMsg(obcm);
				} else {
					ChatMsg obcm = new ChatMsg(UserName, "103", userIdx + " readyChange"); // 로그인
					SendChatMsg(obcm);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) { // 마우스가 나갔을 때 원상복귀
				if (Owner) {
					btnGameStart.setIcon(img_gameStart);
				} else if (!userReadyList[userIdx]) {
					btnGameStart.setIcon(img_gameReady);
				} else {
					btnGameStart.setIcon(img_gameReadyFinish);
				}
				btnGameStart.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		btnGameStart.setBounds(890, 400, 280, 70);
		btnGameStart.setBorderPainted(false); // 버튼 테두리 설정
		btnGameStart.setContentAreaFilled(false); // 버튼 영역 배경 표시 설정
		btnGameStart.setFocusPainted(false); // 포커스 표시 설정
		btnGameStart.setVisible(true);
		contentPane.add(btnGameStart);

		Random random = new Random();

		btnRollYut = new JButton(img_roll);
		btnRollYut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // 마우스가 올라갔을 때
				btnRollYut.setIcon(img_rollHover);
				btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) { // 마우스가 눌렸을 때
				btnRollYut.setIcon(img_rollHover);
				btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) { // 마우스로 클릭했을 때
				rollResult.clear();

				random.setSeed(System.currentTimeMillis());
				int special = random.nextInt(100);
				int specialPos = random.nextInt(4);

				// yutList -> 1 = 앞면 / 0 = 뒷면
				for (int i = 0; i < 4; i++) {
					int num = random.nextInt(2);
					if (num == 0)
						yutList[i] = 1;
					else
						yutList[i] = 0;
				}

				for (int i = 0; i < 4; i++) {
					if (yutList[i] == 1)
						yutObjectLabel[i].setIcon(img_yutFront);
					else
						yutObjectLabel[i].setIcon(img_yutBack);
				}

				boolean isHasBack = false;
				// false는 뒤집힌거기에 빽도적용
				if (special < 25 && yutList[specialPos] == 0) {
					yutObjectLabel[specialPos].setIcon(img_yutSpecial);
					yutList[specialPos] = -1;
					isHasBack = true;
				}

				int yutCnt = 0;
				boolean hasBackShow = false;

				for (int i = 0; i < 4; i++) {
					if (yutList[i] != 1) {
						yutCnt += 1;
						if (yutList[i] == -1)
							hasBackShow = true;
					}
				}
				switch (yutCnt) {
				case 1:
					if (hasBackShow)
						rollResult.add(-1);
					else
						rollResult.add(1);
					break;
				case 2:
					rollResult.add(2);
					break;
				case 3:
					rollResult.add(3);
					break;
				case 4:
					rollResult.add(4);
					break;
				case 0:
					rollResult.add(5);
					break;
				}

				for (int i = 0; i < rollResult.size(); i++) {
					System.out.print(rollResult.get(i) + " ");
				}
				System.out.println();

				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) { // 마우스가 나갔을 때 원상복귀
				btnRollYut.setIcon(img_roll);
				btnRollYut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		btnRollYut.setBounds(890, 400, 280, 70);
		btnRollYut.setBorderPainted(false); // 버튼 테두리 설정
		btnRollYut.setContentAreaFilled(false); // 버튼 영역 배경 표시 설정
		btnRollYut.setFocusPainted(false); // 포커스 표시 설정
		btnRollYut.setVisible(false);
		contentPane.add(btnRollYut);

		JCheckBox autoPlayCheck = new JCheckBox("Auto Play");
		autoPlayCheck.setBounds(828, 487, 115, 23);
		contentPane.add(autoPlayCheck);

		JButton btnRollBack = new JButton("RollBack");
		btnRollBack.setBounds(1115, 487, 97, 23);
		contentPane.add(btnRollBack);

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello"); // 로그인
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
		// Android와 호환성을 위해 각각의 Field를 따로따로 읽는다.

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

			// textArea.append("메세지 송신 에러!!\n");
			// System.exit(0);
		}
		return cm;
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				ChatMsg cm = ReadChatMsg();
				if (cm == null) {
					System.out.println("cm이 null");
					break;
				}
				if (socket == null) {
					System.out.println("socket이 null");
					break;
				}
				switch (cm.code) {
				case "100":
					System.out.println(cm.code + " " + cm.UserName + " " + cm.data);
					break;
				case "101":
					userIdx = Integer.parseInt(cm.data);
					repaint();
					break;
				case "102":
					for (int i = 0; i < 4; i++) {
						userNameText[i].setVisible(false);
						userImageLabel[i].setVisible(false);
						userObjectLabel[i].setVisible(false);
						userObjectCntLabel[i].setVisible(false);
						userReadyLabel[i].setVisible(false);
						crownLabel[i].setVisible(false);
					}
					Owner = false;

					String[] userInfoData = cm.data.split(" ");
					for (int i = 0; i < userInfoData.length; i += 4) {
						int idx = Integer.parseInt(userInfoData[i]);
						String name = userInfoData[i + 1];
						boolean isOwner = Boolean.parseBoolean(userInfoData[i + 2]);
						boolean isReady = Boolean.parseBoolean(userInfoData[i + 3]);
						userList[idx].setIdx(idx);
						userList[idx].setUsername(name);
						userNameText[idx].setText(name);
						userNameText[idx].setVisible(true);
						userImageLabel[idx].setVisible(true);
						userObjectLabel[idx].setVisible(true);
						userObjectCntLabel[idx].setVisible(true);
						userReadyLabel[idx].setVisible(isReady);
						crownLabel[idx].setVisible(isOwner);
						if (userIdx == idx && isOwner)
							Owner = true;
					}

					repaint();
					break;
				case "105":
					String[] serverGameStartResponse = cm.data.split(" ");
					boolean serverGameStartAccept = Boolean.parseBoolean(serverGameStartResponse[0]);
					if(serverGameStartAccept) {
						AppendText("☆☆☆☆☆ 게임을 시작합니다 ☆☆☆☆☆");
						btnGameStart.setVisible(false);
						btnRollYut.setVisible(true);
						for(int i=0; i<4; i++) 
							userReadyLabel[i].setVisible(false);
						
					}else {
						if(serverGameStartResponse[1].equals("NoUser"))
							AppendText("아직 유저가 아무도 없습니다.");
						else
							AppendText("모든 유저들이 아직 준비를 하지 않았습니다");
					}
					break;
				case "200": // chat message
					String msg;
					msg = String.format("[%s] %s", cm.UserName, cm.data);
					System.out.println("msg: " + msg);
					AppendText(msg);
					break;
				case "300": // Image 첨부
					AppendText("[" + cm.UserName + "]" + " " + cm.data);
					// AppendImage(cm.img);
					AppendImageBytes(cm.imgbytes);
					break;
				case "400":
					System.out.println(cm.code + " " + cm.UserName + " " + cm.data);
					break;
				}
			}
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
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
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
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
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
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

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
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

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		ChatMsg obcm = new ChatMsg(UserName, "200", msg);
		SendChatMsg(obcm);
	}

	// 하나의 Message 보내는 함수
	// Android와 호환성을 위해 code, UserName, data 모드 각각 전송한다.
	public void SendChatMsg(ChatMsg obj) {
		try {
			oos.writeObject(obj.code);
			oos.writeObject(obj.UserName);
			oos.writeObject(obj.data);
			if (obj.code.equals("300")) { // 이미지 첨부 있는 경우
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

			// textArea.append("메세지 송신 에러!!\n");
			// System.exit(0);
		}
	}

}
