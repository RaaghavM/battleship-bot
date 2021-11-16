package battleship;

// should show:
// name of ship owner
// shots taken
// hit percentage
// number of ships sunk


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BoardComponent extends JComponent implements MouseListener
{
  private static final String[] SHIP_NAMES = {null, "Destroyer", "Submarine", "Cruiser", "Battleship", "Carrier"};
  private static final int UNIDENTIFIED = 6;

  private int[][] board;
  private boolean[][] shots;
  private Location clickLoc;
  private boolean active;
  private boolean[] shipsSunk;
  private Location lastShot;
  private boolean shipsVisible;
  private Player player;
  private Player opponent;

  public BoardComponent()
  {
    shipsVisible = false;
    board = new int[10][10];
    shots = new boolean[10][10];
    active = false;
    shipsSunk = new boolean[6];
    lastShot = null;

    setPreferredSize(new Dimension(400, 400));

    addMouseListener(this);
  }

  public void paintComponent(Graphics g)
  {
    g.setColor(Color.BLUE);
    g.fillRect(0, 0, getWidth(), getHeight());
    int cellWidth = getWidth() / 10;
    int cellHeight = getHeight() / 10;
    for (int row = 0; row < 10; row++)
    {
      for (int col = 0; col < 10; col++)
      {
        g.setColor(Color.CYAN);
        int x = getWidth() * col / 10;
        int y = getHeight() * row / 10;
        g.drawRect(x, y, cellWidth, cellHeight);
      }
    }
    for (int ship = 1; ship <= 5; ship++)
    {
      if (shipsVisible || shipsSunk[ship])
      {
        int minRow = -1;
        int minCol = -1;
        int maxRow = -1;
        int maxCol = -1;
        for (int row = 0; row < 10; row++)
        {
          for (int col = 0; col < 10; col++)
          {
            if (board[row][col] == ship)
            {
              if (minRow == -1)
              {
                minRow = row;
                minCol = col;
              }
              maxRow = row;
              maxCol = col;
            }
          }
        }
        if (minRow != -1)
        {
          g.setColor(Color.GRAY);
          int x = getWidth() * minCol / 10;
          int y = getHeight() * minRow / 10;
          g.fillOval(x, y, getWidth() * (maxCol - minCol + 1) / 10, getHeight() * (maxRow - minRow + 1) / 10);
//          g.setColor(Color.BLACK);
//          g.drawOval(x, y, getWidth() * (maxCol - minCol + 1) / 10, getHeight() * (maxRow - minRow + 1) / 10);
        }
      }
    }

    for (int row = 0; row < 10; row++)
    {
      for (int col = 0; col < 10; col++)
      {
        if (shots[row][col])
        {
          int x = getWidth() * col / 10;
          int y = getHeight() * row / 10;

          if (lastShot != null && lastShot.getRow() == row && lastShot.getCol() == col)
          {
            g.setColor(Color.CYAN);
            g.fillOval(x + cellWidth / 8, y + cellHeight / 8, cellWidth * 3 / 4, cellHeight * 3 / 4);
          }

          if (board[row][col] == 0)
            g.setColor(Color.WHITE);
          else
            g.setColor(Color.RED);

          g.fillOval(x + cellWidth / 4, y + cellHeight / 4, cellWidth / 2, cellHeight / 2);
        }
      }
    }

//    if (!active)
//    {
//      g.setColor(new Color(0, 0, 0, 63));
//      g.fillRect(0, 0, getWidth(), getHeight());
//    }
  }

  //returns ID of ship shot
  public int shot(int row, int col)
  {
    //System.out.println("shot " + row + " " + col);
    lastShot = new Location(row, col);
    boolean alreadyShot = shots[row][col];
    shots[row][col] = true;
    update();
    if (alreadyShot)
      return 0;
    else
      return board[row][col];
  }

  public int[][] placeShips()
  {
    placeShip(5, 5);
    placeShip(4, 4);
    placeShip(3, 3);
    placeShip(2, 3);
    placeShip(1, 2);
    return board;
  }

  private void placeShip(int ship, int length)
  {
    Object[] options = {"Horizontal", "Vertical"};
    String message = "Choose direction to place " + SHIP_NAMES[ship] + " (length " + length + ")";
    int dir;
    int row;
    int col;
    int dRow;
    int dCol;
    do
    {
      do
        dir = JOptionPane.showOptionDialog(this,
                message,
                SHIP_NAMES[ship],
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[(int)(Math.random() * 2)]);
      while (dir < 0 || dir > 1);

      dRow = 0;
      dCol = 0;
      if (dir == 0)
        dCol = 1;
      else
        dRow = 1;

      clickLoc = null;
      while (clickLoc == null)
      {
        try{Thread.sleep(100);}catch(Exception e){}
      }

      row = clickLoc.getRow();
      col = clickLoc.getCol();
    }
    while (!shipFits(row, col, dRow, dCol, length));

    for (int i = 0; i < length; i++)
    {
      board[row][col] = ship;
      row += dRow;
      col += dCol;
    }

    update();
  }

  public Location getTarget()
  {
    clickLoc = null;
    while (clickLoc == null || shots[clickLoc.getRow()][clickLoc.getCol()])
    {
      try{Thread.sleep(100);}catch(Exception e){}
    }
    //System.out.println("got target");
    return clickLoc;
  }

  public void mouseEntered(MouseEvent e)
  {
    //ignore
  }

  public void mouseExited(MouseEvent e)
  {
    //ignore
  }

  public void mousePressed(MouseEvent e)
  {
    clickLoc = new Location(e.getY() * 10 / getHeight(),
            e.getX() * 10 / getWidth());
  }

  public void mouseReleased(MouseEvent e)
  {
    //ignore
  }

  public void mouseClicked(MouseEvent e)
  {
    //ignore
  }

  private boolean shipFits(int row, int col, int dRow, int dCol, int length)
  {
    for (int i = 0; i < length; i++)
    {
      if (row >= 10 || col >= 10)
        return false;
      if (board[row][col] != 0)
        return false;
      row += dRow;
      col += dCol;
    }
    return true;
  }

  public void targetHit(int shipSunk)
  {
    //System.out.println("targetHit");
    lastShot = clickLoc;
    int row = clickLoc.getRow();
    int col = clickLoc.getCol();
    shots[row][col] = true;
    if (shipSunk != 0)
      shipsSunk[shipSunk] = true;
    update();
//    if (shipSunk != 0)
//      JOptionPane.showMessageDialog(this, "You sunk the " + SHIP_NAMES[shipSunk]);
  }

  public void targetMissed()
  {
    //System.out.println("targetMissed");
    lastShot = clickLoc;
    int row = clickLoc.getRow();
    int col = clickLoc.getCol();
    shots[row][col] = true;
    update();
  }

  public void seriesStarted(Player player, Player opponent)
  {
    this.player = player;
    this.opponent = opponent;
  }

  public void roundStarted()
  {
    shipsVisible = true;
    board = new int[10][10];
    shots = new boolean[10][10];
    shipsSunk = new boolean[6];
    lastShot = null;
    update();
  }

  public void shipsPlaced()
  {
    shipsVisible = !opponent.isHuman();
    board = Player.copyBoard(player.getOriginalBoard());
    update();
  }

  public void setActive(boolean active)
  {
    //System.out.println("setActive " + active);
    this.active = active;
    update();
  }

  public void roundEnded()
  {
    shipsVisible = true;
    update();
  }

  public void update()
  {
    repaint();
    try{Thread.sleep(1);}catch(Exception e){}
  }
}