import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class GamePanel extends JPanel {
  NetDot parent;
  int xPosition, yPosition;
  int boxWidth=50,boxHeight=50;
  Box[][] box=new Box[8][8];//[row][column]
  int NetDotize=5;
  int selectedColumn, selectedRow;
  boolean left,right,top,bottom;//flag to remember where to draw a line when move from one function to another
  Graphics g;
  boolean start=false, flag=true;
  String player1, player2, winPlayer;//name of player
  String name;
  int turnCount;
  boolean flagTurn=false, darkMode=false, first=true, gameOver=false, error=false,sameName=false;
  int count1,count2;	//count players' score
  Color color1;	//color to fill in the box for player1
  Color color2;	//color to fill in the box for player2

  public void saveParent(NetDot mainParent)
  {	parent=mainParent;
  }

  public void startGame()
  {	start=false;
    if(!start)//this will determine whether the players have entered the same name or not (lowerCase and upperCase are considered the same)
    {	if(parent.myStatus==1)
    {	player1=parent.player1.getText();
      System.out.println("Game started! in Server ");
    }
      if(parent.myStatus==2)
      {	player2=parent.player2.getText();
        System.out.println("Game started! in Client ");
      }
    }
    sameName=false;//start the game by setting all the needed flags
    start=true;
    turnCount=1;
    flag=true;
    flagTurn=false;
    first=true;
    error=false;
    //	System.out.println("Game started! ");
  }

  public void paint(Graphics graphic)
  {	left=right=top=bottom=false;
    int startX,startY;
    int xPlace,yPlace;
    g=graphic;
    count1=count2=0;
    color1=new Color(204,229,255);
    color2=new Color(229,204,255);


    super.paint(g);
    if(parent==null)	return;

    if(flag)//this will initialize the 4 coordinate points of each box and save them
    {	for(int i=0; i<8; i++)//set up coordinate points
    {	for(int j=0; j<8; j++)
    {	if(i==0&&j==0)	startX=startY=5;
    else if(i>0&&j>0)
    {	startX=j*50+5;
      startY=i*50+5;
    }
    else if(i==0&&j>0)
    {	startX=j*50+5;
      startY=5;
    }
    else
    {	startX=5;
      startY=i*50+5;
    }
      box[i][j]=new Box();
      box[i][j].setPoint(startX,startY,startX+50,startY+50);
    }
    }
      flag=false;
    }

    //the following is a for-loop to go through each box and will decide whether to draw a line, fill color, set name..
    for(int i=0;i<8;i++)
    {	for(int j=0;j<8;j++)
    {	if(box[i][j].isComplete())
    {	//set color to the box according to the box.initial
      if(box[i][j].name.equals(player1))
      {	g.setColor(color1);
        g.fillRect(box[i][j].topLeftX,box[i][j].topLeftY,50,50);
      }
      if(box[i][j].name.equals(player2))
      {	g.setColor(color2);
        g.fillRect(box[i][j].topLeftX,box[i][j].topLeftY,50,50);
      }
    }

      //decide which color and where to draw ovals that represent the location of the box
      if(darkMode)	g.setColor(new Color(128,128,128));
      if(!darkMode)	g.setColor(Color.black);
      g.fillOval(box[i][j].topLeftX-2, box[i][j].topLeftY-2, NetDotize, NetDotize);
      if(i==7&&j==7)	g.fillOval(box[i][j].bottomRightX-2, box[i][j].bottomRightY-2, NetDotize, NetDotize);
      if(j==7)	g.fillOval(box[i][j].topRightX-2, box[i][j].topRightY-2, NetDotize, NetDotize);
      if(i==7)	g.fillOval(box[i][j].bottomLeftX-2, box[i][j].bottomLeftY-2, NetDotize, NetDotize);

      //decide where to draw a line according to the true/false flag from the box (4 sides)
      if(box[i][j].left==true)	g.drawLine(box[i][j].topLeftX,box[i][j].topLeftY,box[i][j].bottomLeftX,box[i][j].bottomLeftY);
      if(j==7)	if(box[i][j].right==true)	g.drawLine(box[i][j].topRightX,box[i][j].topRightY,box[i][j].bottomRightX,box[i][j].bottomRightY);
      if(box[i][j].top==true)	g.drawLine(box[i][j].topLeftX,box[i][j].topLeftY,box[i][j].topRightX,box[i][j].topRightY);
      if(i==7)	if(box[i][j].bottom==true)	g.drawLine(box[i][j].bottomLeftX,box[i][j].bottomLeftY,box[i][j].bottomRightX,box[i][j].bottomRightY);

      //count players' scores according to the initial in the box
      if(start&&player1!=null&&player2!=null)
      {	if(box[i][j].name!=null)
      {
        if(box[i][j].name.equals(player1))	++count1;
        if(box[i][j].name.equals(player2))	++count2;
      }
      }

      //write the initial of the player in a completed box
      if(box[i][j].initial!=' ')
      {	xPlace=((box[i][j].topLeftX)+22);
        yPlace=((box[i][j].topLeftY)+30);
        g.setColor(Color.gray);
        g.setFont(new Font("Tahoma", Font.BOLD, 16));
        g.drawString(Character.toString(box[i][j].initial),xPlace,yPlace);
      }
    }
    }

    parent.score.setText(sendPoint());//display the score

    //this tells user to enter their name at the beginning of the game
    if(!start&&sameName==false)
    {	if(!darkMode)	parent.txArea1.setForeground(Color.black);
      if(darkMode)	parent.txArea1.setForeground(Color.white);
      parent.txArea1.setText("Enter player's name...");
    }

    //this will display whose turn to play the game
    if(start&&sameName==false)
    {	if(!darkMode)	parent.txArea1.setForeground(Color.black);
      if(darkMode)	parent.txArea1.setForeground(Color.white);
      if(error)	parent.txArea1.setForeground(Color.red);
      if(first)
      {	if(parent.myStatus==2)	parent.txArea1.setText("This is server's turn");
        if(parent.myStatus==1)	parent.txArea1.setText("This is "+player1+"'s turn");
        first=false;
      }
      if(!first&&!error)	parent.txArea1.setText("This is "+whoTurn()+"'s turn");
    }

    //this is a loop to determine if all boxes is completed and will end the game and tell player who has won the game
    for(int i=0; i<8;i++)
    {	for(int j=0; j<8; j++)
    {	if(!box[i][j].isComplete())
    {	gameOver=false;
      break;
    }
      gameOver=true;
    }
      if(!gameOver)	break;
    }
    if(gameOver)
    {	if(count1>count2)
    {	winPlayer=player1;
      parent.txArea1.setText("Game Over! "+winPlayer+" won!");
    }
    else if	(count1<count2)
    {	winPlayer=player2;
      parent.txArea1.setText("Game Over! "+winPlayer+" won!");
    }
    else//count1=count2
    {	parent.txArea1.setText("Game Over! It's a tie!");
    }
    }

  }

  public void click(int x, int y)	//this will call when the player clicks in the JPanel in the NetDot Area
  {	xPosition=x;
    yPosition=y;
    if(start==false)	return;
    selectedBox();
    //System.out.println("x="+xPosition+", y="+yPosition);
    placeToDrawLine();
  }

  public void selectedBox()//this will find out which box the player has clicked on according to the position of the mouse click
  {	selectedRow=((int) Math.floor(yPosition/boxWidth));
    if(selectedRow>7) selectedRow=7;
    selectedColumn=((int)Math.floor(xPosition/boxHeight));
    if(selectedColumn>7) selectedColumn=7;
    //	System.out.println("Row= "+selectedRow+" Column= "+selectedColumn);
    System.out.println("You have selected the box["+(selectedRow)+"]["+(selectedColumn)+"]");
  }

  public void placeToDrawLine()//this will find the shortest distance/the nearest side of the selected box
  {	int dLeft,dRight,dTop,dBottom;//distance from clicked point to each side
    int temp,shortestDistance=0;

    dLeft=Math.abs(xPosition-(box[selectedRow][selectedColumn].topLeftX));
    dRight=Math.abs((box[selectedRow][selectedColumn].topRightX)-xPosition);
    dTop=Math.abs(yPosition-(box[selectedRow][selectedColumn].topRightY));
    dBottom=Math.abs((box[selectedRow][selectedColumn].bottomRightY)-yPosition);

    temp=Math.min(dLeft, dRight);
    temp=Math.min(temp, dTop);
    shortestDistance=Math.min(temp, dBottom);

    if(shortestDistance==dLeft)	left=true;
    else if(shortestDistance==dRight)	right=true;
    else if(shortestDistance==dTop)	top=true;
    else if(shortestDistance==dBottom)	bottom=true;
    else {temp=1;}
    drawLine();
  }

  public void drawLine()//this will which side of the box to true and also the corresponding box's side to true
  {						//also determine whether the side of the box has been taken and return
    //if the player chose the left side
    if(left==true)
    {	if(box[selectedRow][selectedColumn].left==true)
    {	parent.txArea1.setText("You have selected a taken side. Try again!");
      System.out.println("Box["+selectedRow+"]["+selectedColumn+"] is taken!");
      System.out.println("---------------------------------------");
      error=true;
      return;
    }
      error=false;
      box[selectedRow][selectedColumn].left=true;
      if(selectedColumn>0)	box[selectedRow][selectedColumn-1].right=true;
      System.out.println("Drawing a line in box["+selectedRow+"]["+selectedColumn+"] on the left");
      System.out.println("---------------------------------------");
      checkIfCompleted('l');
    }
    //if the player chose the right side
    if(right==true)
    {	if(box[selectedRow][selectedColumn].right==true)
    {	parent.txArea1.setText("You have selected a taken side. Try again!");
      System.out.println("Box["+selectedRow+"]["+selectedColumn+"] is taken!");
      System.out.println("---------------------------------------");
      error=true;
      return;
    }
      error=false;
      box[selectedRow][selectedColumn].right=true;
      if(selectedColumn<7)	box[selectedRow][selectedColumn+1].left=true;
      System.out.println("Drawing a line in box["+selectedRow+"]["+selectedColumn+"] on the right");
      System.out.println("---------------------------------------");
      checkIfCompleted('r');
    }
    //if the player chose the top side
    if(top==true)
    {	if(box[selectedRow][selectedColumn].top==true)
    {	parent.txArea1.setText("You have selected a taken side. Try again!");
      System.out.println("Box["+selectedRow+"]["+selectedColumn+"] is taken!");
      System.out.println("---------------------------------------");
      error=true;
      return;
    }
      error=false;
      box[selectedRow][selectedColumn].top=true;
      if(selectedRow>0)	box[selectedRow-1][selectedColumn].bottom=true;
      System.out.println("Drawing a line in box["+selectedRow+"]["+selectedColumn+"] on the top");
      System.out.println("---------------------------------------");
      checkIfCompleted('t');
    }
    //if the player chose the bottom side
    if(bottom==true)
    {	if(box[selectedRow][selectedColumn].bottom==true && selectedRow!=7)
    {	parent.txArea1.setText("You have selected a taken side. Try again!");
      System.out.println("Box["+selectedRow+"]["+selectedColumn+"] is taken!");
      System.out.println("---------------------------------------");
      error=true;
      return;
    }
      error=false;
      box[selectedRow][selectedColumn].bottom=true;
      if(selectedRow<7)	box[selectedRow+1][selectedColumn].top=true;
      System.out.println("Drawing a line in box["+selectedRow+"]["+selectedColumn+"] on the buttom");
      System.out.println("---------------------------------------");
      checkIfCompleted('b');
    }
  }

  public void checkIfCompleted(char side)//this is to check if a box is completed  and set the initial of the box according to the player's name
  {	if(side=='l')//left side
  {	if(box[selectedRow][selectedColumn].isComplete())
  {
    box[selectedRow][selectedColumn].setName(whoTurn());
    flagTurn=true;
    if(selectedColumn>0)
    {	if(box[selectedRow][selectedColumn-1].isComplete())
    {	box[selectedRow][selectedColumn-1].setName(whoTurn());
      flagTurn=true;
    }
    }
  }
    if(box[selectedRow][selectedColumn].isComplete()==false && selectedColumn>0)
    {	if(box[selectedRow][selectedColumn-1].isComplete())
    {	box[selectedRow][selectedColumn-1].setName(whoTurn());
      flagTurn=true;
    }
    }
    switchTurn();
  }
    if(side=='r')//right side
    {	if(box[selectedRow][selectedColumn].isComplete())
    {
      box[selectedRow][selectedColumn].setName(whoTurn());
      flagTurn=true;
      if(selectedColumn<7)
      {	if(box[selectedRow][selectedColumn+1].isComplete())
      {	box[selectedRow][selectedColumn+1].setName(whoTurn());
        flagTurn=true;
      }
      }
    }
      if(box[selectedRow][selectedColumn].isComplete()==false && selectedColumn<7)
      {	if(box[selectedRow][selectedColumn+1].isComplete())
      {	box[selectedRow][selectedColumn+1].setName(whoTurn());
        flagTurn=true;
      }
      }
      switchTurn();
    }
    if(side=='t')//top side
    {	if(box[selectedRow][selectedColumn].isComplete())
    {	box[selectedRow][selectedColumn].setName(whoTurn());
      flagTurn=true;
      if(selectedRow>0)
      {	if(box[selectedRow-1][selectedColumn].isComplete())
      {	box[selectedRow-1][selectedColumn].setName(whoTurn());
        flagTurn=true;
      }
      }
    }
      if(box[selectedRow][selectedColumn].isComplete()==false && selectedRow>0)
      {	if(box[selectedRow-1][selectedColumn].isComplete())
      {	box[selectedRow-1][selectedColumn].setName(whoTurn());
        flagTurn=true;
      }
      }
      switchTurn();
    }
    if(side=='b')//bottom side
    {	if(box[selectedRow][selectedColumn].isComplete())
    {	box[selectedRow][selectedColumn].setName(whoTurn());
      flagTurn=true;
      if(selectedRow<7)
      {	if(box[selectedRow+1][selectedColumn].isComplete())
      {	box[selectedRow+1][selectedColumn].setName(whoTurn());
        flagTurn=true;
      }
      }
    }
      if(selectedRow<7 && box[selectedRow][selectedColumn].isComplete()==false)
      {	if(box[selectedRow+1][selectedColumn].isComplete())
      {	box[selectedRow+1][selectedColumn].setName(whoTurn());
        flagTurn=true;
      }
      }
      switchTurn();
    }
  }

  public void switchTurn()//this will determine whether to switch turn between players or let the winning player continue for another round
  {	if(flagTurn)
  {	flagTurn=false;
    return;
  }
    ++turnCount;
    flagTurn=false;
  }

  public String whoTurn() //this is keep track of whose turn it is
  {	int a=turnCount%2;
    if(a==0)
    {	//System.out.println("It was player2's turn");
      return player2;
    }
    if(a==1)
    {	//System.out.println("It was player1's turn");
      return player1;
    }
    return null;
  }

  public String sendPoint()//this will return as a string of player's name and their score
  {	if(!start)	return("SERVER :  "+count1+"	CLIENT : "+count2);
    if(start&&player1!=null&&player2!=null)
      return(player1.toUpperCase()+" : "+count1+"     "+player2.toUpperCase()+": "+count2);
    return("SERVER :  "+count1+"	CLIENT : "+count2);
  }

}
