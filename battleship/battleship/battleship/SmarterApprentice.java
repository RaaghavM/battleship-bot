package battleship;

import java.util.*;

public class SmarterApprentice implements Strategy {
    //smarter apprentice bot (same as apprentice, but makes use of parity when searching for ships)
    //aims somewhat randomly, but keeps track of what it has already attacked and doesn't attack it again
    //and if it hits a ship, targets nearby areas until it sinks the ship

    private boolean sinkMode;
    private ArrayList<Location> recentHits;
    private Location justAttacked;
    private Set<Location> attackedLocs;
    private Set<Location> findLocs;
    private int currAttackDir;

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
        sinkMode = false;
        recentHits = new ArrayList<Location>();
        justAttacked = null;
        findLocs = new HashSet<Location>();
        currAttackDir = -1;
        for (int r = 0; r < 10; r += 2)
        {
            for (int c = 0; c < 10; c += 2)
            {
                findLocs.add(new Location(r,c));
            }
        }
        for (int r = 1; r < 10; r += 2)
        {
            for (int c = 1; c < 10; c += 2)
            {
                findLocs.add(new Location(r,c));
            }
        }
        //for (int[] row : board)
        //    System.out.println("board: " + Arrays.toString(row));
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
        //System.out.println(findLocs.toString());
        if (!sinkMode && findLocs.size() > 0) {
            Location attackLoc = null;
            int randIndex = 1 + (int)(Math.random()*findLocs.size());
            Iterator i = findLocs.iterator();
            int count = 0;
            while (count != randIndex)
            {
                attackLoc = (Location)(i.next());
                count++;
            }
            findLocs.remove(attackLoc);
            attackedLocs.add(attackLoc);
            justAttacked = attackLoc;
            return attackLoc;
        }
        else if (!sinkMode && findLocs.size() == 0)
        {
            int r = (int) (Math.random() * 10);
            int c = (int) (Math.random() * 10);
            while (attackedLocs.contains(new Location(r, c))) { //might be better to just change either r or c instead of both?
                r = (int) (Math.random() * 10);
                c = (int) (Math.random() * 10);
            }
            Location attackLoc = new Location(r, c);
            attackedLocs.add(attackLoc);
            justAttacked = attackLoc;
            return attackLoc;
        }
        else
        {
            Location recentHit = recentHits.get(recentHits.size()-1);
            int r = recentHit.getRow();
            int c = recentHit.getCol();
            int dir;
            Location attackLoc = new Location(r, c);
            Set<Integer> dirs = new HashSet<Integer>();
            while ((r >= 10 || r < 0 || c >= 10 || c < 0 || attackedLocs.contains(attackLoc)) && sinkMode) {
                //System.out.println(recentHit.toString());
                if (surrounded(recentHit))
                {
                    //System.out.println("surrounded!");
                    if (recentHits.size() - 1 >= 0) {
                        recentHits.remove(recentHits.size() - 1);
                        recentHit = recentHits.get(recentHits.size() - 1);
                    }
                    else {
                        sinkMode = false;
                        break;
                    }
                }
                r = recentHit.getRow();
                c = recentHit.getCol();
                dir = (int) (Math.random() * 4);
                dirs.add(dir);
                switch (dir) {
                    case 0:
                        r++;
                        break;
                    case 1:
                        r--;
                        break;
                    case 2:
                        c++;
                        break;
                    case 3:
                        c--;
                        break;
                }

                attackLoc = new Location(r, c);
            }
            if (findLocs.contains(attackLoc))
                findLocs.remove(attackLoc);
            attackedLocs.add(attackLoc);
            justAttacked = attackLoc;
            return attackLoc;
        }
    }

    public void targetMissed()
    {
        //ignored
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
        if (shipSunk == 0)
        {
            sinkMode = true;
            recentHits.add(justAttacked);
        }
        else
        {
            switch(shipSunk)
            {
                case 1: //2
                    recentHits.remove(justAttacked);

                    break;
                case 2: //3
                case 3: //3
                case 4: //4
                case 5: //5
            }
            sinkMode = false;
        }
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
