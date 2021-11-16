package battleship;

import java.util.*;

public class RaaghavMalik implements Strategy {
    //Like SmarterApprentice, but improved targeting algorithm that relies on randomly sampled probability (per ship and then added)

    private boolean sinkMode;
    private ArrayList<Location> recentHits;
    private Location justAttacked;
    private Set<Location> attackedLocs;
    private Set<Location> findLocs;
    private int[][] currentBoard;
    private Set<Integer> aliveShips;
    private int numHitsBoosterParam;
    private boolean printStuff;
    private int[][] opponentShots;
    //private Set<Location>
    private int currentRound;

    public void seriesStarted(int numWinsNeeded) {
        opponentShots = new int[10][10];
        currentRound = 1;
    }

    public int[][] placeShips() {
       // if (currentRound <= 5) {
            int[][] board = new int[10][10];
            int[][] temp = new int[10][10];
            for (int shipType = 5; shipType >= 1; shipType--) {
                Location shipLoc = new Location((int) (Math.random() * 10), (int) (Math.random() * 10));
                boolean direction = Math.random() > 0.5;
                temp = placeSingleShip(temp, shipLoc, direction, shipType);
                while ((temp == null) || shipsTooClose(temp)) {
                    shipLoc = new Location((int) (Math.random() * 10), (int) (Math.random() * 10));
                    direction = Math.random() > 0.5;
                    temp = new int[10][10];
                    for (int i = 0; i < board.length; i++) {
                        temp[i] = board[i].clone();
                    }
                    temp = placeSingleShip(temp, shipLoc, direction, shipType);
                }
                board = temp;
                //for (int[] row : board)
                //System.out.println("board placed " + shipType + ": " + Arrays.toString(row));
            }

            attackedLocs = new HashSet<Location>();
            sinkMode = false;
            recentHits = new ArrayList<Location>();
            justAttacked = null;
            findLocs = new HashSet<Location>();
            aliveShips = new HashSet<Integer>();
            for (int i = 1; i <= 5; i++) aliveShips.add(i);
            currentBoard = new int[10][10];
            numHitsBoosterParam = 500;
            printStuff = false;


            for (int r = 0; r < 10; r += 2) {
                for (int c = 0; c < 10; c += 2) {
                    findLocs.add(new Location(r, c));
                }
            }
            for (int r = 1; r < 10; r += 2) {
                for (int c = 1; c < 10; c += 2) {
                    findLocs.add(new Location(r, c));
                }
            }
            //for (int[] row : board)
            //    System.out.println("board: " + Arrays.toString(row));
            return board;
       // }
        //else
        //{

        //}
    }

