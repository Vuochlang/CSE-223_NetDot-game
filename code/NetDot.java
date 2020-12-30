//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Name: Vuochlang Chang                                                                                                            //
// Class: CSE223        Spring 2020                                                                                                 //
// Date: 06/4/2020                                                                                                                  //
// Assignment: PA5 - NetDot game                                                                          						              //
//     This is a game that allow 2 players take turn to play the game over the connected port 1234. It will draw a line where the   //
//		player clicked on. It will remember whose turn it is and always calculate players's score and display who will win the game.  //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.beans.PropertyChangeEvent;
import javax.swing.border.LineBorder;
import java.awt.Component;
import javax.swing.SwingConstants;

public class NetDot extends JFrame {

  JPanel contentPane;
  JTextField player1,player2;
  JTextArea	score, txArea1, Area1;
  JLabel lblNewLabel1,lblNewLabel2,lblScore,ConnectLabel;
  JButton startButton;
  JRadioButton ServerButton, ClientButton;
  JTextArea NoticeArea;
  int count=0;	int myStatus,start=0;
  GamePanel panel;
  boolean isServerClicked=false; boolean isClientClicked=false; boolean firstNT=true;	boolean dataFrom;	boolean callClose=false;

  ServerSocket ss;
  Socket socket;
  PrintWriter pw;
  Scanner sc;
  NetThread nt=new NetThread();

  String name1, name2;
  int x,y;
  JTextField txtLocalhost;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          NetDot frame = new NetDot();
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
  public NetDot() {

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 659, 572);
    contentPane = new JPanel();
    contentPane.setBackground(Color.WHITE);
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    panel = new GamePanel();
    panel.setBorder(new LineBorder(new Color(0, 0, 0)));
    panel.saveParent(this);
    nt.saveParent(this);

