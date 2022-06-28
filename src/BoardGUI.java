import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardGUI {
    private JFrame boardGUI;
    private Board board;
    private final JButton[][] allTiles;
    private final Font font;
    private boolean gameInProgress;
    private short row;
    private short col;
    private short mines;

    public BoardGUI(){
        short[] level = askLevel();
        col = level[0];
        row = level[1];
        mines = level[2];
        boardGUI = new JFrame();
        boardGUI.setTitle("Minesweeper");
        boardGUI.setSize(col * 32,row * 32 + 50);
        boardGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        boardGUI.setLayout(new GridLayout(row,col));
        allTiles = new JButton[row][col];
        font = new Font("Arial", Font.BOLD, 20);
        gameInProgress = true;

        for (short height = 0; height < row; height++) {
            for (short width = 0; width < col; width++) {
                JButton tileToAdd = new JButton();
                tileToAdd.setSize(30,30);
                TileLoc tileLoc = new TileLoc(width, height);
                tileToAdd.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        if (board != null && gameInProgress){
                            if (e.getButton() == 1){
                                shadeAdj(tileLoc);
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e){
                        TileLoc[] response;
                        switch (e.getButton()) {
                            case 1 -> {
                                if (gameInProgress){
                                    if (board != null) {
                                        unshadeAdj(tileLoc);
                                    } else {
                                        board = new Board(col, row, mines, tileLoc);
                                        board.printBoard();
                                    }

                                    response = board.leftClickTile(tileLoc); // clicks tile
                                    if (response != null) { // returns null if tile is flagged
                                        setGUI(response);
                                    }
                                }

                                if (board.mineCheck()) {
                                    dispLose();
                                    gameInProgress = false;
                                    e.consume();
                                }
                                if (board.winCheck()) {
                                    dispWin();
                                    gameInProgress = false;
                                    e.consume();
                                }
                            }

                            case 3 -> {
                                if (gameInProgress){
                                    response = board.rightClickTile(tileLoc);
                                    if (response != null) { // returns null if tile is clicked
                                        setFlag(response);
                                    }
                                }

                            }
                            default -> e.consume();
                        }
                    }
                });
                allTiles[height][width] = tileToAdd;
                boardGUI.add(tileToAdd);
            }
        }
        boardGUI.setVisible(true);
    }

    private void setFlag(TileLoc[] response) {
        for (TileLoc tl : response) {
            TileGUI tileGUI = board.getTileGui(tl);
            switch (tileGUI) {
                case FLAG -> allTiles[tl.row()][tl.col()].setBackground(Color.ORANGE);
                case UNFLAG -> allTiles[tl.row()][tl.col()].setBackground(null);
                default -> {
                }
            }
        }
    }

    private void setGUI(TileLoc[] response) {
        short num;
        for (TileLoc tl : response) {
            TileGUI tileGUI = board.getTileGui(tl);
            JButton changeGUI = allTiles[tl.row()][tl.col()];
            switch (tileGUI) {
                case NUMBER:
                    setBackground(changeGUI);
                    num = board.getTileVal(tl.row(), tl.col());
                    if (num != 0){
                        changeGUI.setText(String.valueOf(num));
                        setForeground(num, changeGUI);
                    }
                    break;
                case BOMB:
                    if (allTiles[tl.row()][tl.col()].getBackground() != Color.ORANGE)
                        allTiles[tl.row()][tl.col()].setBackground(Color.RED);
                    break;
            }
        }
    }

    private void setBackground(JButton changeGUI) {
        changeGUI.setBackground(Color.WHITE);
        changeGUI.setFont(font);
        changeGUI.setMargin(new Insets(0, 0, 0, 0));
    }

    private void setForeground(short num, JButton changeGUI) {
        switch (num) {
            case (1) -> changeGUI.setForeground(new Color(13, 143, 214));
            case (2) -> changeGUI.setForeground(new Color(0, 255, 255));
            case (3) -> changeGUI.setForeground(new Color(194, 237, 59));
            case (4) -> changeGUI.setForeground(new Color(255, 188, 0));
            case (5) -> changeGUI.setForeground(new Color(227, 85, 34));
            case (6) -> changeGUI.setForeground(new Color(139, 27, 31));
            case (7) -> changeGUI.setForeground(new Color(255,0,255));
            case (8) -> changeGUI.setForeground(new Color(140,59,226));
        }
    }

    private void shadeAdj(TileLoc tileLoc) {
        for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
            for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                if (validTile(rowOff, colOff)){
                    allTiles[rowOff][colOff].setBackground(Color.GRAY);
                }
            }
        }
    }

    private void unshadeAdj(TileLoc tileLoc) {
        for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
            for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                if (validTile(rowOff,colOff))
                    allTiles[rowOff][colOff].setBackground(null);
            }
        }
    }

    private boolean validTile(short rowOff, short colOff){
        boolean valid = false;
        if (rowOff >= 0 && rowOff < row)
            if (colOff >= 0 && colOff < col)
                if (!board.isClicked(rowOff, colOff) && !board.isFlagged(rowOff, colOff))
                    valid = true;
        return valid;
    }

    private void dispWin() {
        JOptionPane.showMessageDialog(null,"You Win","Minesweeper",JOptionPane.PLAIN_MESSAGE);
    }

    private void dispLose() {
        JOptionPane.showMessageDialog(null,"You Lose, Try Again","Minesweeper",JOptionPane.PLAIN_MESSAGE);
    }

    private short[] askLevel() {
        return new short[]{30, 16, 99};
    }
}
