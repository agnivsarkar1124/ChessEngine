import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChessGUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        BoardPanel board = new BoardPanel();

        frame.add(board);
        frame.setSize(1200, 1000);
        frame.setVisible(true);
    }
}

class BoardPanel extends JPanel {

    private static final int TILE_SIZE = 80;

    private Game g;

    private int selectedX = -1, selectedY = -1;

    private String[][] board = new String[8][8];
    private static HashMap<String,Image> img;

    public BoardPanel() {
        setOpaque(true);
        img = new HashMap<String,Image>();
        String[] codes = {"b","B","k","K","n","N","p","P","q","Q","r","R"};
        for(String code : codes){
            String name = "Chess_" + code.toLowerCase() + (code.toLowerCase().equals(code) ? "d" : "l") + "t60.png";
            img.put(code,new ImageIcon(name).getImage());
        }
        g = new Game();
        initBoard();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                selectedX = e.getX() / TILE_SIZE;
                selectedY = e.getY() / TILE_SIZE;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x2 = e.getX() / TILE_SIZE;
                int y2 = e.getY() / TILE_SIZE;

                if (inBounds(selectedX, selectedY) && inBounds(x2, y2)) {
                    move(selectedX, 7 - selectedY, x2, 7 - y2);
                }

                selectedX = -1;
                selectedY = -1;

                repaint();
            }
        });
    }

    private void initBoard() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = (g.arr[i][j] != null) ? g.arr[i][j].toString() : null;
            }
        }

    }

    private void updateBoard(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = (g.arr[i][j] != null) ? g.arr[i][j].toString() : null;
            }
        }
    }

    private void move(int x1, int y1, int x2, int y2) {

        boolean worked = g.move(x1,y1,x2,y2,true);
        if(!worked){
            return;
        }

        if(g.done){
            updateBoard();
            repaint();
            return;
        }

        // sort legal moves by the heuristic of the evaluation function from the current board state
        List<String> copy = new ArrayList<String>(g.legalMoves);
        Collections.shuffle(copy);

        Map<String,Integer> memo = new HashMap<String,Integer>();
        for(int i = 0; i < copy.size(); i++){
            System.out.println(copy.get(i));
            System.out.println("CHOICE " + i + "\n");
            memo.put(copy.get(i),g.score(copy.get(i),true));
            System.out.println("\n");
        }

        copy.sort((a,b) -> memo.get(b) - memo.get(a));
        int index = 0;
        String[] sp = copy.get(index).split(" ");
        boolean t = g.move(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]), Integer.parseInt(sp[2]),
                Integer.parseInt(sp[3]),true);


        if(g.done){
            updateBoard();
            return;
        }

        updateBoard();





    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        // draw board
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                boolean light = (x + y) % 2 == 0;
                gr.setColor(light ? Color.WHITE : Color.GRAY);

                gr.fillRect(x * TILE_SIZE, (7 - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // highlight selected square
                if (x == selectedX && y == selectedY) {
                    gr.setColor(Color.YELLOW);
                    gr.drawRect(x * TILE_SIZE, (7 - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gr.drawRect(x * TILE_SIZE + 1, (7 - y) * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                }

                // draw piece
                if (board[x][y] != null) {
                    gr.setColor(Color.BLACK);
                    gr.setFont(new Font("Arial", Font.BOLD, 24));
                    gr.drawImage(img.get(board[x][y].toString()), x * TILE_SIZE, (7 - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }

        gr.setColor(Color.BLACK);
        if(g.done){
            if(g.checkmate){
                if(g.whiteToMove) gr.drawString("BLACK WINS - WHITE CHECKMATED", 650, 50);
                else gr.drawString("WHITE WINS - BLACK CHECKMATED", 650, 50);
            }
            else{
                gr.drawString("DRAW - STALEMATE", 650, 50);
            }
        }
        int low = g.moves/2 - Math.min(g.moves/2,13) + 1;
        int it = low;
        for(it = low; it <= g.moves/2; it++){
            gr.drawString(it + ". " + g.algebraicMoves.get((it - 1) * 2), 650, 50 + 50 * (it - low + 1));
            gr.drawString(g.algebraicMoves.get((it - 1) * 2 + 1), 790, 50 + 50 * (it - low + 1));
        }
        if(g.moves%2 == 1) gr.drawString(it + ". " + g.algebraicMoves.get((it - 1) * 2), 650, 50 + 50 * (it - low + 1));


    }
}