    panel.setBackground(Color.WHITE);
    panel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {//when player clicks inside the JPanel (NetDot area)
        x=arg0.getX();
        y=arg0.getY();
        handleClick(myStatus, x, y);
      }

    });
    panel.setBounds(50, 30, 412, 412);
    contentPane.add(panel);
    panel.setLayout(null);

    player1 = new JTextField();
    player1.setText("Server");
    player1.setBounds(477, 104, 129, 26);
    contentPane.add(player1);
    player1.setColumns(10);

    player2 = new JTextField();
    player2.setText("Client");
    player2.setSelectedTextColor(Color.WHITE);
    player2.setDisabledTextColor(Color.GRAY);
    player2.setBounds(477, 268, 129, 26);
    contentPane.add(player2);
    player2.setColumns(10);

    startButton = new JButton("START");
    startButton.addMouseListener(new MouseAdapter() {//when the Server or the Client click on the button, it will start to create server/client portal, start netThread and start the game
      @Override
      public void mouseClicked(MouseEvent e) {
        ++start;
        if(myStatus!=1&&myStatus!=2)	return;
        if(start==1)
        {	if(myStatus==1)
        {
          name1=player1.getText();
          createServer();
          nt.saveInfo(pw, socket,myStatus,name1);
          nt.start();
          nt.sendInfo(name1);
        }
          if(myStatus==2)
          {
            name2=player2.getText();
            createClient();
            nt.saveInfo(pw, socket,myStatus,name2);
            nt.start();
            nt.sendInfo(name2);
          }
          if(sc.hasNextLine()&&firstNT)	//exchange name between server and client
          {	parseString(sc.nextLine());
            firstNT=false;
          }
          panel.startGame();
          panel.repaint();
        }
        if(start==2)
        {	if(myStatus==1)
        {	closeServer();
          panel.setEnabled(false);
          panel.gameOver=true;
        }
          if(myStatus==2)
          {	closeClient();
            panel.setEnabled(false);
            panel.gameOver=true;
          }
        }
      }
    });

    startButton.setBounds(477, 159, 115, 29);
    contentPane.add(startButton);

    lblNewLabel1 = new JLabel("1st Player's name");
    lblNewLabel1.setForeground(Color.BLACK);
    lblNewLabel1.setBounds(477, 75, 128, 26);
    contentPane.add(lblNewLabel1);

    lblNewLabel2 = new JLabel("2nd Player's name");
    lblNewLabel2.setBounds(477, 240, 139, 26);
    contentPane.add(lblNewLabel2);

    score = new JTextArea();
    score.setEditable(false);
    score.setFont(new Font("Tahoma", Font.BOLD, 12));
    score.setWrapStyleWord(true);
    score.setBounds(98, 6, 341, 26);
    contentPane.add(score);

    lblScore = new JLabel("SCORE :");
    lblScore.setBounds(15, 0, 68, 26);
    contentPane.add(lblScore);

    txArea1 = new JTextArea();
    txArea1.setEditable(false);
    txArea1.setFont(new Font("Tahoma", Font.BOLD, 16));
    txArea1.setBounds(50, 483, 393, 33);

    contentPane.add(txArea1);

    ServerButton = new JRadioButton("Server");
    ServerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent arg0) {//if player click the sever radioButton, the client button will disabled
        if(isClientClicked)
        {	isClientClicked=false;
          ClientButton.setSelected(false);
        }
        isServerClicked=true;
        myStatus=1;
        startButton.setText("START");
      }
    });
    ServerButton.setOpaque(false);
    ServerButton.setBounds(473, 45, 155, 29);
    contentPane.add(ServerButton);

    ClientButton = new JRadioButton("Client");
    ClientButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {//if player click the client radioButton, the server button will disabled
        if(isServerClicked)
        {	isServerClicked=false;
          ServerButton.setSelected(false);
        }
        isClientClicked=true;
        myStatus=2;
        startButton.setText("CONNECT");
      }
    });
    ClientButton.setOpaque(false);
    ClientButton.setBounds(473, 210, 155, 29);
    contentPane.add(ClientButton);

    NoticeArea = new JTextArea();
    NoticeArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
    NoticeArea.setBounds(110, 450, 197, 31);
    contentPane.add(NoticeArea);

    Area1 = new JTextArea();
    Area1.setFont(new Font("Tahoma", Font.PLAIN, 13));
    Area1.setBounds(322, 450, 139, 31);
    contentPane.add(Area1);

    JLabel ConnectLabel = new JLabel("Connect to:");
    ConnectLabel.setBounds(477, 309, 129, 20);
    contentPane.add(ConnectLabel);

    txtLocalhost = new JTextField();
    txtLocalhost.setText("localhost");
    txtLocalhost.setBounds(477, 330, 129, 26);
    contentPane.add(txtLocalhost);
    txtLocalhost.setColumns(10);

    JLabel lblNewLabel_1 = new JLabel("Notice:");
    lblNewLabel_1.setBounds(60, 447, 69, 20);
    contentPane.add(lblNewLabel_1);
    contentPane.setVisible(true);
  }

  public void createServer()//create the ServerPortal with port 1234
  {	try {	ss=new ServerSocket(1234);
  }catch(Exception e) {
    System.out.println("cannot create serverSocket!");
    return;
  }

    try {	socket=ss.accept();
    }catch(Exception e) {
      System.out.println("cannot accept connection in socket!");
      return;
    }

    try {	sc=new Scanner(socket.getInputStream());
      pw=new PrintWriter(socket.getOutputStream());
    }catch(Exception e) {
      System.out.println("cannot create scanner and printwriter!");
      return;
    }

    Area1.setText("You are the Player 1");
    NoticeArea.setText("Got a connection with the socket!");
    startButton.setText("QUIT");

  }

  public void createClient()//create ClientPortal with port 1234
  {	try {	socket=new Socket(txtLocalhost.getText(),1234);
  }catch(Exception e) {
    System.out.println("cannot connect to server!");
    return;
  }

    try {	sc=new Scanner(socket.getInputStream());
      pw=new PrintWriter(socket.getOutputStream());
    }catch(Exception e) {
      System.out.println("cannot create scanner and printwriter!");
      return;
    }

    Area1.setText("You are the Player 2");
    NoticeArea.setText("Connected to the server!");
    startButton.setText("QUIT");
  }

  public void closeServer()//close the Server port and send the message to the client
  {	panel.setEnabled(false);
    if(!callClose)
    {	nt.sendInfo("Q");
      Area1.setText("You have exited!");
    }
    pw.close();
    sc.close();
    System.out.println("closing the server...");
    try {

      socket.close();
      NoticeArea.setText("Server closed!");
    } catch (Exception e) {
      System.out.println("cannot close the socket for sever");
      return;
    }
    System.out.println("Server closed!");
  }

  public void closeClient()//close the client port and send the message to the server
  {	panel.setEnabled(false);
    if(!callClose)
    {	nt.sendInfo("Q");
      Area1.setText("You have exited!");
    }
    pw.close();
    sc.close();
    System.out.println("closing the client...");
    try {
      socket.close();
      NoticeArea.setText("Client closed!");
    } catch (Exception e) {
      System.out.println("cannot close the socket for client");
      return;
    }
    System.out.println("Client closed!");
  }

  public void parseString(String a)//this will parse the string to exchange name between Server and Client
  {	String[] newWord=a.split(" ");
    if(newWord.length<2)
    {	if(myStatus==1)
    {	player2.setText(newWord[0]);
      panel.player2=name2=newWord[0];
      player2.setEnabled(false);
      txtLocalhost.setEnabled(false);

    }
      if(myStatus==2)
      {	player1.setText(newWord[0]);
        panel.player1=name1=newWord[0];
        player1.setEnabled(false);
        txtLocalhost.setEnabled(false);
      }
    }
  }

  public void handleClick(int a, int b, int c)//this will call click(x,y) in the JPanel as called accordingly
  {	int x1=b;
    int y1=c;

    if(x1!=x)//if it called from NetThread
    {	panel.click(x1, y1);
      panel.repaint();
      return;
    }
    if(x1==x)//if it called from mouseclikc
    {
      if(myStatus==1&&!name1.equals(panel.whoTurn()))	return;//this will ignore if the player is trying to click but it's not their turn
      if(myStatus==2&&!name2.equals(panel.whoTurn()))	return;

      panel.click(x1,y1);//this will call the click function in gamePanel as usual and update everything
      panel.repaint();
      nt.sendInfo("C "+x1+" "+y1);//this will send the coordinate from one player to other
    }
  }
}
