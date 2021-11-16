package battleship;

public interface Strategy
{
  //Called when a series starts against a new opponent.
  //Play continues until one player reaches given number of wins.
  void seriesStarted(int numWinsNeeded);

  //Called when a new round starts.
  //Returns a 10x10 array showing the location of all ships, where
  //0 = empty,
  //1 = destroyer (length 2),
  //2 = submarine (length 3),
  //3 = cruiser (length 4),
  //4 = battleship (length 4), and
  //5 = carrier (length 5).
  int[][] placeShips();

  //Called when this strategy must fire a shot at opponent.
  //Returns (row, col) to fire at opponent.
  Location getTarget();

  //Called after getTarget, to inform player that recently fired shot did not hit opponent ship.
  void targetMissed();

  //Called after getTarget, to inform player that recently fired shot hit opponent's ship.
  //shipSunk is ID of ship completely sunk
  //(1 = destroyer, 2 = submarine, 3 = cruiser, 4 = battleship, and 5 = carrier),
  //or 0 if ship has not sunk yet.
  void targetHit(int shipSunk);

  //Called when opponent has fired a shot at (row, col).
  void opponentShot(int row, int col);

  //Called at the end of a round outcome is +1 if this strategy won,
  //-1 if this strategy lost, and 0 if a tie occurred.
  //opponentShips is a 10x10 array showing the location of all opponent ships, where
  //0 = empty,
  //1 = destroyer (length 2),
  //2 = submarine (length 3),
  //3 = cruiser (length 4),
  //4 = battleship (length 4), and
  //5 = carrier (length 5).
  void roundEnded(int outcome, int[][] opponentShips);

  //Called at the end of a series of rounds against a single opponent.
  void seriesEnded(int numRoundsWon, int numRoundsLost, int numRoundsTied);
}