package battleship;

public class Location implements Comparable<Location>
{
  private int row;
  private int col;

  public Location(int row, int col)
  {
    this.row = row;
    this.col = col;
  }

  public int getRow()
  {
    return row;
  }

  public int getCol()
  {
    return col;
  }

  public boolean equals(Object obj)
  {
    Location other = (Location)obj;
    return row == other.getRow() && col == other.getCol();
  }

  // allows Location to be used in TreeSet or TreeMap
  public int compareTo(Location other)
  {
    if (row == other.getRow())
      return col - other.getCol();
    else
      return row - other.getRow();
  }

  // allows Location to be used in HashSet or HashMap
  public int hashCode()
  {
    return row * 10 + col;
  }

  public String toString()
  {
    return ("(" + this.getRow() + ", " + this.getCol() + ")");
  }
}