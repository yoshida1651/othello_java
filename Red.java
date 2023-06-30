// Red.java
import java.awt.Graphics;
import java.awt.Color;

public class Red extends Piece {  // 赤のクラス

  // コンストラクタ
  public Red(int x, int y) {
    super(x, y);
  }

  // ポーンを表示するメソッド
  public void  draw(Graphics gra) {
    gra.setColor(Color.red);
    gra.fillOval(115+50*x, 465-50*y, 20, 20);
  }
}