// White.java
import java.awt.Graphics;
import java.awt.Color;

public class White extends Piece {  // 白のクラス

  // コンストラクタ
  public White(int x, int y) {
    super(x, y);
  }

  // ポーンを表示するメソッド
  public void  draw(Graphics gra) {
    gra.setColor(Color.black);
    gra.drawOval(110+50*x, 460-50*y, 30, 30);
  }
}
