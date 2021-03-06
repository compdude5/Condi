import java.awt.*;

/** Units are defined as anything with health and combat stats, speed, etc.
  * Right now, it's just Monsters and the Player, but it could include traps of some sort
  * (which would be like immobile monsters)
  */
public class Unit extends TimeUser {
  int health;
  int level;
  int row;
  int col;
  
  Inventory inv;
  
  Info info;
  
  Effect ailment;
  
  //Reference to the map this unit is on
  Map map;
  
  Unit(int level, int row, int col, Map map, Info info) {
    this.level = level;
    this.row = row;
    this.col = col;
    this.map = map;
    this.info = info;
    
    inv = new Inventory();
    
    health = getMaxHealth();
  }
  
  public int getRow() {
    return row;
  }
  public void setRow(int row) {
    this.row = row;
  }
  
  public int getCol() {
    return col;
  }
  public void setCol(int col) {
    this.col = col;
  }
  
  public int getLevel() {
    return level;
  }
  public void setLevel(int level) {
    this.level = level;
  }
  
  public boolean isDead() {
    return getHealth() <= 0; 
  }
  
  public void damage(int amt) {
    health -= amt;
  }
  
  public void heal(int amt) {
    health += amt;
    if(health > getMaxHealth())
      health = getMaxHealth();
  }
  
  /** This doesn't need to account for items or bonuses */
  public int getHealth() {
    return health;
  }
  
  /** This still needs to account for items and other bonuses (or not) */
  public int getMaxHealth() {
    return 100 + 30 * level;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getAttack() {
    int attack = 5 * level + inv.getAttack();
    if(ailment != null && ailment.type.equals("confuse"))
      attack -= ailment.power;
    if(ailment != null && ailment.type.equals("bleed"))
      attack -= 20;
    return attack;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getDefense() {
    int defense = 20 + 5 * level + inv.getDefense();
    if(ailment != null && ailment.type.equals("daze"))
      defense -= ailment.power;
    return defense;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getSpeed() {
    int speed = 1000 + inv.getSpeed() + inv.getSpeedMassDebuff();
    if(ailment != null && ailment.type.equals("slow"))
      speed += ailment.power;
    return speed;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getDamage() {
    int damage = (20 + inv.getDamage()) * (level + 1);
    if(ailment != null && ailment.type.equals("weaken"))
      damage -= ailment.power;
    return damage;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getAttackSpeed() {
    return 800 + inv.getASpeed();
  }
  
  /** This still needs to account for items and other bonuses */
  public int getRange() {
    return Math.max(1, inv.getRange());
  }
  
  /** Returns true if the unit is visible from this unit */
  public boolean isVisible(Unit u) {
    return map.sight(getRow(), getCol(), u.getRow(), u.getCol());
  }
  
  /** Returns true if the unit is in range */
  public boolean inRange(Unit u) {
    return Math.abs(getRow() - u.getRow()) <= getRange() && Math.abs(getCol() - u.getCol()) <= getRange();
  }
  
  /** Returns true if attack hits, false if it doesn't or attack is out of range */
  public boolean attack(Unit u) {
    //ensure unit is in range and is visible
    if(inRange(u) && isVisible(u)) {
      addTime(getAttackSpeed());
      if(getAttack() + Game.rand(0, 100) >= u.getDefense()) { //attack hits
        u.damage(Game.rand((int)Math.round(0.75 * getDamage()), 1 + (int)Math.round(1.25 * getDamage())));
        //Check for effects
        Effect e = getInv().getEffect();
        if(e != null) {
          map.addEffect(e, u);
        }
        if(this instanceof Player) {
          info.g.postMessage("You hit the " + ((Monster)u).type + " with your " + (getInv().slotUse("weapon") ? getInv().getSlot("weapon") : "fist") + ".", new CharCol(Color.GREEN));
        }
        if(u instanceof Player) {
          info.g.postMessage("You were hit by the " + ((Monster)this).type + ".", new CharCol(Color.RED));
        }
        return true;
      } else {
        if(this instanceof Player) {
          info.g.postMessage("You miss the " + ((Monster)u).type + ".", new CharCol(Color.RED));
        }
        if(u instanceof Player) {
          info.g.postMessage("The " + ((Monster)this).type + " missed you.", new CharCol(Color.GREEN));
        }
      }
    }
    return false;
  }
  
  /** Moves to target relative square (doesn't move if there is an obstacle or too far away) (example: move(-1, 1)) 
    * To simplify things, moving onto a square with a unit attacks the unit instead
    */
  public void move(int r, int c) {
    if(ailment != null && ailment.type.equals("snare")) {
      setWaiting(true);
    } else {
      if(Math.abs(r) <= 1 && Math.abs(c) <= 1) {
        if(map.getTile(getRow() + r, getCol() + c).isWalkable()) {
          Unit habitGuy = map.getLocationUnit(getRow() + r, getCol() + c);
          if(habitGuy == null) {
            setRow(getRow() + r);
            setCol(getCol() + c);
            int distGone = Math.abs(r) + Math.abs(c);
            if(distGone == 1)
              addTime(getSpeed());
            if(distGone == 2)
              addTime((int)Math.round(getSpeed() * 1.41));
          } else {
            //Attack the habitGuy!
            attack(habitGuy);
          }
        }
      }
    }
  }
  
  /** Returns the character that should be used to represent this unit */
  public char getChar() {
    return '?'; //should be overridden in Monster and Player
  }
  
  /** Returns the character colour that should be used to represent this unit */
  public CharCol getCharCol() {
    return new CharCol(Color.CYAN);
  }
  
  /** Returns the map */
  public Map getMap() {
    return map;
  }
  
  /** Teleports the player to another map */
  public void changeMap(Map map) {
    this.map = map;
  }
  
  /** Returns the inventory */
  public Inventory getInv() {
    return inv;
  }
}