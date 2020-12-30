
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class NetThread extends Thread{
  PrintWriter pw;
  Scanner sc;
  Socket socket;
  ServerSocket ss;
  NetDot myParent;
  boolean isQuit=false;
  int x,y,myStatus;
  String name1,name2;

  public void saveParent(NetDot parent)//save NetDot.java as the parent of the NetThread.java
  {	myParent=parent;
  }

  public void saveInfo(PrintWriter pw2, Socket socket2,int c, String x) //save information for NetThread
  {
    pw=pw2;
    socket=socket2;
    myStatus=c;
    if(c==1)	name1=x;
    if(c==2)	name2=x;
  }

  public void run()//loop to receive information from the other side of the port
  {	try {
    sc=new Scanner(socket.getInputStream());
  } catch (Exception e) {
    System.out.println("error in sc !");
    return;
  }
    while(sc.hasNextLine())
    {	String temp=sc.nextLine();
      parseString(temp);
      if(myStatus==1)	System.out.println("received from Client=<"+temp+">");
      if(myStatus==2)	System.out.println("received from Server=<"+temp+">");
      System.out.println("I'm in the scanner loop!");
    }
  }

  public void sendInfo(String s)//send information to the other side of the port
  {	pw.println(s);
    pw.flush();
  }

  public void parseString(String a)//parse string to figure out whether to continue or quit the game
  {	String[] newWord=a.split(" ");
    String Quit="Q";
    String Continue="C";
    if(newWord.length>1)
    {	x=Integer.parseInt(newWord[1]);
      y=Integer.parseInt(newWord[2]);
    }

    //the following just a checking error message
    System.out.println("I'm in the parsetring with string passed =<"+a+">");
    System.out.println("x="+x+" y="+y);
    for(int i=0; i<newWord.length; i++)
    {	System.out.println("newWord["+i+"]=<"+newWord[i]+">");
    }

    if(newWord[0].equals(Quit))//this will call to close the port for both server and client
    {	System.out.println("In quit operation...");
      if(myStatus==1)
      {	myParent.Area1.setText("Client has exited!");
        myParent.callClose=true;
        myParent.closeServer();
        return;
      }
      if(myStatus==2)
      {	myParent.Area1.setText("Server has exited!");
        myParent.callClose=true;
        myParent.closeClient();
        return;
      }
    }

    if(newWord[0].equals(Continue))//this indicates that the game is still playing
    {	System.out.println("sending x y in NetThread...myStatus="+myStatus);
      if(myStatus==1)
      {
        myParent.handleClick(1,x,y);
        System.out.println("already called callClick");
      }
      if(myStatus==2)
      {
        myParent.handleClick(2,x,y);
        System.out.println("already called callClick");
      }
    }
  }
}