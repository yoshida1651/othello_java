// GPFrame.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// ゲーム用フレーム（ウィンドウ）
public class GameFrame extends JFrame implements ActionListener, MouseListener{

static  String[] str = {"Pass", "Exit" /* ,"適用"*/};  // ボタン用の文字列

  JComboBox cb;     // メニューやボタン用のコンボボックス(使ってない？)

  JButton[] bt = new JButton[str.length];  // Pass・Exitボタン

  static  String[] strHint = {"No", "Yes"};   // ヒント用の文字列
  static  String[] strCpu = {"No", "Yes"};   // cpu用の文字列
  static  String[] labelStr = {"Hint", "Cpu"};
  JComboBox<String> cb1, cb2, cb3;     // メニューやボタン用のコンボボックス
  //cb1がヒント、cb2がcpuのコンボボックス


  Graphics gra;          // Graphicsオブジェクト

  int xPressed = 0;      // マウスキーを押したＸ座標
  int yPressed = 0;      // マウスキーを押したＹ座標
  int xPosition = 0;     // 指定されたＸ方向の位置
  int yPosition = 0;     // 指定されたＹ方向の位置

  Game game;             // ゲーム

   // メインプログラム
   public static void main(String[] args) {
    Game g = new Game();  // ゲームのインスタンス生成
    new GameFrame("Game", g);  // ゲーム用フレームのインスタンス生成
  }

  // コンストラクタ、第１引数：タイトル、第２引数：ペイントツールの参照
  public GameFrame(String title, Game gp) {
    super(title);                    // スーパクラスのコンストラクタ呼び出し
    setSize(600, 600);               // フレームのサイズを横600,縦600とする
    setBackground(Color.white);      // 背景色を白とする
    Container cp = getContentPane();                // コンテナ設定
    cp.setBackground(getBackground());              // コンテナの背景設定
    cp.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左側から並べるレイアウト

    // ボタン
    JPanel pn = new JPanel();                       // パネル生成
    pn.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左から並べるレイアウト
    for (int i=0; i<str.length; i++) {   // ボタン数分繰り返し
      bt[i] = new JButton(str[i]);       // ボタン生成
      bt[i].addActionListener(this);     // ボタンにリスナ（アクション監視)付加
      pn.add(bt[i]);                     // パネルにボタン追加
    }

    //ラベル（自作）
    JLabel lb = new JLabel(); //ラベル生成
    lb.setText("どちらの番なのかを表示"); //ラベルにテキストをセット
    Font myFont = new Font("フォント", Font.BOLD ,12); //フォント生成
    lb.setFont(myFont); //ラベルにフォントをセット？

    /* 
    //コンボボックス（自作）
    String[] item = {"通常","ヒント"};
    cb = new JComboBox<>(item);
    cb.setPreferredSize(new Dimension(80,17));
    cb.setSelectedIndex(0); //コンボボックスのデフォルトを通常に？
    */

    createMenus(cp);
    
    cp.add(lb); // コンテナにラベルを追加？
    cp.add(pn); // コンテナにパネル追加
    //cp.add(cb); // コンテナにコンボボックスを追加
    

    addMouseListener(this);  // マウスリスナー（アクション監視）付加
    setVisible(true);        // 見えるようにする
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //デフォルトはHIDE_ON_CLOSEでプログラムは終了しない
    gra = getGraphics();     // Graphicsオブジェクト記憶
    game = gp;               // ゲームの参照記憶
    game.start(gra,lb,this);         // ゲームの開始
  }
  // メニューとボタンを作成する
  void createMenus(Container cp) {
    createMenuHint(cp);
    createMenuCpu(cp);
  }

  // アクションが発生した（メニューが選択された、ボタンが押された）場合の処理
  public void actionPerformed(ActionEvent evt) {

    String selStr;  // 選択された文字列記憶用変数

    if (evt.getSource() == bt[0]) {   // Passボタンが押された場合は
    game.pass(gra);       // ゲームのメソッド pass() 呼び出し
      repaint();          // 画面をクリアして再描画
    }

    if (evt.getSource() == bt[1]) {   // Exitボタンが押された場合は
      dispose();       // フレームを消す
      System.exit(0);  // 終了
    }

    /*
    if(evt.getSource() == bt[2]){
      if(cb.getSelectedIndex() == 1){ //コンボボックスでヒントが選択されていたら
        game.changeItem(1);
      }else{                          //コンボボックスで通常が選択されていたら
        game.changeItem(0);
      }
      repaint();          // 画面をクリアして再描画
    }
    */
    if (evt.getSource() == cb1) {   // ヒントのメニューが選択された場合は
      hintOperation();
    }
    if (evt.getSource() == cb2) {   // cpuのメニューが選択された場合は
      cpuOperation();
    }
  }
  


