import java.util.*;
import java.io.*;
enum PieceType{
    KNIGHT,
    BISHOP,
    PAWN,
    QUEEN,
    KING,
    ROOK
}
class Piece {
    PieceType type;
    boolean isWhite;
    String code = "";
    int moved;
    int r;
    int c;
    Piece(PieceType t, boolean isWhite, int r, int c) {
        type = t;
        this.isWhite = isWhite;
        switch(t){
            case KNIGHT:
                code = "N";
                break;
            case BISHOP:
                code = "B";
                break;
            case QUEEN:
                code = "Q";
                break;
            case ROOK:
                code = "R";
                break;
            case KING:
                code = "K";
                break;
            case PAWN:
                code = "P";
                break;
        }
        if(!isWhite) code = code.toLowerCase();
        moved = 0;
        this.r = r;
        this.c = c;

    }
    public String toString(){
        switch(type){
            case KNIGHT:
                code = "N";
                break;
            case BISHOP:
                code = "B";
                break;
            case QUEEN:
                code = "Q";
                break;
            case ROOK:
                code = "R";
                break;
            case KING:
                code = "K";
                break;
            case PAWN:
                code = "P";
                break;
        }
        if(!isWhite) code = code.toLowerCase();
        return code;
    }
    public boolean isSameColor(Piece p){
        if(p == null) return false;
        return isWhite == p.isWhite;
    }

}
class Game {
    Piece[][] arr = new Piece[8][8];
    boolean whiteToMove = true;

    boolean whiteCheck,blackCheck;
    boolean done;
    boolean checkmate;
    boolean stalemate;
    String moveStringWhite;
    String moveStringBlack;
    List<String> legalMoves;
    List<Piece> pieces;
    List<String> algebraicMoves;
    boolean promote;
    int moves;
    Stack<Piece> removed;
    Map<String,Integer> scoreDevRes = new HashMap<String,Integer>();

