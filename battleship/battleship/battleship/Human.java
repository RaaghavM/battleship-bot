package battleship;

public class Human implements Strategy
{
  private Display display;
  
  public Human(Display display)
  {
    this.display = display;
  }
  
  public void seriesStarted(int numWinsNeeded)
  {
  }
  
  public int[][] placeShips()
  {
    return display.placeShips();
  }
  
  public Location getTarget()
  {
    return display.getTarget();
  }
  
  public void targetHit(int shipSunk)
  {
    display.targetHit(shipSunk);
  }
  
  public void targetMissed()
  {
    display.targetMissed();
  }

  public void opponentShot(int row, int col)
  {
    //ignore
  }

  public void roundEnded(int outcome, int[][] opponentShips)
  {
    //ignore
  }
  
  public void seriesEnded(int numRoundsWon, int numRoundsLost, int numRoundsTied)
  {
    //ignore
  }
}