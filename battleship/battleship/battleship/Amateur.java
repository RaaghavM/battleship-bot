package battleship;

import java.util.*;

public class Amateur implements Strategy {
    //has knowledge of what has been hit or miss
    //like an amateur, is a tryhard, and uses an overly complex algorithm for not great results (and doesn't even work --> has some null pointer/stack overflow errors that rise up in certain cases :( )

    private Location currentShipAttack;
    private int[][] knowledge;
    private Location justAttacked;

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

        currentShipAttack = null;
        justAttacked = null;
        knowledge = new int[10][10];
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
        //System.out.println(currentShipAttack);
        if (currentShipAttack == null) { //can improve random to search for ships better, perhaps do equally spaced out quadrants
            int r = (int) (Math.random() * 10);
            int c = (int) (Math.random() * 10);
            while (alreadyAttacked(new Location(r,c))) { //might be better to just change either r or c instead of both?
                r = (int) (Math.random() * 10);
                c = (int) (Math.random() * 10);
            }
            Location attackLoc = new Location(r, c);
            justAttacked = attackLoc;
            return attackLoc;
        }
        else
        {
            System.out.println(currentShipAttack);
            Location attackLoc = attackNearby(currentShipAttack);
            justAttacked = attackLoc;
            return attackLoc;

            //cases:
            //isolated hit
            //isolated hit with single adjacent miss
            //isolated hit with double adjacent miss (opposite directions)
            //isolated hit with double same direction miss
            //nearby hit in one direction
            //nearby hit in two opposite directions
            //nearby hit in two same directions
            //misses in 3 directions
        }
    }

    private Location attackNearby(Location hitLoc)
    {
        System.out.println(hitLoc);
        if (!alreadyAttacked(hitLoc))
            return hitLoc;
        else
        {
            Location attackLoc = null;
            Condition c = condition(hitLoc);
            System.out.println(c.getType() + "; " + c.getDirection());
            int dir;
            switch (c.getType())
            {
                case 0:
                    while (attackLoc == null) {
                        dir = (int) (Math.random() * 4);
                        attackLoc = stepDirection(hitLoc, dir);
                    }
                    return attackLoc;
                case 1:
                case 3:
                    dir = (int) (Math.random() * 2);
                    if (dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 1); // one step in the direction clockwise of the miss
                    else if (dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 3); // one step in the direction counterclockwise of the miss
                    if (attackLoc == null && dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 3);
                    else if (attackLoc == null && dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 1);
                    return attackLoc;
                case 2:
                    dir = (int) (Math.random() * 2);
                    if (dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 2); // two steps in the direction clockwise of the miss
                    else if (dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 3); // one step in the direction counterclockwise of the miss
                    if (attackLoc == null && dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 3);
                    else if (attackLoc == null && dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 2);
                    return attackLoc;
                case 4:

                    attackLoc = stepDirection(hitLoc, c.getDirection() + 2);
                    System.out.println(attackLoc);

                    if (attackLoc == null || alreadyAttacked(attackLoc))
                    {
                        return attackNearby(stepDirection(hitLoc, c.getDirection()));
                    }
                    else
                        return attackLoc;
                case 5:
                    dir = (int) (Math.random() * 2);
                    if (dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 2); // two steps in the direction clockwise of the miss
                    else if (dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 3); // one step in the direction counterclockwise of the miss
                    if ((attackLoc == null || alreadyAttacked(attackLoc)) && dir == 0) attackLoc = stepDirection(hitLoc, c.getDirection() + 3);
                    else if ((attackLoc == null || alreadyAttacked(attackLoc)) && dir == 1) attackLoc = stepDirection(hitLoc, c.getDirection() + 2);

                    if (attackLoc == null || alreadyAttacked(attackLoc)) //still, after trying both ways
                    {
                        if (dir == 0) return attackNearby(stepDirection(hitLoc, c.getDirection()));
                        else if (dir == 1) return attackNearby(stepDirection(hitLoc, c.getDirection() + 1));
                    }
                    return attackLoc;
                case 6:
                    dir = (int) (Math.random() * 2);
                    if (dir == 0) attackNearby(stepDirection(hitLoc, c.getDirection()));
                    else if (dir == 1) attackNearby(stepDirection(hitLoc, c.getDirection()+2));
                case 7:
                    return stepDirection(hitLoc, c.getDirection());
            }
        }
        return null;
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
            return knowledge[loc.getRow()][loc.getCol()] != 0;
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
        if (inRange(loc))   return knowledge[loc.getRow()][loc.getCol()] == 1;
        if (!inRange(loc)) System.out.println("not in range!");
        return false;
    }

    private boolean isMiss(Location loc)
    {
        if (inRange(loc)) return knowledge[loc.getRow()][loc.getCol()] == -1;
        if (!inRange(loc)) System.out.println("not in range!");
        return false;
    }

    public void targetMissed()
    {
        knowledge[justAttacked.getRow()][justAttacked.getCol()] = -1;
    }

    public void targetHit(int shipSunk)
    {
        if (shipSunk == 0)
        {
            currentShipAttack = justAttacked;
        }
        else
        {
            currentShipAttack = null;
        }
        knowledge[justAttacked.getRow()][justAttacked.getCol()] = 1;
    }

    private Condition condition(Location loc)
    {
        //returns a number corresponding to the case the loc is in with respect to its neighbors
        Location up = new Location(loc.getRow()-1,loc.getCol());
        Location down = new Location(loc.getRow()+1,loc.getCol());
        Location left = new Location(loc.getRow(),loc.getCol()-1);
        Location right = new Location(loc.getRow(),loc.getCol()+1);
        int numMisses = 0;
        if (isMiss(up)) numMisses++; if (isMiss(down)) numMisses++;
        if (isMiss(left)) numMisses++; if (isMiss(right)) numMisses++;

        int numHits = 0;
        if (isHit(up)) numHits++; if (isHit(down)) numHits++;
        if (isHit(left)) numHits++; if (isHit(right)) numHits++;
        System.out.println("num hits: " + numHits);
        System.out.println("num misses: " + numMisses);

        if (numHits == 0 && numMisses == 0)
            return new Condition(0, 0); //isolated

        //problems to check: if multiple cases are true within each case, the first one will only register
        else if (numHits == 1)
        {
            int type = 4;
            int direction = -1;
            if (isHit(up)) direction = 0;
            else if (isHit(right)) direction = 1;
            else if (isHit(down)) direction = 2;
            else if (isHit(left)) direction = 3;
            return new Condition(type, direction);
        }
        else if (numHits >= 2)
        {
            int direction = -1;
            if (isHit(up) && isHit(right)) return new Condition(5, 0);
            else if (isHit(right) && isHit(down)) return new Condition(5, 1);
            else if (isHit(down) && isHit(left)) return new Condition(5, 2);
            else if (isHit(left) && isHit(up)) return new Condition(5, 3);
            else if (isHit(up) && isHit(down)) return new Condition(6, 0);
            else if (isHit(left) && isHit(right)) return new Condition(6, 1);
        }
        else if (numMisses == 1 && numHits == 0)
        {
            int type = 1;
            int direction = -1;
            if (isMiss(up)) direction = 0;
            else if (isMiss(right)) direction = 1;
            else if (isMiss(down)) direction = 2;
            else if (isMiss(left)) direction = 3;
            return new Condition(type, direction);
        }
        else if (numMisses == 2)
        {
            int type = 2;
            int direction = -1;
            if (isMiss(up) && isMiss(right)) direction = 0;
            else if (isMiss(right) && isMiss(down)) direction = 1;
            else if (isMiss(down) && isMiss(left)) direction = 2;
            else if (isMiss(left) && isMiss(up)) direction = 3;
            else if (isMiss(up) && isMiss(down)) { type = 3; direction = 0; }
            else if (isMiss(left) && isMiss(right)) { type = 3; direction = 1; }
            return new Condition(type, direction);
        }
        else if (numMisses > 2)
        {
            int type = 7;
            int direction = -1;
            if (!isMiss(up)) direction = 0;
            if (!isMiss(left)) direction = 3;
            if (!isMiss(down)) direction = 2;
            if (!isMiss(right)) direction = 1;
            return new Condition(type, direction);
        }


        return null;
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

class Condition
{
    int type;
    int direction;

    public Condition(int type, int direction)
    {
        this.type = type;
        this.direction = direction;
    }

    public Condition()
    {}

    public void setType(int newType)
    {
        this.type = newType;
    }

    public int getType()
    {
        return this.type;
    }

    public void setDirection(int newDirection)
    {
        this.direction = newDirection;
    }

    public int getDirection()
    {
        return this.direction;
    }
}