    public int points(Piece p){
        if(p == null) return 0;
        switch(p.type){
            case PAWN:
                return 1;
            case KNIGHT:
            case BISHOP:
                return 3;
            case ROOK:
                return 5;
            case QUEEN:
                return 9;

        }
        return 0;
    }
    public int scoreBoard(){
        int total = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                total += points(arr[i][j]);
            }
        }
        return total;
    }
    public int scoreDev(String move, int K){
        String[] m1 = move.split(" ");
        int r1 = Integer.parseInt(m1[0]);
        int c1 = Integer.parseInt(m1[1]);
        int r2 = Integer.parseInt(m1[2]);
        int c2 = Integer.parseInt(m1[3]);
        Piece piece1 = arr[r1][c1];
        Piece piece2 = arr[r2][c2];
        boolean capture = arr[r2][c2] != null;
        List<String> l = new ArrayList<String>();
        for(String thing : legalMoves) l.add(thing);
        move(r1,c1,r2,c2,false);

        if(K == 0){
            int val = scoreBoard();
            unmove(r1,c1,r2,c2,capture);
            legalMoves = l;
            return val;
        }
        int max = Integer.MIN_VALUE;
        for(String legalMove : legalMoves){
            if(K >= 1){
                int res = scoreDev(legalMove,K - 1);
                if(res > max){
                    max = res;
                }
            }

        }
        unmove(r1,c1,r2,c2,capture);
        legalMoves = l;
        return max;
    }
    public int score(String move, boolean show){
        int result = 0;
        String[] m1 = move.split(" ");
        int r1 = Integer.parseInt(m1[0]);
        int c1 = Integer.parseInt(m1[1]);
        int r2 = Integer.parseInt(m1[2]);
        int c2 = Integer.parseInt(m1[3]);
        Piece piece1 = arr[r1][c1];
        Piece piece2 = arr[r2][c2];
        arr[r2][c2] = arr[r1][c1];
        arr[r1][c1] = null;
        piece1.r = r2;
        piece1.c = c2;
        int index = -1;
        if(piece2 != null){
            index = pieces.indexOf(piece2);
            pieces.remove(piece2);
        }

        boolean sacrifice = false;
        whiteToMove = !whiteToMove;
        for(Piece p : pieces){
            if(isLegalMove(p,p.r,p.c,r2,c2,false)){
                sacrifice = true;
            }
        }

        boolean avoidsCapture = true;
        int valueLost = -1;
        for(Piece p : pieces){
            for(Piece q : pieces){
                if(p.isWhite != arr[r2][c2].isWhite && q.isWhite == arr[r2][c2].isWhite &&
                        isLegalMove(p,p.r,p.c,q.r,q.c,false)){
                    avoidsCapture = false;
                    valueLost = Math.max(valueLost,points(arr[q.r][q.c]));
                }
            }
        }



        if(sacrifice){
            result -= 10 * points(arr[r2][c2]); // Punish sacrifice
            if(show) System.out.println("SACRIFICE -" + (10 * points(arr[r2][c2])));
        }
        if(piece1.isWhite && check(blackR(),blackC()) || !piece1.isWhite && check(whiteR(),whiteC())){
            // Bonus for non-sacrificial check
            if(avoidsCapture && !sacrifice && show){
                result += 70;
                System.out.println("NON SACRIFICIAL CHECK +70");
            }
            else if(show){
                result += 15;
                System.out.println("SACRIFICIAL CHECK +15");
            }
        }
        if(!avoidsCapture){
            result -= valueLost * 10;
            if(show) System.out.println("DOESN'T AVOID CAPTURE -" + (valueLost * 10));
        }
        else{
            if(arr[r2][c2] != null && arr[r2][c2].type == PieceType.PAWN){
                result += (arr[r2][c2].isWhite ? (c1 + 1) * 5 : (9 - c1) * 5);
                if(show) System.out.println("PAWN FORWARD +" + ((arr[r2][c2].isWhite ? (c1 + 1) * 5 : (9 - c1) * 5)));
            }
            result += 50;
            if(show) System.out.println("AVOIDS CAPTURE +50");
        }
        if(arr[r2][c2] != null && arr[r2][c2].type == PieceType.KING){
            result -= 30;
            System.out.println("KING MOVE -30");
        }
        whiteToMove = !whiteToMove;


        piece1.r = r1;
        piece1.c = c1;
        if(piece2 != null){
            pieces.add(index,piece2);
        }
        arr[r1][c1] = piece1;
        arr[r2][c2] = piece2;
        if(show) System.out.println("CAPTURE +" + (points(arr[r2][c2]) * 10));
        result += points(arr[r2][c2]) * 10; // Bonus for capture
        if(show) System.out.println("CENTRALITY +" + (-(r2) * (r2 - 8) + -(c2) * (c2 - 8))/3);
        if(show) System.out.println("\n");
        result += (-(r2) * (r2 - 8) + -(c2) * (c2 - 8))/3; // Bonus for centrality
        result += (int)(Math.random() * 10);
        System.out.println("TOTAL " + result);
        return result;
    }
    public int whiteR(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(arr[i][j] != null && arr[i][j].type == PieceType.KING && arr[i][j].isWhite){
                    return i;
                }
            }
        }
        return -1;
    }

    public int whiteC(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(arr[i][j] != null && arr[i][j].type == PieceType.KING && arr[i][j].isWhite){
                    return j;
                }
            }
        }
        return -1;
    }

    public int blackR(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(arr[i][j] != null && arr[i][j].type == PieceType.KING && !arr[i][j].isWhite){
                    return i;
                }
            }
        }
        return -1;
    }

    public int blackC(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(arr[i][j] != null && arr[i][j].type == PieceType.KING && !arr[i][j].isWhite){
                    return j;
                }
            }
        }
        return -1;
    }
    Game(){
        arr[0][0] = new Piece(PieceType.ROOK,true,0,0);
        arr[1][0] = new Piece(PieceType.KNIGHT,true,1,0);
        arr[2][0] = new Piece(PieceType.BISHOP,true,2,0);
        arr[3][0] = new Piece(PieceType.QUEEN,true,3,0);
        arr[4][0] = new Piece(PieceType.KING,true,4,0);
        arr[5][0] = new Piece(PieceType.BISHOP,true,5,0);
        arr[6][0] = new Piece(PieceType.KNIGHT,true,6,0);
        arr[7][0] = new Piece(PieceType.ROOK,true,7,0);

        for(int i = 0; i <= 7; i++){
            arr[i][1] = new Piece(PieceType.PAWN,true,i,1);
        }

        arr[0][7] = new Piece(PieceType.ROOK,false,0,7);
        arr[1][7] = new Piece(PieceType.KNIGHT,false,1,7);
        arr[2][7] = new Piece(PieceType.BISHOP,false,2,7);
        arr[3][7] = new Piece(PieceType.QUEEN,false,3,7);
        arr[4][7] = new Piece(PieceType.KING,false,4,7);
        arr[5][7] = new Piece(PieceType.BISHOP,false,5,7);
        arr[6][7] = new Piece(PieceType.KNIGHT,false,6,7);
        arr[7][7] = new Piece(PieceType.ROOK,false,7,7);
        for(int i = 0; i <= 7; i++){
            arr[i][6] = new Piece(PieceType.PAWN,false,i,6);
        }
        pieces = new ArrayList<Piece>();
        for(int i = 0; i <= 7; i++){
            for(int j = 0; j <= 7; j++){
                if(arr[i][j] != null){
                    pieces.add(arr[i][j]);
                }
            }
        }
        whiteCheck = false;
        blackCheck = false;
        done = false;

        legalMoves = new ArrayList<String>();
        moveStringWhite = "";
        moveStringBlack = "";
        algebraicMoves = new ArrayList<String>();
        removed = new Stack<Piece>();

    }
    boolean check(int row, int col){
        // Checks if the side moving is going to be under check after this move
        Piece piece = arr[row][col];
        if(piece.isWhite){
            // check white king
            // some piece must be able to move to king
            for(Piece pi : pieces){

                if(pi != null && pi.type != PieceType.KING && !pi.isWhite
                        && isLegalMove(pi,pi.r,pi.c,whiteR(),whiteC(),false)){

                    return true;
                }
                if(pi != null && pi.type == PieceType.KING && !pi.isWhite
                        && Math.max(Math.abs(pi.r - whiteR()),Math.abs(pi.c - whiteC())) == 1){
                    return true;
                }
            }


        }
        else{
            for(Piece pi : pieces){

                if(pi != null && pi.type != PieceType.KING && pi.isWhite
                        && isLegalMove(pi,pi.r,pi.c,blackR(),blackC(),false)){
                    return true;
                }
                if(pi != null && pi.type == PieceType.KING && pi.isWhite
                        && Math.max(Math.abs(pi.r - blackR()),Math.abs(pi.c - blackC())) == 1){
                    return true;
                }
            }


        }
        return false;
    }
    void unmove(int r1, int c1, int r2, int c2, boolean capture){
        arr[r1][c1] = arr[r2][c2];
        if(capture){
            arr[r2][c2] = removed.pop();
            pieces.add(arr[r2][c2]);
        }
        else{
            arr[r2][c2] = null;
        }

        arr[r1][c1].moved--;
        if(c2 == 7 && c1 == 6 && arr[r1][c1].type == PieceType.QUEEN){
            arr[r1][c1].type = PieceType.PAWN;
        }
        whiteToMove = !whiteToMove;
        moves--;
        whiteCheck = false;
        blackCheck = false;
        done = false;
        checkmate = false;
        stalemate = false;
        // Generate legal moves
        legalMoves.clear();

    }



    boolean move(int r1, int c1, int r2, int c2, boolean real) {
        if(!isValidCoordinates(r1,c1)) return false;
        if(!isValidCoordinates(r2,c2)) return false;
        if(arr[r1][c1] == null) {
            // No piece exists here
            return false;
        }
        else {
            // check legality of move
            if(whiteToMove != arr[r1][c1].isWhite){
                return false;
            }
            //System.out.println("IN MOVE");
            boolean legal = isLegalMove(arr[r1][c1],r1,c1,r2,c2,true);
            if(!legal){
                return false;
            }
            if(legalMove(arr[r1][c1],r1,c1,r2,c2)){
                Piece piece = arr[r1][c1];
                Piece piece2 = arr[r2][c2];


                arr[r1][c1].moved++;

                if(arr[r1][c1].type == PieceType.PAWN){
                    // Auto promote to queen
                    if(arr[r1][c1].isWhite && c2 == 7 || !arr[r1][c1].isWhite && c2 == 0){
                        arr[r1][c1].type = PieceType.QUEEN;
                        promote = true;
                    }
                }
                if(arr[r2][c2] != null){
                    // CAPTURED PIECE!!!!
                    pieces.remove(arr[r2][c2]);
                    removed.push(arr[r2][c2]);
                }


                arr[r1][c1].r = r2;
                arr[r1][c1].c = c2;
                arr[r2][c2] = arr[r1][c1];
                arr[r1][c1] = null;



                legalMoves.clear();
                whiteToMove = !whiteToMove;
                List<String> pairs = new ArrayList<String>();
                for(int kk = 0; kk < pieces.size(); kk++){
                    Piece pi = pieces.get(kk);
                    if(pi.isWhite != whiteToMove){
                        continue;
                    }
                    switch(pi.type){

                        case KING:
                            for(int dr = -1; dr <= 1; dr++){
                                for(int dc = -1; dc <= 1; dc++){
                                    if(dr == 0 && dc == 0) continue;
                                    if(isValidCoordinates(pi.r + dr, pi.c + dc)
                                    && validate(pi,pi.r,pi.c,pi.r + dr, pi.c + dc, false)){
                                        pairs.add(pi.r + " " + pi.c + " " + (pi.r + dr) + " " + (pi.c + dc));
                                    }
                                }
                            }
                            break;
                        case QUEEN:
                            for(int col = 0; col <= 7; col++){
                                if(col == pi.c) continue;
                                if(isValidCoordinates(pi.r,col)
                                && validate(pi,pi.r,pi.c,pi.r,col,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + col);
                                }
                            }
                            for(int row = 0; row <= 7; row++){
                                if(row == pi.r) continue;
                                if(isValidCoordinates(row,pi.c) &&
                                        validate(pi,pi.r,pi.c,row,pi.c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + row + " " + pi.c);
                                }
                            }
                            int sum = pi.c + pi.r;
                            int diff = pi.r - pi.c;
                            for(int r = 0; r <= 7; r++){
                                int c = sum - r;
                                if(isValidCoordinates(r,c) &&
                                        validate(pi,pi.r,pi.c,r,c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + r + " " + c);
                                }
                            }
                            for(int r = 0; r <= 7; r++){
                                int c = r - diff;
                                if(isValidCoordinates(r,c) &&
                                        validate(pi,pi.r,pi.c,r,c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + r + " " + c);
                                }
                            }
                            break;
                        case ROOK:
                            for(int col = 0; col <= 7; col++){
                                if(col == pi.c) continue;
                                if(isValidCoordinates(pi.r,col)
                                        && validate(pi,pi.r,pi.c,pi.r,col,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + col);
                                }
                            }
                            for(int row = 0; row <= 7; row++){
                                if(row == pi.r) continue;
                                if(isValidCoordinates(row,pi.c) &&
                                        validate(pi,pi.r,pi.c,row,pi.c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + row + " " + pi.c);
                                }
                            }
                            break;
                        case BISHOP:
                            int sm = pi.c + pi.r;
                            int dff = pi.r - pi.c;
                            for(int r = 0; r <= 7; r++){
                                int c = sm - r;
                                if(isValidCoordinates(r,c) &&
                                        validate(pi,pi.r,pi.c,r,c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + r + " " + c);
                                }
                            }
                            for(int r = 0; r <= 7; r++){
                                int c = r - dff;
                                if(isValidCoordinates(r,c) &&
                                        validate(pi,pi.r,pi.c,r,c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + r + " " + c);
                                }
                            }
                            break;
                        case KNIGHT:
                            int[][] d = {{2,1}, {2,-1}, {-2,1}, {-2,1},
                                    {1,2}, {1,-2}, {-1,2}, {-1,-2}};
                            for(int[] move : d){
                                int r = pi.r + move[0];
                                int c = pi.c + move[1];
                                if(isValidCoordinates(r,c) &&
                                        validate(pi,pi.r,pi.c,r,c,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + r + " " + c);
                                }
                            }
                            break;
                        case PAWN:
                            if(pi.isWhite){
                                if(isValidCoordinates(pi.r,pi.c + 1) &&
                                        validate(pi,pi.r,pi.c,pi.r,pi.c + 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + (pi.c + 1));
                                }
                                if(isValidCoordinates(pi.r,pi.c + 2) &&
                                        validate(pi,pi.r,pi.c,pi.r,pi.c + 2,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + (pi.c + 2));
                                }
                                if(isValidCoordinates(pi.r + 1,pi.c + 1) &&
                                        validate(pi,pi.r,pi.c,pi.r + 1,pi.c + 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + (pi.r + 1) + " " + (pi.c + 1));
                                }
                                if(isValidCoordinates(pi.r - 1,pi.c + 1) &&
                                        validate(pi,pi.r,pi.c,pi.r - 1,pi.c + 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + (pi.r - 1) + " " + (pi.c + 1));
                                }

                            }
                            else{
                                if(isValidCoordinates(pi.r,pi.c - 1) &&
                                        validate(pi,pi.r,pi.c,pi.r,pi.c - 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + (pi.c - 1));
                                }
                                if(isValidCoordinates(pi.r,pi.c - 2) &&
                                        validate(pi,pi.r,pi.c,pi.r,pi.c - 2,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + pi.r + " " + (pi.c - 2));
                                }
                                if(isValidCoordinates(pi.r + 1,pi.c - 1) &&
                                        validate(pi,pi.r,pi.c,pi.r + 1,pi.c - 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + (pi.r + 1) + " " + (pi.c - 1));
                                }
                                if(isValidCoordinates(pi.r - 1,pi.c - 1) &&
                                        validate(pi,pi.r,pi.c,pi.r - 1,pi.c - 1,false)){
                                    pairs.add(pi.r + " " + pi.c + " " + (pi.r - 1) + " " + (pi.c - 1));
                                }


                            }
                            break;


                    }


                }
                for(String s : pairs){
                    int first = Integer.parseInt(s.split(" ")[0]);
                    int second = Integer.parseInt(s.split(" ")[1]);
                    int i = Integer.parseInt(s.split(" ")[2]);
                    int j = Integer.parseInt(s.split(" ")[3]);
                    if(whiteToMove != arr[first][second].isWhite){
                        continue;
                    }

                    legalMoves.add(first + " " + second + " " + i + " " + j);

                }

                if(check(whiteR(),whiteC())){
                    whiteCheck = true;
                }
                else{
                    whiteCheck = false;
                }
                if(check(blackR(),blackC())){
                    blackCheck = true;
                }
                else{
                    blackCheck = false;
                }
                if(!whiteToMove){
                    checkstale(false);
                }
                else{
                    checkstale(true);
                }
                if(real){
                    algebraicMoves.add(algebraicNotation(piece,piece2,r1,c1,r2,c2,promote));
                    ArrayList<String> legalMovesCopy = new ArrayList<String>();
                    for(String str : legalMoves){
                        legalMovesCopy.add(str);
                    }

                }
                promote = false;

                moves++;
                return true;
            }
            return false;
        }

    }

    boolean validate(Piece piece, int r1, int c1, int r2, int c2, boolean makeMove){
        boolean a = isLegalMove(piece,r1,c1,r2,c2,makeMove);
        return a && legalMove(piece,r1,c1,r2,c2);
    }

    boolean isLegalMove(Piece piece, int r1, int c1, int r2, int c2, boolean makeMove){

        PieceType type = piece.type;
        boolean isWhite = piece.isWhite;
        if(piece.isSameColor(arr[r2][c2])) return false;

        switch(type){
            case KING:
                if(!(Math.max(Math.abs(r1 - r2), Math.abs(c1 - c2)) == 1)) {
                    if(Math.abs(r1 - r2) == 2){
                        if(r2 > r1){
                            r2++;
                            if(!(r2 < arr.length && c2 < arr[0].length)){
                                return false;
                            }
                            if(arr[r2][c2] == null || arr[r2][c2].type != PieceType.ROOK){
                                // not a rook, can't castle
                                return false;
                            }
                            if(c1 != c2){
                                // Not on same row
                                return false;
                            }
                            if(piece.moved > 0 || arr[r2][c2].moved > 0){
                                // the king or the rook has moved, invalid
                                return false;
                            }

                            for(int x = r1 + 1; x < r2; x++){
                                if(arr[x][c1] != null){
                                    // Interfering piece
                                    return false;
                                }
                            }
                            boolean valid = true;
                            Piece rook = arr[r2][c2];
                            for(int x = r1; x <= r2; x++){
                                int index = -1;
                                piece.r = x;
                                Piece piece2 = arr[x][c1];
                                arr[x][c1] = arr[r1][c1];
                                if(x != r1){
                                    arr[r1][c1] = null;
                                    if(piece2 != null){
                                        index = pieces.indexOf(piece2);
                                        pieces.remove(piece2);
                                    }
                                }


                                if(check(x,c1)){

                                    valid = false;
                                }

                                if(x != r1){
                                    arr[x][c1] = null;
                                    if(piece2 != null){
                                        pieces.add(index,piece2);
                                    }
                                }
                                piece.r = r1;

                                arr[r1][c1] = piece;
                                arr[r2][c2] = rook;
                            }
                            if(!valid) return false;
                            if(makeMove){
                                arr[r2][c2].r = r2 - 2;
                                arr[r2 - 2][c2] = rook;
                                arr[r2][c2] = null;
                            }

                            return true;

                        }
                        else{
                            r2 -= 2;
                            if(!(r2 >= 0 && c2 < arr[0].length)){
                                return false;
                            }
                            if(arr[r2][c2] == null || arr[r2][c2].type != PieceType.ROOK){
                                // not a rook, can't castle
                                return false;
                            }
                            if(c1 != c2){
                                // Not on same row
                                return false;
                            }
                            if(piece.moved > 0 || arr[r2][c2].moved > 0){
                                // the king or the rook has moved, invalid
                                return false;
                            }

                            for(int x = r1 - 1; x > r2; x--){
                                if(arr[x][c1] != null){
                                    // Interfering piece
                                    return false;
                                }
                            }
                            boolean valid = true;
                            Piece rook = arr[r2][c2];
                            for(int x = r1; x >= r2; x--){
                                int index = -1;
                                piece.r = x;
                                Piece piece2 = arr[x][c1];
                                arr[x][c1] = arr[r1][c1];
                                if(x != r1){
                                    arr[r1][c1] = null;
                                    if(piece2 != null){
                                        index = pieces.indexOf(piece2);
                                        pieces.remove(piece2);
                                    }
                                }
                                if(check(x,c1)){
                                    valid = false;
                                }
                                piece.r = r1;

                                if(x != r1){
                                    arr[x][c1] = null;
                                    if(piece2 != null) pieces.add(index,piece2);
                                }
                                arr[r1][c1] = piece;
                                arr[r2][c2] = rook;
                            }
                            if(!valid) return false;
                            if(makeMove){
                                arr[r2][c2].r = r2 + 3;
                                arr[r2 + 3][c2] = rook;
                                arr[r2][c2] = null;
                            }
                            return true;
                        }

                    }
                    else return false;
                }
                return true;
            case KNIGHT:
                if(!(Math.abs(r1 - r2) * Math.abs(c1 - c2) == 2)) {
                    return false;
                }
                break;
            case BISHOP:
                if(r1 - c1 == r2 - c2){
                    if(r1 < r2){
                        for(int row = r1 + 1; row < r2; row++){
                            int col = c1 + (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                    else{
                        for(int row = r1 - 1; row > r2; row--){
                            int col = c1 + (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                }
                else if(r1 + c1 == r2 + c2){
                    if(r1 < r2){
                        for(int row = r1 + 1; row < r2; row++){
                            int col = c1 - (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                    else{
                        for(int row = r1 - 1; row > r2; row--){
                            int col = c1 - (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                }
                else{
                    return false;
                }

                break;
            case QUEEN:
                if(r1 - c1 == r2 - c2){
                    if(r1 < r2){
                        for(int row = r1 + 1; row < r2; row++){
                            int col = c1 + (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                    else{
                        for(int row = r1 - 1; row > r2; row--){
                            int col = c1 + (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                }
                else if(r1 + c1 == r2 + c2) {
                    if (r1 < r2) {
                        for (int row = r1 + 1; row < r2; row++) {
                            int col = c1 - (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    } else {
                        for (int row = r1 - 1; row > r2; row--) {
                            int col = c1 - (row - r1);
                            if(arr[row][col] != null) return false;
                        }
                    }
                }
                else if(r1 == r2){
                    if(c1 < c2){
                        for(int col = c1 + 1; col < c2; col++){
                            if(arr[r1][col] != null) return false;
                        }
                    }
                    else{
                        for(int col = c1 - 1; col > c2; col--){
                            if(arr[r1][col] != null) return false;
                        }
                    }
                }

                else if(c1 == c2){
                    if(r1 < r2){
                        for(int row = r1 + 1; row < r2; row++) {
                            if (arr[row][c1] != null) return false;
                        }
                    }
                    else{
                        for(int row = r1 - 1; row > r2; row--){
                            if(arr[row][c1] != null) return false;
                        }
                    }
                }
                else{
                    return false;
                }
                break;
            case ROOK:
                if(!(r1 == r2 || c1 == c2)){
                    return false;
                }
                if(r1 == r2){
                    if(c1 < c2){
                        for(int col = c1 + 1; col < c2; col++) {
                            if(arr[r1][col] != null) return false;
                        }
                    }
                    else{
                        for(int col = c1 - 1; col > c2; col--){
                            if(arr[r1][col] != null) return false;
                        }
                    }
                }
                else{
                    if(r1 < r2){
                        for(int row = r1 + 1; row < r2; row++){
                            if(arr[row][c1] != null) return false;
                        }
                    }
                    else{
                        for(int row = r1 - 1; row > r2; row--){
                            if(arr[row][c1] != null) return false;
                        }
                    }
                }

                break;
            case PAWN:
                if(piece.isWhite){
                    if(r1 == r2){
                        if(c1 == 1){
                            if(c2 == 3) {
                                if((arr[r1][c1 + 2] != null || arr[r1][c1 + 1] != null)){
                                    return false;
                                }
                            }
                            else if(!(c2 == 2 && arr[r1][c1 + 1] == null)) return false;
                        }
                        else if(c2 != c1 + 1) return false;
                        if(arr[r1][c1 + 1] != null) return false;
                    }
                    else if(r2 == r1 + 1 || r2 == r1 - 1){
                        if(c2 != c1 + 1 || arr[r2][c2] == null || piece.isSameColor(arr[r2][c2])) return false;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    if(r1 == r2){
                        if(c1 == 6){
                            if(c2 == 4) {
                                if((arr[r1][c1 - 1] != null || arr[r1][c1 - 2] != null)){
                                    return false;
                                }
                            }
                            else if(!(c2 == 5 && arr[r1][c1 - 1] == null)) return false;
                        }
                        else if(c2 != c1 - 1) return false;
                        if(arr[r1][c1 - 1] != null) return false;
                    }
                    else if(r2 == r1 + 1 || r2 == r1 - 1){
                        if(c2 != c1 - 1 || arr[r2][c2] == null || piece.isSameColor(arr[r2][c2])) return false;
                    }
                    else{
                        return false;
                    }
                }
        }

        return true;
    }

    boolean legalMove(Piece piece, int r1, int c1, int r2, int c2){
        Piece old = arr[r2][c2];
        arr[r2][c2] = piece;
        int index = -1;
        if(old != null){
            index = pieces.indexOf(old);
            pieces.remove(old);
        }
        piece.r = r2;
        piece.c = c2;
        arr[r1][c1] = null;

        if(check(r2,c2)) {
            piece.r = r1;
            piece.c = c1;
            arr[r2][c2] = old;
            arr[r1][c1] = piece;
            if(old != null) pieces.add(index,old);
            return false;
        }
        piece.r = r1;
        piece.c = c1;
        if(old != null) pieces.add(index,old);
        arr[r2][c2] = old;
        arr[r1][c1] = piece;

        return true;
    }

    boolean isValidCoordinates(int r1, int c1){
        return (0 <= r1 && r1 <= 7 && 0 <= c1 && c1 <= 7);
    }

    void printBoardState(){
        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){
                System.out.print((arr[c][7 - r] == null) ? "." : arr[c][7-r]);
            }
            System.out.println();
        }
    }

    void checkstale(boolean white){
        // 1. The King must be under check
        // 2. We can't block the attack
        // 3. We can't move the king to a safe square in a legal move
        // 4. We can't capture the piece attacking
        if(white){
            boolean isChecked = check(whiteR(),whiteC());
            boolean escape = false;
            boolean block = false;
            boolean capture = false;
            for(String move : legalMoves){
                String[] spl = move.split(" ");
                int r1 = Integer.parseInt(spl[0]);
                int c1 = Integer.parseInt(spl[1]);
                int r2 = Integer.parseInt(spl[2]);
                int c2 = Integer.parseInt(spl[3]);

                if(!arr[r1][c1].isWhite) continue;


                Piece old1 = arr[r1][c1];
                Piece old2 = arr[r2][c2];
                arr[r2][c2] = arr[r1][c1];
                int index = -1;
                if(old2 != null){
                    index = pieces.indexOf(old2);
                    pieces.remove(old2);
                }
                old1.r = r2;
                old1.c = c2;
                arr[r1][c1] = null;

                if(!check(r2,c2)){
                    escape = true;
                }
                if(old2 != null) pieces.add(index,old2);
                old1.r = r1;
                old1.c = c1;
                arr[r1][c1] = old1;
                arr[r2][c2] = old2;


            }
            if(!escape && !block && !capture){
                if(isChecked){
                    checkmate = true;
                }
                else{
                    stalemate = true;
                }
                done = true;
            }

        }
        else{
            boolean isChecked = check(blackR(),blackC());
            boolean escape = false;
            boolean block = false;
            boolean capture = false;
            Piece king = arr[blackR()][blackC()];

            for(String move : legalMoves){
                String[] spl = move.split(" ");
                int r1 = Integer.parseInt(spl[0]);
                int c1 = Integer.parseInt(spl[1]);
                int r2 = Integer.parseInt(spl[2]);
                int c2 = Integer.parseInt(spl[3]);
                if(arr[r1][c1].isWhite) continue;

                Piece old1 = arr[r1][c1];
                Piece old2 = arr[r2][c2];
                old1.r = r2;
                old1.c = c2;
                arr[r2][c2] = arr[r1][c1];
                int index = -1;
                if(old2 != null){
                    index = pieces.indexOf(old2);
                    pieces.remove(old2);
                }
                arr[r1][c1] = null;

                if(!check(r2,c2)){
                    escape = true;
                }
                old1.r = r1;
                old1.c = c1;
                if(old2 != null) pieces.add(index,old2);
                arr[r1][c1] = old1;
                arr[r2][c2] = old2;


            }
            if(!escape){
                if(isChecked){
                    checkmate = true;
                }
                else{
                    stalemate = true;
                }
                done = true;
            }
        }
    }
    String getCode(int r2, int c2){
        return "" + (char)('a' + r2) + (char) (c2 + 49);
    }
    String algebraicNotation(Piece one, Piece two, int r1, int c1, int r2, int c2, boolean promote){

        if(one.type == PieceType.KING && two == null){
            if(r2 == r1 + 2){
                return "0-0";
            }
            else if(r2 == r1 - 2) {
                return "0-0-0";
            }
        }
        String res = "";
        // Figure out what type of piece is at arr[r1][c1]
        // Figure out what type of piece is at arr[r2][c2]
        String destCode = getCode(r2,c2);
        String code = one.type == PieceType.PAWN ? "" :
                one.toString().toUpperCase();
        res = code;
        if(two != null){
            // Normal move (not capture)
            // Do casework
            if(one.type == PieceType.PAWN) res = (char)('a' + r1) + "";
            res += "x";
        }
        res += destCode;
        if(promote){
            if(two != null) res = (char)('a' + r1) + res.substring(1);
            res += "=Q";
        }
        if(!checkmate && (whiteCheck || blackCheck)) res += "+";
        if(checkmate) res += "#";
        return res;
    }




}

public class ChessEngine {
    public static void main(String[] args) {
        Game g = new Game();
        Scanner sc = new Scanner(System.in);
        //g.printBoardState();
        while(true){
            int x = sc.nextInt();
            if(x == -1){
                break;
            }
            int y = sc.nextInt();
            int r = sc.nextInt();
            int c = sc.nextInt();
            Piece piece = g.arr[x][y];
            boolean legal = g.move(x,y,r,c,true);
            if(legal){
                System.out.println("MOVING " + piece + " from " + x + " " + y + " to " + r + " " + c);
            }
            else{
                System.out.println("INVALID MOVE");
            }
            g.printBoardState();
        }


    }
}
