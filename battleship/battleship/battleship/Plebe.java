package battleship;

public class Plebe implements Strategy
{
  public void seriesStarted(int numWinsNeeded)
  {
    //ignored
  }
  
  public int[][] placeShips()
  {
    int[][] board = {
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 1, 0, 0, 0, 5, 0, 0, 0, 0},
      {0, 1, 0, 0, 0, 5, 0, 0, 4, 0},
      {0, 2, 3, 3, 3, 5, 0, 0, 4, 0},
      {0, 2, 0, 0, 0, 5, 0, 0, 4, 0},
      {0, 2, 0, 0, 0, 5, 0, 0, 4, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    return board;
  }
  
  public Location getTarget()
  {
    return new Location(0, 0);
  }
  
  public void targetMissed()
  {
    //ignored
  }
  
  public void targetHit(int shipSunk)
  {
    //ignored
  }
  
  public void opponentShot(int row, int col)
  {
    //ignored
  }

  public void roundEnded(int outcome, int[][] opponentShips)
  {
    //ignored
  }
  
  public void seriesEnded(int numRoundsWon, int numRoundsLost, int numRoundsTied)
  {
    //ignored
  }
}