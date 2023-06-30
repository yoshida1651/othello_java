// Game.java(ゲームのルールを作成)
import java.awt.Graphics;

import javax.swing.JLabel;

import java.awt.Color; 
import java.awt.event.*;
import javax.swing.*;

import java.awt.*; 

public class Game {       // ゲームのクラス

  private int mode = 0;  // ゲームのモード 0のとき黒、1のとき白を置くモード
  private boolean cpumode = false; //cpuとの対戦であるときtrue                       
  private int hint = 0; //0のとき通常、1のときヒント　コンボボックスで選択される
  private Brack[] myBracks = new Brack[64];  // 黒の参照の配列
  private White[] myWhites = new White[64];  // 白の参照の配列
  private Red[] myReds = new Red[32];
  private int numOfBracks = 0;             // 黒の数
  private int numOfWhites = 0;             // 白の数 
  private int numOfReds = 0;
  private int countMax = 0; //その位置でひっくり返せる最大の枚数
  private int count =0;
  private int cpuX = 0;
  private int cpuY = 0;

  
  //numOfWhites == 1のとき、myWhites[0]に白が存在し、myWhites[1]==nullである。
  //myBracks[numOfBracks] == myWhites[numOfWhites] == nullの状態を保つように↓
  //numOfBracksとnumOfWhitesを制御する。

  JLabel label;
  private GameFrame GF;

  // コンストラクタ
  public Game() {
    myBracks[numOfBracks] = new Brack(3, 3); //駒を中央に二つずつ置く
    numOfBracks++;
    myBracks[numOfBracks] = new Brack(4, 4);
    numOfBracks++;
    myWhites[numOfWhites] = new White(3, 4);
    numOfWhites++;
    myWhites[numOfWhites] = new White(4, 3);
    numOfWhites++;
  }

  // 開始時に呼び出されるメソッド
  public void start(Graphics gra,JLabel lb,GameFrame gf) {
    label = lb;
    GF = gf;
    draw(gra);
  }

  // マウスキーが押された時に呼び出されるメソッド
  // 第１引数xはマウスキーを押したマスのＸ位置
  // 第２引数yはマウスキーを押したマスのＹ位置
  // 第３引数graは表示のためのGraphicsオブジェクトの参照
  public void mousePressed(int x, int y, Graphics gra) {
    if (mode == 0) {
      if ( canPut(x,y)) { //もし引数の位置に駒を置けるなら
        allRedsDeleate();
        myBracks[numOfBracks] = new Brack(x, y);
        myBracks[numOfBracks].draw(gra);
        numOfBracks++;
        turnOver(x,y,gra); //ひっくり返せる駒をひっくり返す
        if(cpumode){ //cpuとの対戦であれば、cpuが駒をひっくり返す。modeは切り替えない。(mode==0)のまま
          cpuPut(gra);
        }else{ //cpuとの対戦でないなら、modeを切り替える
          mode = 1; //モード切り替え
        }
      }
    }else if(mode == 1){
      if (canPut(x,y)) {
        allRedsDeleate();
        myWhites[numOfWhites] = new White(x, y);
        myWhites[numOfWhites].draw(gra);
        numOfWhites++;
        turnOver(x,y,gra);
        mode = 0; //モード切り替え
      }
    }
    makeReds(); //赤い駒を作成
    GF.repaint(); //一度盤とすべての駒をクリアしたいので、draw(gra);ではない
  }

   // ゲーム（盤と駒）を表示するメソッド
   public void draw(Graphics gra) {
    int i;
    boardDraw(gra);                          // 盤を表示する
    for (i = 0; i < numOfBracks ; i++) {  // 全ての
      myBracks[i].draw(gra);                    // 黒を表示する
    }
    for (i = 0; i < numOfWhites ; i++) {  // 全ての
      myWhites[i].draw(gra);                    // 白を表示する
    }
    if(hint == 1){//ヒントが選択されている場合
      for(i = 0; i < numOfReds ; i++){ //全ての
        myReds[i].draw(gra);                // 赤を表示する
      }
    }
    
    if((numOfBracks + numOfWhites == 64) || end(gra) ){ //盤が駒で埋まっているまたは両者とも駒を置けないなら
      System.out.println("黒のコマ数" + numOfBracks + " : " + numOfWhites + "白のコマ数");
      if(numOfBracks > numOfWhites){
        System.out.println("黒の勝ち"); //ターミナル上で結果表示
        label.setText("黒の勝ち");   //フレーム上で結果表示
      }else if(numOfWhites > numOfBracks){
        System.out.println("白の勝ち");
        label.setText("白の勝ち");
      }else{
        System.out.println("引き分け");
        label.setText("引き分け");
      }
    }else{ //まだ続行可能なら
      if(mode == 0){
        if(cpumode){
          label.setText("あなた(黒)の番です");
        }else{
          label.setText("黒を置く番です");
        }
        }else{
        label.setText("白を置く番です");
    }
    }
  }  

