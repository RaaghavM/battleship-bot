package battleship;

import java.awt.*;
import javax.swing.*;

public class Display
{
  private BoardPanel board1;
  private BoardPanel board2;
  private JFrame frame;
  private int numShots;
  private int desiredSeconds;
  private long startTime;
  private int winsNeeded;

  //default game time of 60 seconds
  public Display()
  {
    this(60);
  }

  public Display(int desiredSeconds)
  {
    this.desiredSeconds = desiredSeconds;

    frame = new JFrame("Battleship");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    //frame.getContentPane().setLayout(null);
    frame.getContentPane().setLayout(new GridLayout(1, 2));
    frame.getContentPane().setBackground(Color.BLACK);

    board1 = new BoardPanel();
    frame.getContentPane().add(board1);
    //board1.setBounds(25, 25, 400, 450);

    board2 = new BoardPanel();
    frame.getContentPane().add(board2);
    //board2.setBounds(450, 25, 400, 450);

    //frame.setSize(875, 475);

    frame.pack();
    frame.setVisible(true);
  }

  public void shot(int player, int row, int col)
  {
    numShots++;

    int maxHits = Math.max(board1.getPlayer().getNumHits(), board2.getPlayer().getNumHits());
    double shotsRemaining = (double)numShots * (double)(17 - maxHits) / maxHits;
    int timeRemaining = 1000 * desiredSeconds - (int)(System.currentTimeMillis() - startTime);
    int timePerShot = Math.min(2000, (int)(timeRemaining / shotsRemaining));
//    System.out.println("--------------------------------------------------");
//    System.out.println("maxHits = " + maxHits);
//    System.out.println("numShots = " + numShots);
//    System.out.println("shotsRemaining = " + shotsRemaining);
//    System.out.println("timeRemaining = " + timeRemaining);
//    System.out.println("timePerShot = " + timePerShot);
    try{Thread.sleep(timePerShot);}catch(Exception e){}

    if (player == 1)
      board1.shot(row, col);
    else
      board2.shot(row, col);
  }

  public int[][] placeShips()
  {
    return board1.placeShips();
  }

  public Location getTarget()
  {
    return board2.getTarget();
  }

  public void targetHit(int shipSunk)
  {
    board2.targetHit(shipSunk);
  }

  public void targetMissed()
  {
    board2.targetMissed();
  }

  public void seriesStarted(Player player1, Player player2, int winsNeeded)
  {
    this.winsNeeded = winsNeeded;
    board1.seriesStarted(player1, player2);
    board2.seriesStarted(player2, player1);
    if (player1.isHuman() || player2.isHuman())
      desiredSeconds = 0;
  }

  public void roundStarted()
  {
    numShots = 0;
    startTime = System.currentTimeMillis();
    board1.roundStarted();
    board2.roundStarted();
  }

  public void setActive(int player)
  {
    board1.setActive(player == 2);
    board2.setActive(player == 1);
  }

  public void roundEnded(int winner)
  {
    board1.roundEnded();
    board2.roundEnded();
    String message;
    if (winner == 1)
      message = board1.getPlayer().getName() + " won the round";
    else if (winner == 2)
      message = board2.getPlayer().getName() + " won the round";
    else
      message = "Tie round";
    JOptionPane.showMessageDialog(frame, message + " after " + (numShots / 2) + " shots");
  }

  public void setDesiredLength(int numSeconds)
  {
    desiredSeconds = numSeconds;
  }

  public void seriesEnded(int winner)
  {
    String message;
    if (winner == 1)
      message = board1.getPlayer().getName();
    else
      message = board2.getPlayer().getName();
    JOptionPane.showMessageDialog(frame, message + " won the series");
  }

  public void shipsPlaced()
  {
    board1.shipsPlaced();
    board2.shipsPlaced();
  }
}