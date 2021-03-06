import java.awt.*;
import java.util.*;

/** Contains information about a Monster */
public class Monster extends Unit {
  String type;
  
  int playerRow;
  int playerCol;
  
  Monster(int level, int row, int col, Map map, Info info) {
    super(level, row, col, map, info);
    playerRow = -1;
    playerCol = -1;
    //Choose a monster of the correct level
    Vector<String> possible = info.listOf("monster");
    Vector<String> goodones = new Vector<String>();
    for(String m : possible) {
      int lolev = Integer.parseInt(info.stats.get(m).get("lolev"));
      int hilev = Integer.parseInt(info.stats.get(m).get("hilev"));
      if(lolev <= level && level <= hilev)
        goodones.add(m);
    }
    type = goodones.get(Game.rand(0, goodones.size()));
    //Now choose a weapon and some armor for this guy
    inv.addItem(new Item(info, "weapon", info.tags.get(type)));
    //Add some armor
    for(int i = 0; i < 4; i++) {
      Item armor = new Item(info, "armor", info.tags.get(type));
      if(!inv.slotUse(armor.getSlot()))
        inv.addItem(armor);
    }
  }
  
  //The monster's turn has come, it should perform an action (like moving or attacking)
  public void takeTurn() {    
    //Update the location of the player if we can see it
    if(getMap().sight(getRow(), getCol(), getMap().getPlayer().getRow(), getMap().getPlayer().getCol())) {
      playerRow = getMap().getPlayer().getRow();
      playerCol = getMap().getPlayer().getCol();
      //Check whether in range for an attack
      if(Math.max(Math.abs(playerRow - getRow()), Math.abs(playerCol - getCol())) <= getRange()) {
        attack(getMap().getPlayer());
        return;
      }
    }
    
    //Move towards the player or don't move at all
    if(playerRow != -1) {
      int rowChange = 0;
      int colChange = 0;
      if(playerRow > getRow()) rowChange = 1;
      if(playerRow < getRow()) rowChange = -1;
      if(playerCol > getCol()) colChange = 1;
      if(playerCol < getCol()) colChange = -1;
      int oldRow = getRow();
      int oldCol = getCol();
      move(rowChange, colChange);
      if(getRow() == oldRow && getCol() == oldCol) {
        move(rowChange, 0);
        if(getRow() == oldRow && getCol() == oldCol) {
          move(0, colChange);
          if(getRow() == oldRow && getCol() == oldCol) {
            setWaiting(true);
          }
        }
      }
    } else {
      setWaiting(true);
    }
  }
  
  public String getName() {
    return type;
  }
  
  /** Returns the character that should be used to represent the player */
  public char getChar() {
    return info.stats.get(type).get("disp").charAt(0);
  }
  
  /** Returns the character colour that should be used to represent the player */
  public CharCol getCharCol() {
    String c = info.stats.get(type).get("disp").substring(1);
    Color cc = Color.WHITE;
    if(c.equals("blue"))
      cc = Color.BLUE;
    else if(c.equals("cyan"))
      cc = Color.CYAN;
    else if(c.equals("green"))
      cc = Color.GREEN;
    else if(c.equals("magenta"))
      cc = Color.MAGENTA;
    else if(c.equals("orange"))
      cc = Color.ORANGE;
    else if(c.equals("pink"))
      cc = Color.PINK;
    else if(c.equals("red"))
      cc = Color.RED;
    else if(c.equals("white"))
      cc = Color.WHITE;
    else if(c.equals("yellow"))
      cc = Color.YELLOW;
    
    return new CharCol(cc);
  }
}

class Pos {
  int row;
  int col;
  Pos(int row, int col) {
    this.row = row;
    this.col = col;
  }
}