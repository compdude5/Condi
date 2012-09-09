import java.awt.*;

/** Contains information about the player */
public class Player extends Unit {
  int xp;
  
  Player(int row, int col, Map map, Info info) {
    super(0, row, col, map, info);
  }
  
  /** Player is super fast for testing */
  public int getSpeed() {
    return 1000;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getMaxHealth() {
    return 500 + 50 * level;
  }
  
  /** Returns the character that should be used to represent the player */
  public char getChar() {
    return '@';
  }
  
  /** Returns the character colour that should be used to represent the player */
  public CharCol getCharCol() {
    return new CharCol(Color.RED);
  }
  
  public void giveXp(int xp) {
    this.xp += xp;
  }
  public int getXp() {
    return xp;
  }
  public void setXp(int xp) {
    this.xp = xp;
  }
}