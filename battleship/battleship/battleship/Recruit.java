package battleship;

import java.util.*;

public class Recruit implements Strategy {
    //aims randomly, but keeps track of what it has already attacked and doesn't attack it again


    private Set<Location> attackedLocs;

    public void seriesStarted(int numWinsNeeded) {
        //ignored
    }

    public int[][] placeShips() {
        int[][] board = new int[10][10];
        int[][] temp = new int[10][10];
        for (int shipType = 5; shipType >= 1; shipType--) {
            Location shipLoc = new Location((int) (Math.random() * 10), (int) (Math.random() * 10));
            boolean direction = Math.random() > 0.5;
            temp = placeSingleShip(temp, shipLoc, direction, shipType);
            while (temp == null) {
                shipLoc = new Location((int) (Math.random() * 5), (int) (Math.random() * 5));
                direction = Math.random() > 0.5;
                temp = new int[10][10];
                for (int i = 0; i < board.length; i++)
                { temp[i] = board[i].clone(); }
                temp = placeSingleShip(temp, shipLoc, direction, shipType);
            }
            board = temp;
            //for (int[] row : board)
                //System.out.println("board placed " + shipType + ": " + Arrays.toString(row));
        }

        attackedLocs = new HashSet<Location>();
        //for (int[] row : board)
            //System.out.println("board: " + Arrays.toString(row));
        return board;
    }

    private int[][] placeSingleShip(int[][] currentBoard, Location loc, boolean direction, int shipType)
    {
        int[][] ret = new int[currentBoard.length][currentBoard[0].length];
        for (int i = 0; i < currentBoard.length; i++)
        {
            for (int j = 0; j < currentBoard[0].length; j++)
            {
                ret[i][j] = currentBoard[i][j];
            }
        }
        int length = shipType;
        if (length == 2 || length == 1) length++;
        for (int i = 0; i < length; i++)
        {
            if (direction == true) //vertical placement
            {
                if (loc.getRow()+i < 10 && loc.getCol() < 10 && ret[loc.getRow()+i][loc.getCol()] == 0)
                    ret[loc.getRow()+i][loc.getCol()] = shipType;
                else {
                    return null;
                }
            }
            else //horizontal placement
            {
                if (loc.getRow() < 10 && loc.getCol()+i < 10 && ret[loc.getRow()][loc.getCol()+i] == 0)
                    ret[loc.getRow()][loc.getCol()+i] = shipType;
                else
                    return null;
            }
        }
        return ret;
    }

    public Location getTarget()
    {
        int r = (int)(Math.random()*10);
        int c = (int)(Math.random()*10);
        while (attackedLocs.contains(new Location(r, c))) { //might be better to just change either r or c instead of both?
            r = (int) (Math.random() * 10);
            c = (int) (Math.random() * 10);
        }
        Location attackLoc = new Location(r,c);
        attackedLocs.add(attackLoc);
        return attackLoc;
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