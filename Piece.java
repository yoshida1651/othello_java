// Piece.java
import java.awt.Graphics;
import java.awt.Color;

public abstract class Piece {  // 駒のクラス
  int x;  // 駒のＸ位置
  int y;  // 駒のＹ位置

  // コンストラクタ
  // 第１引数xは駒のＸ位置、第２引数yは駒のＹ位置
  public Piece(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean isAt(int x, int y) {
    return ((this.x == x) && (this.y == y));
  }

  public abstract void draw(Graphics gra);  // 駒の表示

}