  // 盤を表示するメソッド
  private void boardDraw(Graphics gra) {
    gra.setColor(Color.black);
    gra.drawRect(100, 100, 400, 400);
    for (int i = 1; i < 8; i++) {
      gra.drawLine(100, 100+50*i, 500, 100+50*i);
      gra.drawLine(100+50*i, 100, 100+50*i, 500);
    }
  }

  //引数で指定された位置に置けるかどうか
  private boolean canPut(int x,int y){
    boolean canput = false;
    if(!isPieceAt(x, y) && ( middleRightLine(x, y) || middleLeftLine(x,y) || upperMiddleLine(x,y) || lowerMiddleLine(x,y) || 
    upperRightLine(x,y) || upperLeftLine(x,y) || lowerRightLine(x,y) || lowerLeftLine(x,y))){
      canput = true;
    }
    return canput;
  }

  //引数で指定された場所に駒を置いた場合にひっくり返せる駒をひっくり返す。
  private void turnOver(int x,int y , Graphics gra){
    int i;
    int j;
    int index;
    if(middleRightLine(x, y)){
      i = x+1; //右に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, y)){ //そこに白い駒があるか
        index = getWhiteIndex(i, y); //x位置とy位置からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i++;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, y)){
          index = getBrackIndex(i, y);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          i++;
        }
      }
    } 

    if(middleLeftLine(x, y)){
      i = x-1; //左に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, y)){ //そこに白い駒があるか
        index = getWhiteIndex(i, y); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i--;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, y)){
          index = getBrackIndex(i, y);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          i--;
        }
      }
    } 

    if(upperMiddleLine(x, y)){
      j = y+1; //右に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(x, j)){ //そこに白い駒があるか
        index = getWhiteIndex(x, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        j++;
      }
      }else{ //白を置く場合
        while(isBrackAt(x, j)){
          index = getBrackIndex(x, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          j++;
        }
      }
    } 

    if(lowerMiddleLine(x, y)){
      j = y-1; //下に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(x, j)){ //そこに白い駒があるか
        index = getWhiteIndex(x, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        j--;
      }
      }else{ //白を置く場合
        while(isBrackAt(x, j)){
          index = getBrackIndex(x, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          j--;
        }
      }
    } 
    
    if(upperRightLine(x, y)){
      i = x+1; //右に一マス移動
      j = y+1; //上に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, j)){ //そこに白い駒があるか
        index = getWhiteIndex(i, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i++;
        j++;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, j)){
          index = getBrackIndex(i, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          i++;
          j++;
        }
      }
    } 

    if(upperLeftLine(x, y)){
      i = x-1; //左に一マス移動
      j = y+1; //上に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, j)){ //そこに白い駒があるか
        index = getWhiteIndex(i, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i--;
        j++;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, j)){
          index = getBrackIndex(i, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          i--;
          j++;
        }
      }
    } 

    if(lowerRightLine(x, y)){
      i = x+1; //右に一マス移動
      j = y-1; //下に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, j)){ //そこに白い駒があるか
        index = getWhiteIndex(i, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i++;
        j--;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, j)){
          index = getBrackIndex(i, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          i++;
          j--;
        }
      }
    } 

    if(lowerLeftLine(x, y)){
      i = x-1; //左に一マス移動
      j = y-1; //下に一マス移動
      if(mode == 0){ //黒を置く場合
      while(isWhiteAt(i, j)){ //そこに白い駒があるか
        index = getWhiteIndex(i, j); //x座標とy座標からインデックスをget
        changeWhite(index); //myWhites[index]の位置に新しく黒い駒を作成
        myBracks[numOfBracks-1].draw(gra); //作成した黒い駒を表示
        i--;
        j--;
      }
      }else{ //白を置く場合
        while(isBrackAt(i, j)){
          index = getBrackIndex(i, j);
          changeBrack(index); //myBracks[index]の位置に新しく白い駒を作成
          myWhites[numOfWhites-1].draw(gra); //作成した白い駒を表示
          i--;
          j--;
        }
      }
    } 
}

  private int getBrackIndex(int x,int y){ //isBrackAtと似ている
    boolean found = false;
    int i = 0;
    while ((i < numOfBracks) && (!found)) {
      found = myBracks[i].isAt(x,y) ;//(2)
      i++;
    }
    return i-1;
  }

  private int getWhiteIndex(int x,int y){
    boolean found = false;
    int i = 0;
    while ((i < numOfWhites) && (!found)) {
      found = myWhites[i].isAt(x,y) ;//(2)
      i++;
    }
    return i-1;
  }


   // 引数で指定された位置に駒が存在するかを調べるメソッド
   private boolean isPieceAt(int x, int y) {
    boolean found = false;
    found = isWhiteAt(x,y);
    if(isWhiteAt(x, y) || isBrackAt(x, y)){
      found = true;
    }
    return found; //白か黒どちらかの駒があればtrueを返す
  }

  //引数で指定された位置に黒の駒があるかどうか
  //黒の駒がある場合true、白の駒もしくは駒がない場合はfalseを返す。
  private boolean isBrackAt(int x,int y){
    boolean found = false;
    int i = 0;
    while ((i < numOfBracks) && (!found)) {
      found = myBracks[i].isAt(x,y) ;//(2)
      i++;
    }
    return found;
  }

  //引数で指定された位置に白の駒があるかどうか
  //白の駒がある場合true、黒の駒もしくは駒がない場合はfalseを返す。
  private boolean isWhiteAt(int x,int y){
    boolean found = false;
    int i = 0;
    while ((i < numOfWhites) && (!found)) {
      found = myWhites[i].isAt(x,y) ;//(2)
      i++;
    }
    return found;
  }

  //指定された位置の右中央のラインに相手のピースがあり、そのピース以降に自分のピースがあるかどうか
  private boolean middleRightLine(int x,int y){
  boolean canputright = false; //指定された位置に駒がおけるかどうか(返り値)
  boolean next = false; //相手のpieceが右隣にあるかどうか
  int tentativeCount = 0; //仮のcount
  if(mode == 0){ //黒を置く場合
    while(!canputright){
      x++; //一マス分右へ
      next = isWhiteAt(x, y); //白かどうか
      if(next){ //白だった場合
        canputright = isBrackAt(x+1, y); //さらに右に黒の駒があるかどうか
        tentativeCount++;
      }else{ //駒がないもしくは黒の駒の場合
        break;
      }
    }
  }else{ //白を置く場合
    while(!canputright){
      x++; 
      next = isBrackAt(x, y);
      if(next){ //右に相手の駒がある場合
        canputright = isWhiteAt(x+1, y);
        tentativeCount++;
      }else{
        break;
      }
    }
  }
  if(canputright){//引数で指定した場所に駒が置けるなら
    count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
  }
    return canputright;
  }

  //引数(指定された位置の左中央のラインに相手のピースがあり、そのピース以降に自分のピースがあるかどうか
  private boolean middleLeftLine(int x,int y){
    boolean canputleft = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canputleft){
        x--; //一マス分左へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canputleft = isBrackAt(x-1, y);  //そのまた左が黒かどうか
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canputleft){
        x--; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canputleft = isWhiteAt(x-1, y);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canputleft){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canputleft;
  }

  //引数(指定された位置の上中央のラインに相手のピースがあり、そのピース以降に自分のピースがあるかどうか
  private boolean upperMiddleLine(int x,int y){
    boolean canputupper = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canputupper){
        y++; //一マス分上へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canputupper = isBrackAt(x, y+1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canputupper){
        y++; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canputupper = isWhiteAt(x, y+1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canputupper){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canputupper;
  }

  //引数(指定された位置の下中央のラインに相手のピースがあり、そのピース以降に自分のピースがあるかどうか
  private boolean lowerMiddleLine(int x,int y){
    boolean canputlower = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canputlower){
        y--; //一マス分下へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canputlower = isBrackAt(x, y-1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canputlower){
        y--; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canputlower = isWhiteAt(x, y-1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canputlower){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canputlower;
  }

  private boolean upperRightLine(int x,int y){
    boolean canput = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canput){
        x++; //一マス分右へ
        y++; //一マス分上へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canput = isBrackAt(x+1, y+1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canput){
        x++;
        y++; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canput = isWhiteAt(x+1, y+1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canput){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canput;
  }

  private boolean upperLeftLine(int x,int y){
    boolean canput = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canput){
        x--; //一マス分左へ
        y++; //一マス分上へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canput = isBrackAt(x-1, y+1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canput){
        x--;
        y++; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canput = isWhiteAt(x-1, y+1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canput){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canput;
  }

  private boolean lowerRightLine(int x,int y){
    boolean canput = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canput){
        x++; //一マス分右へ
        y--; //一マス分下へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canput = isBrackAt(x+1, y-1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canput){
        x++;
        y--; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canput = isWhiteAt(x+1, y-1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canput){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canput;
  }

  
  private boolean lowerLeftLine(int x,int y){
    boolean canput = false; //指定された位置に駒がおけるかどうか(返り値)
    boolean next = false; //相手のpieceが右隣にあるかどうか
    int tentativeCount = 0; //仮のcount

    if(mode == 0){ //黒を置く場合
      while(!canput){
        x--; //一マス分左へ
        y--; //一マス分下へ
        next = isWhiteAt(x, y); //白かどうか
        if(next){ //白だった場合
          canput = isBrackAt(x-1, y-1); 
          tentativeCount++;
        }else{ //駒がないもしくは黒の駒の場合
          break;
        }
      }
    }else{ //白を置く場合
      while(!canput){
        x--;
        y--; 
        next = isBrackAt(x, y);
        if(next){ //右に相手の駒がある場合
          canput = isWhiteAt(x-1, y-1);
          tentativeCount++;
        }else{
          break;
        }
      }
    }
    if(canput){//引数で指定した場所に駒が置けるなら
      count += tentativeCount;  //このラインでひっくり返す駒の数をcountに足す
    }
      return canput;
  }

 // 引数番目の黒の駒の場所に白の駒を作成してから、引数番目の黒の駒を削除するメソッド
  private void changeBrack(int n) {
    int i;
    myWhites[numOfWhites] = new White(myBracks[n].getX(),myBracks[n].getY()); //白の配列の最後尾に白を作成
    numOfWhites++;
    for (i = n + 1; i < numOfBracks; i++) {
       myBracks[i-1] = myBracks[i];
    }
    myBracks[i-1] = null;
    numOfBracks--;
  }

  private void changeWhite(int n) {
    int i;
    myBracks[numOfBracks] = new Brack(myWhites[n].getX(),myWhites[n].getY());
    numOfBracks++;
    for (i = n + 1; i < numOfWhites; i++) {
       myWhites[i-1] = myWhites[i];
    }
    myWhites[i-1] = null;
    numOfWhites--;
  }

  private void makeReds(){
    int x;
    int y;
    for(x = 0; x < 8; x++){ //全部の位置(64箇所)を調べる
      for(y = 0; y < 8; y++)
      if(canPut(x, y)){ //その位置で置けるとこなら、
        myReds[numOfReds] = new Red(x,y);
        numOfReds++;
      }
    }
  }

  //盤上にあるすべての赤を削除
  private void allRedsDeleate(){
    int i = 0;
    while(i < numOfReds){ // myReds[numOfReds]は元からnull
      myReds[i] = null;
      i++;
    }
    numOfReds = 0;
  }

  //他のクラスから呼び出されるのでprivateではなくpublic
  //modeをチェンジ
  public void pass(Graphics gra){
    if(!cpumode){//cpumodeでないなら
      if(mode == 0){
        mode = 1;
      }else{
        mode = 0;
      }
      if(hint == 1){ //ヒントが選択されていたら
        allRedsDeleate(); //元あった赤を削除
        makeReds(); //モード切り替え後の赤を生成
      }
    }else{//cpumodeなら
      cpuPut(gra);
    }
  }

  //他のクラスから呼び出されるのでprivateではなくpublic
  public void changeHint(int i){ //0のとき通常、1のときヒント
    hint = i;
    if(hint == 1){ //ヒントを選択後すぐに赤の駒を置かせる
      allRedsDeleate(); //元あった赤を削除
      makeReds(); //赤を生成
    }else{ //通常を選択後すぐに赤の駒を削除
      allRedsDeleate();
    }
  }
  //他のクラスから呼び出されるのでprivateではなくpublic
  public void changeCpu(int i){ //0のとき通常、1のときヒント
    if(i == 0){
      cpumode = false;
    }else{
      cpumode = true;
    }
  }

  //両者とも駒を置けない場合、trueを返す
  private boolean end(Graphics gra){
    boolean end = false;
    if(numOfReds == 0){ //現在のモードでの赤の駒が存在せず
      pass(gra); //モード切り替え
      makeReds(); //赤の駒生成
      if(numOfReds == 0){ //モードを切り替えても赤の駒が存在しないなら
        end = true;
      }
      pass(gra); //モードを元に戻す
      makeReds(); //赤の駒生成
    }
    return end;
  }
  
  private void cpuPut(Graphics gra){
    mode = 1; //cpuが置く駒は白のためmode切り替え
    int x;
    int y;
    for(x = 0; x < 8; x++){ //全部の位置(64箇所)を調べる
      for(y = 0; y < 8; y++)
      if(canPut(x, y)){ //その位置で置けるとこなら、
        if(countMax<count){
          countMax = count;
          cpuX = x;
          cpuY = y;
        }
      }
    }
    myWhites[numOfWhites] = new White(cpuX, cpuY);
    myWhites[numOfWhites].draw(gra);
    numOfWhites++;
    turnOver(cpuX,cpuY,gra);
    mode = 0; //プレイヤーが置く駒は黒のためmode切り替え
  }
}