package battleship;

public class Game
{
    //plays a series of rounds until one strategy wins the given number of times
    public static int play(Strategy strategy1, Strategy strategy2, Display display, int numWinsNeeded)
    {
        Player[] players = {new Player(strategy1), new Player(strategy2)};

        for (Player p : players)
            p.seriesStarted(numWinsNeeded);

        if (display != null)
            display.seriesStarted(players[0], players[1], numWinsNeeded);

        while (players[0].getRoundsWon() < numWinsNeeded && players[1].getRoundsWon() < numWinsNeeded)
            playRound(players, display);

        for (Player p : players)
            p.seriesEnded();

        int winner;
        if (players[0].getRoundsWon() == numWinsNeeded)
            winner = 1;
        else
            winner = 2;

        if (display != null)
            display.seriesEnded(winner);

        return winner;
    }

    //player 1 always goes first, then player 2
    //if player 2 sinks player 1's last ship, then round ends and player 2 wins. returns 2.
    //if player 1 sinks player 2's last ship, then player 2 gets one more shot.
    //if player 2 fails to sink player 1's last ship with that shot, then player 1 wins. returns 1.
    //if player 2 sinks player 1's last ship with that shot, then the round ends in a tie. return 0.
    private static int playRound(Player[] players, Display display)
    {
        if (display != null)
            display.roundStarted();

        for (Player p : players)
            p.placeShips();

        if (display != null)
            display.shipsPlaced();

        while (players[0].hasShips() && players[1].hasShips())
        {
            for (int curr = 0; curr < 2; curr++)
            {
                Player current = players[curr];
                Player opponent = players[1 - curr];

                if (display != null)
                    display.setActive(curr + 1);

                Location target = current.getTarget();
                int row = target.getRow();
                int col = target.getCol();
                int ship = opponent.opponentShot(row,col);
                if (ship == 0)
                {
                    current.targetMissed();
                }
                else
                {
                    if (opponent.findShip(ship) == null)
                        current.targetHit(ship);
                    else
                        current.targetHit(0);
                }
                if (display != null)
                    display.shot(2 - curr, row, col);
            }
        }

        int winner;
        if (players[0].hasShips())
        {
            winner = 1;
            players[0].roundEnded(+1, players[1].getOriginalBoard());
            players[1].roundEnded(-1, players[0].getOriginalBoard());
        }
        else if (players[1].hasShips())
        {
            winner = 2;
            players[0].roundEnded(-1, players[1].getOriginalBoard());
            players[1].roundEnded(+1, players[0].getOriginalBoard());
        }
        else
        {
            winner = 0;
            players[0].roundEnded(0, players[1].getOriginalBoard());
            players[1].roundEnded(0, players[0].getOriginalBoard());
        }

        if (display != null)
            display.roundEnded(winner);

        return winner;
    }
}