
//x-axis=column-axis
//y-axis=row-axis
//BOX[ROW][COLUMN]

public class Box {
  boolean left,right,top,bottom;//lines
  char initial;
  String name;
  int topLeftX, topLeftY;
  int topRightX, topRightY;
  int bottomLeftX, bottomLeftY;
  int bottomRightX,bottomRightY;//coordinate of box
  int cellHeight,cellWidth;

  public Box()
  {	cellHeight=50;
    cellWidth=50;
    left=right=top=bottom=false;
    initial=' ';
  }


  //the following method is not using in this Dots-PA4
  public Box(int size)
  {	cellHeight=cellWidth=size;
  }

  //the following is not using in the game, but it will print out the 4 corrdinate points of the box
  public void printPoint()
  {	System.out.print("Coordinate= [ ("+topLeftX+", "+topLeftY+"), ");
    System.out.print("("+topRightX+", "+topRightY+"), ");
    System.out.print("("+bottomLeftX+", "+bottomLeftY+"), ");
    System.out.println("("+bottomRightX+", "+bottomRightY+") ]\n");
  }

  //this will tell whether the box is completed
  public boolean isComplete() {
    if(left==true && right==true && bottom==true && top==true)	return true;
    return false;
  }

  //this will save the 4 coordinate points of the box
  public void setPoint(int a, int b, int c, int d) {
    topLeftX=a;	topLeftY=b;
    topRightX=c; topRightY=b;
    bottomLeftX=a; bottomLeftY=d;
    bottomRightX=c; bottomRightY=d;
  }

  //this will set the initial of the box
  public void setName(String name1)
  {	name=name1;
    if(initial!=' ')	return;
    if(initial==' ')	initial=Character.toUpperCase(name1.charAt(0));
  }
}

