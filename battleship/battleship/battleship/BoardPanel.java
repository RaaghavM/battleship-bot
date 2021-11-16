package battleship;

import java.awt.*;
import javax.swing.*;

public class BoardPanel extends JPanel
{
  private BoardComponent board;
  private JLabel titleLabel;
  private JLabel statsLabel;
  private Player player;
  private Player opponent;
  private JLabel scoreLabel;

  public BoardPanel()
  {
    setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    setBackground(Color.BLACK);
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    titleLabel = new JLabel("Player's Ships");
    titleLabel.setFont(new Font(null, Font.PLAIN, 24));
    titleLabel.setForeground(Color.WHITE);
    add(titleLabel);

    statsLabel = new JLabel(" ");
    statsLabel.setFont(new Font(null, Font.PLAIN, 18));
    statsLabel.setForeground(Color.WHITE);
    add(statsLabel);

    board = new BoardComponent();
    add(board);

    scoreLabel = new JLabel(" ");
    scoreLabel.setFont(new Font(null, Font.PLAIN, 24));
    scoreLabel.setForeground(Color.WHITE);
    add(scoreLabel);
  }

  public void shot(int row, int col)
  {
    board.shot(row, col);
    updateStats();
  }

  public int[][] placeShips()
  {
    return board.placeShips();
  }

  public Location getTarget()
  {
    return board.getTarget();
  }

  public void targetHit(int shipSunk)
  {
    board.targetHit(shipSunk);
  }

  public void targetMissed()
  {
    board.targetMissed();
  }

  public void seriesStarted(Player player, Player opponent)
  {
    this.player = player;
    this.opponent = opponent;
    titleLabel.setText(player.getName() + "'s Ships");
    board.seriesStarted(player, opponent);
  }

  public void roundStarted()
  {
    board.roundStarted();
  }

  public void shipsPlaced()
  {
    board.shipsPlaced();
  }

  public void setActive(boolean active)
  {
//    Color color = active ? Color.WHITE : Color.GRAY;
//    titleLabel.setForeground(color);
//    statsLabel.setForeground(color);
    board.setActive(active);
  }

  public void roundEnded()
  {
    board.roundEnded();
    int won = opponent.getRoundsWon();
    scoreLabel.setText(opponent.getName() + " won " + won + " round" + (won == 1 ? "" : "s"));
  }

  private void updateStats()
  {
    int numHits = opponent.getNumHits();
    int total = numHits + opponent.getNumMisses();
    statsLabel.setText(opponent.getName() + "'s Hit Rate:  " + numHits + " / " + total + " = " + (100 * numHits / total) + "%");
  }

  public Player getPlayer()
  {
    return player;
  }
}