    private boolean shipsTooClose(int[][] board) //need to implement
    {
        for (int r = 0; r < 10; r++)
        {
            for (int c = 0; c < 10; c++)
            {
                if (board[r][c] != 0)
                {
                    if (inRange(new Location(r+1, c)) && board[r+1][c] != board[r][c] && board[r+1][c] > 0)
                        return true;
                    if (inRange(new Location(r-1, c)) && board[r-1][c] != board[r][c] && board[r-1][c] > 0)
                        return true;
                    if (inRange(new Location(r, c+1)) && board[r][c+1] != board[r][c] && board[r][c+1] > 0)
                        return true;
                    if (inRange(new Location(r, c-1)) && board[r][c-1] != board[r][c] && board[r][c-1] > 0)
                        return true;
                }
            }
        }
        return false;
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
        //System.out.println(findLocs.toString());
        double[][] probBoard = new double[10][10];
        int total = 0;
        for (Integer shipType : aliveShips) //need to not think of removed ships, and if a ship is removed, does not count as hit but as a blocked out space
        {
            for (int r = 0; r < currentBoard.length; r++)
            {
                for (int c = 0; c < currentBoard[r].length; c++)
                {
                    int length = shipType;
                    if (length == 2 || length == 1) length++;
                    //horizontal placement
                    ArrayList<Location> testList = new ArrayList<Location>();
                    boolean valid = true;
                    int numHits = 1;
                    for (int i = 0; i < length; i++)
                    {
                        Location l = new Location(r, c+i);
                        if (inRange(l)) testList.add(l);
                        else valid = false;
                    }
                    if (valid)
                    {
                        for (Location l : testList)
                        {
                            if (isMiss(l) || isSunk(l)) valid = false;
                            else if (isHit(l)) numHits += numHitsBoosterParam;
                        }
                    }
                    if (valid)
                    {
                        for (int i = 0; i < length; i++)
                        {
                            Location l = new Location(r, c+i);
                            probBoard[l.getRow()][l.getCol()] += numHits;
                            //System.out.println(numHits);
                            total++;
                        }
                    }

                    //vertical placement
                    ArrayList<Location> testList2 = new ArrayList<Location>();
                    boolean valid2 = true;
                    int numHits2 = 1;
                    for (int i = 0; i < length; i++)
                    {
                        Location l = new Location(r+i, c);
                        if (inRange(l)) testList2.add(l);
                        else valid2 = false;
                    }
                    if (valid2)
                    {
                        for (Location l : testList2)
                        {
                            if (isMiss(l) || isSunk(l)) valid2 = false;
                            if (isHit(l)) numHits2 += numHitsBoosterParam;
                        }
                    }
                    if (valid2)
                    {
                        for (int i = 0; i < length; i++)
                        {
                            Location l = new Location(r+i, c);
                            probBoard[l.getRow()][l.getCol()] += numHits2;
                            total++;
                        }
                    }
                    if (alreadyAttacked(new Location(r,c)))
                    {
                        probBoard[r][c] = 0;
                    }
                }
            }
        }
        if (printStuff) {
            System.out.println("");
            System.out.println("");

            for (double[] row : probBoard) {
                System.out.println(Arrays.toString(row));
            }

            System.out.println("");
            System.out.println("");

            for (int[] row : currentBoard) {
                System.out.println(Arrays.toString(row));
            }


            System.out.println("");
            System.out.println("");
        }

        double max = probBoard[0][0];
        Location maxLoc = new Location(0, 0);

        for (int r = 0; r < probBoard.length; r++)
        {
            for (int c = 0; c < probBoard.length; c++)
            {
                if (probBoard[r][c] >= max)
                {
                    max = probBoard[r][c];
                    maxLoc = new Location(r,c);
                }
            }
        }

        attackedLocs.add(maxLoc);
        justAttacked = maxLoc;

        return maxLoc;
    }

    public void targetMissed()
    {

        currentBoard[justAttacked.getRow()][justAttacked.getCol()] = -1;
    }

    private boolean surrounded(Location loc)
    {
        Set<Location> adj = new HashSet<Location>();
        if (loc.getRow()-1 >= 0) adj.add(new Location(loc.getRow()-1,loc.getCol()));
        if (loc.getRow()+1 < 10) adj.add(new Location(loc.getRow()+1,loc.getCol()));
        if (loc.getCol()-1 >= 0) adj.add(new Location(loc.getRow(),loc.getCol()-1));
        if (loc.getCol()+1 < 10) adj.add(new Location(loc.getRow(),loc.getCol()+1));
        return (attackedLocs.containsAll(adj));
    }

