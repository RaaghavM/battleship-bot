package battleship;

public class Battleship
{
  public static void main(String[] args)
  {
    Display display = new Display(30);
    Strategy strategy1 = new Officer2();
    Strategy strategy2 = new RaaghavMalik();

    int win1 = 0;
    int win2 = 0;
    for (int c = 0; c < 100; c++) {

      int numWinsNeeded = 100;
      int winner = Game.play(strategy1, strategy2, display, numWinsNeeded);
      if (winner == 1)
        win1++;
      else
        win2++;
    }
    System.out.println("1 won " + win1);
    System.out.println("2 won " + win2);
  }

}