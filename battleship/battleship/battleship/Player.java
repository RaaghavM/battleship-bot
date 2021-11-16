package battleship;

public class Player
{
  private Strategy strategy;
  private int[][] originalBoard;
  private int[][] board;
  private boolean[] sunkShips;
  private int numHits;
  private int numMisses;
  private int roundsWon;
  private int roundsLost;
  private int roundsTied;

  public Player(Strategy strategy)
  {
    this.strategy = strategy;
    roundsWon = 0;
    roundsLost = 0;
    roundsTied = 0;
  }

  public void placeShips()
  {
    board = new int[10][10];
    sunkShips = new boolean[6];
    numHits = 0;
    numMisses = 0;
    board = strategy.placeShips();
    if (board == null)
      System.out.println(strategy.getClass().getName() + " returned null board");
    else
    {
      originalBoard = copyBoard(board);
      board = copyBoard(board);
      checkBoard();
    }
  }

  public static int[][] copyBoard(int[][] board)
  {
    int[][] copy = new int[board.length][board[0].length];
    for (int row = 0; row < board.length; row++)
    {
      for (int col = 0; col < board[0].length; col++)
        copy[row][col] = board[row][col];
    }
    return copy;
  }

  private void checkBoard()
  {
    String name = strategy.getClass().getName();
    if (board.length != 10 || board[0].length != 10)
      throw new RuntimeException(name + " has " + board.length + " rows and " + board[0].length + " columns");
    int[][] copy = copyBoard(board);
    removeShip(copy, 1, 2);
    removeShip(copy, 2, 3);
    removeShip(copy, 3, 3);
    removeShip(copy, 4, 4);
    removeShip(copy, 5, 5);
    for (int row = 0; row < 10; row++)
    {
      for (int col = 0; col < 10; col++)
      {
        if (copy[row][col] != 0)
          throw new RuntimeException(name + " has extra ship part " + copy[row][col] + " at (" + row + ", " + col + ")");
      }
    }
  }

  private void removeShip(int[][] board, int ship, int length)
  {
    String name = strategy.getClass().getName();
    Location loc = findShip(ship);
    if (loc == null)
      throw new RuntimeException(name + " does not have ship " + ship + " of length " + length);
    int row = loc.getRow();
    int col = loc.getCol();
    int drow = 0;
    int dcol = 0;
    if (col < 9 && board[row][col + 1] == ship)
      dcol = 1;  //horizontal
    else
      drow = 1;  //vertical
    for (int i = 0; i < length; i++)
    {
      if (board[row][col] != ship)
        throw new RuntimeException(name + " has ship " + ship + " shorter than length " + length);
      board[row][col] = 0;
      row += drow;
      col += dcol;
    }
  }

  //asks player to choose a target and returns it
  public Location getTarget()
  {
    Location loc = strategy.getTarget();
    int row = loc.getRow();
    int col = loc.getCol();
    if (row < 0 || row > 9 || col < 0 || col > 9)
      throw new RuntimeException(strategy.getClass().getName() + " returned invalid location (" + row + ", " + col + ")");
    return loc;
  }

  //tells player that opponent took a shot at these coordinates
  //returns 0 if miss/empty, and ship ID if hit (regardless of whether was sunk)
  public int opponentShot(int row, int col)
  {
    int ship = board[row][col];
    board[row][col] = 0;
    if (ship != 0 && findShip(ship) == null)
      sunkShips[ship] = true;
    strategy.opponentShot(row, col);
    return ship;
  }

  //reports back to player that their shot missed
  public void targetMissed()
  {
    numMisses++;
    strategy.targetMissed();
  }

  //reports back to player that their shot hit a ship
  public void targetHit(int shipSunk)
  {
    numHits++;
    strategy.targetHit(shipSunk);
  }

  public Location findShip(int ship)
  {
    for (int row = 0; row < 10; row++)
    {
      for (int col = 0; col < 10; col++)
      {
        if (board[row][col] == ship)
          return new Location(row, col);
      }
    }
    return null;
  }

  public int[][] getOriginalBoard()
  {
    return originalBoard;
  }

  public boolean isHuman()
  {
    return strategy instanceof Human;
  }

  public boolean hasShips()
  {
    for (int i = 1; i <= 5; i++)
    {
      if (!sunkShips[i])
        return true;
    }
    return false;
  }

  public String getName()
  {
    return strategy.getClass().getName();
  }

  public void roundEnded(int outcome, int[][] opponentShips)
  {
    if (outcome == 1)
      roundsWon++;
    else if (outcome == -1)
      roundsLost++;
    else
      roundsTied++;

    strategy.roundEnded(outcome, opponentShips);
  }

  //returns the number of times this player has successfully hit their opponent
  public int getNumHits()
  {
    return numHits;
  }

  //returns the number of times this player has missed their opponent
  public int getNumMisses()
  {
    return numMisses;
  }

  public void seriesStarted(int targetWins)
  {
    strategy.seriesStarted(targetWins);
  }

  public int getRoundsWon()
  {
    return roundsWon;
  }

  public void seriesEnded()
  {
    strategy.seriesEnded(roundsWon, roundsLost, roundsTied);
  }
}