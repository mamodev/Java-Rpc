package server.data;

import lib.document.IUniqueIdentifier;

public class User implements IUniqueIdentifier {
  
  private String username;
  private String password;
  private int level = 0;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getId() {
    return this.username;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  } 

  public void incrementLevel() {
    if(this.level < 4)
    this.level++;
  }

  public String getLevelName () {
    switch (this.level) {
      case 0:
        return "Recensore";
      case 1:
        return "Recensore esperto";
      case 2:
        return "Contributore";
      case 3:
        return "Contributore esperto";
      case 4:
        return "Contributore Super";
      default:
        return "Contatta Hotelier per premio in denaro";
    }
  }

  public String toString() {
    String str = "";

    str += "username: " + this.username + "\n";
    str+= "------------------------------------\n";
    str += "\n";

    str += "[";
    for (int i = 0; i < this.level; i++) {
      str += "*";
    }
    str += "] " + this.getLevelName() + "\n";
    
    return str;
  }
}