  // マウスキーが押された場合の処理
  public void mousePressed(MouseEvent evt) {
    xPressed = evt.getX();  // イベントが発生したところのＸ座標を記憶
    yPressed = evt.getY();  // イベントが発生したところのＹ座標を記憶
    if ((xPressed > 100) && (xPressed < 500) &&
        (yPressed > 100) && (yPressed < 500)) {
      int xPosition = (xPressed - 100) / 50;
      int yPosition = (500 - yPressed) / 50;
      game.mousePressed(xPosition, yPosition, gra);
                    // ゲームのメソッド mousePressed() 呼び出し
//      repaint();
    }
  }

  // フレームを復元（再描画）する処理
  public void paint(Graphics gra) {
    super.paint(gra);    // スーパークラス（JFrame）の paint() 呼び出し
    if (game != null) {   // ゲームがあれば
      game.draw(gra);     // ゲームの表示
    }
  }

  //java.awtパッケージにあるComponentクラスにrepaint()メソッドは属している
  //Componentクラスのupdate()メソッドを実行し、背景色でウィンドウをクリアしてからpaint()メソッドを呼び出す
  public void repaint(){
    super.repaint();
  }


    // ヒントのメニュー
    void createMenuHint(Container cp) {
      JPanel pn1 = new JPanel();                       // パネル生成
      pn1.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左から並べるレイアウト
      JLabel lb1 = new JLabel(labelStr[0]);  // "Filled"ラベルの生成
      lb1.setForeground(Color.BLACK);        // ラベルの文字色を黒とする
      pn1.add(lb1);                          // パネルにラベルを付加
  
      cb1 = new JComboBox<String>();                // コンボボックス生成
      cb1.setEditable(false);               // コンボボックスの編集は不可とする
      for (int i=0; i<strHint.length; i++) {   // 塗りつぶしメニュー項目数分繰り返し
        cb1.addItem(strHint[i]);               // 塗りつぶしメニュー項目追加
      }
  
      cb1.addActionListener(this);  // コンボボックスにリスナ(アクション監視)付加
      pn1.add(cb1);                 // パネルにコンボボックスを付加
      cp.add(pn1);                  // コンテナにパネル追加
    }

// cpuのメニュー
void createMenuCpu(Container cp) {
  JPanel pn2 = new JPanel();                       // パネル生成
  pn2.setLayout(new FlowLayout(FlowLayout.LEFT));  // 左から並べるレイアウト
  JLabel lb2 = new JLabel(labelStr[1]);  // "Filled"ラベルの生成
  lb2.setForeground(Color.BLACK);        // ラベルの文字色を黒とする
  pn2.add(lb2);                          // パネルにラベルを付加

  cb2 = new JComboBox<String>();                // コンボボックス生成
  cb2.setEditable(false);               // コンボボックスの編集は不可とする
  for (int i=0; i<strHint.length; i++) {   // 塗りつぶしメニュー項目数分繰り返し
    cb2.addItem(strHint[i]);               // 塗りつぶしメニュー項目追加
  }

  cb2.addActionListener(this);  // コンボボックスにリスナ(アクション監視)付加
  pn2.add(cb2);                 // パネルにコンボボックスを付加
  cp.add(pn2);                  // コンテナにパネル追加
}

  //ヒントのメニューが選択された場合に呼び出されるメソッド
  void hintOperation() {
    String selStr;  // 選択された文字列記憶用変数
    
    repaint();  // 再描画（プルダウンメニューで図形が消えることがあるので)
    selStr = (String)cb1.getSelectedItem();  // 選択された項目の文字列を得る
    if (selStr == strHint[0]) {  // 選択された文字列が"No"であれば
    game.changeHint(0);
    } else if (selStr == strHint[1]) {  // 選択された文字列が"Yes"であれば
    game.changeHint(1);
    }
  }

    //cpuのメニューが選択された場合に呼び出されるメソッド
    void cpuOperation(){
      String selStr;  // 選択された文字列記憶用変数
      
      repaint();  // 再描画（プルダウンメニューで図形が消えることがあるので)
      selStr = (String)cb2.getSelectedItem();  // 選択された項目の文字列を得る
      if (selStr == strCpu[0]) {  // 選択された文字列が"No"であれば
      game.changeCpu(0);
      } else if (selStr == strCpu[1]) {  // 選択された文字列が"Yes"であれば
      game.changeCpu(1);
      }
    }

  // マウスキーが離された場合の処理
  public void mouseReleased(MouseEvent evt) {
  }

  // マウスキーがクリックされた場合の処理
  public void mouseClicked(MouseEvent evt) {
  }

  // マウスキーが領域内に入った場合の処理
  public void mouseEntered(MouseEvent evt) {
  }

  // マウスキーが領域外に出た場合の処理
  public void mouseExited(MouseEvent evt) {
  }

}