    public void targetHit(int shipSunk)
    {
        currentBoard[justAttacked.getRow()][justAttacked.getCol()] = 1;
        if (shipSunk == 0)
        {
            sinkMode = true;
            recentHits.add(justAttacked);
        }
        else
        {
            Set<Location> sunkLocs = new HashSet<Location>();
            int length = shipSunk;
            if (shipSunk == 1 || shipSunk == 2) length++;
            for (int i = 0; i < 4; i++)
            {
                if (printStuff)
                    System.out.println("about to test");
                if (shipTester(length, i))
                {
                    if (printStuff)
                        System.out.println("Sunk and tested with length " + length + " and direction " + i);
                    if (i == 0)
                    {
                        for (int j = 0; j < length; j++) {
                            Location searchLoc = new Location(justAttacked.getRow() - j, justAttacked.getCol());
                            sunkLocs.add(searchLoc);
                        }
                    }
                    else if (i == 1)
                    {
                        for (int j = 0; j < length; j++) {
                            Location searchLoc = new Location(justAttacked.getRow() + j, justAttacked.getCol());
                            sunkLocs.add(searchLoc);
                        }
                    }
                    else if (i == 2)
                    {
                        for (int j = 0; j < length; j++) {
                            Location searchLoc = new Location(justAttacked.getRow(), justAttacked.getCol() + j);
                            sunkLocs.add(searchLoc);
                        }
                    }
                    else if (i == 3)
                    {
                        for (int j = 0; j < length; j++) {
                            Location searchLoc = new Location(justAttacked.getRow(), justAttacked.getCol() - j);
                            sunkLocs.add(searchLoc);
                        }
                    }
                    break;
                }
            }
            for (Location l : sunkLocs)
            {
                currentBoard[l.getRow()][l.getCol()] = 2;
            }
            sinkMode = false;
            aliveShips.remove(shipSunk);
        }
    }

    private boolean shipTester(int length, int direction)
    {
        if (direction == 0) {
            for (int i = 0; i < length; i++) {
                Location searchLoc = new Location(justAttacked.getRow() - i, justAttacked.getCol());
                if (!isHit(searchLoc)) return false;
            }
        }
        else if (direction == 1) {
            for (int i = 0; i < length; i++) {
                Location searchLoc = new Location(justAttacked.getRow() + i, justAttacked.getCol());
                if (!isHit(searchLoc)) return false;
            }
        }
        else if (direction == 2) {
            for (int i = 0; i < length; i++) {
                Location searchLoc = new Location(justAttacked.getRow(), justAttacked.getCol() + i);
                if (!isHit(searchLoc)) return false;
            }
        }
        else if (direction == 3) {
            for (int i = 0; i < length; i++) {
                Location searchLoc = new Location(justAttacked.getRow(), justAttacked.getCol() - i);
                if (!isHit(searchLoc)) return false;
            }
        }
        return true;
    }

    public void opponentShot(int row, int col)
    {
        opponentShots[row][col]++;
    }

    public void roundEnded(int outcome, int[][] opponentShips)
    {
        currentRound++;
    }

    public void seriesEnded(int numRoundsWon, int numRoundsLost, int numRoundsTied)
    {
        if (printStuff) {
            System.out.println("RaaghavMalik won " + numRoundsWon);
            System.out.println("RaaghavMalik lost " + numRoundsLost);
            System.out.println("RaaghavMalik tied " + numRoundsTied);
        }
    }

    private Location stepDirection(Location loc, int dir)
    {
        dir = dir % 4;
        Location ret = null;
        if (dir == 0) ret = new Location(loc.getRow() - 1, loc.getCol());
        else if (dir == 1) ret = new Location(loc.getRow(), loc.getCol() + 1);
        else if (dir == 2) ret = new Location(loc.getRow() + 1, loc.getCol());
        else if (dir == 3) ret = new Location(loc.getRow(), loc.getCol() - 1);
        if (inRange(ret))
            return ret;
        else
            return null;
    }

    private boolean alreadyAttacked(Location loc)
    {
        if (inRange(loc))
            return currentBoard[loc.getRow()][loc.getCol()] != 0;
        return true;
    }

    private boolean inRange(Location loc)
    {
        int r = loc.getRow();
        int c = loc.getCol();
        return (r >= 0 && r < 10 && c >= 0 && c < 10);
    }

    private boolean isHit(Location loc)
    {
        if (inRange(loc))   return currentBoard[loc.getRow()][loc.getCol()] == 1;
        //if (!inRange(loc)) System.out.println("not in range!");
        return false;
    }

    private boolean isMiss(Location loc)
    {
        if (inRange(loc)) return currentBoard[loc.getRow()][loc.getCol()] == -1;
        //if (!inRange(loc)) System.out.println("not in range!");
        return false;
    }

    private boolean isSunk(Location loc)
    {
        if (inRange(loc)) return currentBoard[loc.getRow()][loc.getCol()] == 2;
        return false;
    }

}
