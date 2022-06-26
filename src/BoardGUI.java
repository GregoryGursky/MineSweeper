import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardGUI {
    private JFrame boardGUI;
    private final JButton[][] allTiles;
    private Board board;
    private final Font font;

    public BoardGUI(){
        short[] level = askLevel();
        short col = level[0];
        short row = level[1];
        short mines = level[2];
        boardGUI = new JFrame();
        boardGUI.setTitle("Minesweeper");
        boardGUI.setSize(col * 40,row * 40 + 50);
        boardGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        boardGUI.setLayout(new GridLayout(row,col));
        allTiles = new JButton[row][col];
        font = new Font("Arial", Font.PLAIN, 20);

        for (short height = 0; height < row; height++) {
            for (short width = 0; width < col; width++) {
                JButton tileToAdd = new JButton();
                tileToAdd.setSize(30,30);
                TileLoc tileLoc = new TileLoc(width, height);
                tileToAdd.addMouseListener(new MouseAdapter() {
                    TileLoc[] response;
                    @Override
                    public void mousePressed(MouseEvent e){ // shades surrounding tiles
                        if (board != null){
                            if (e.getButton() == 1){
                                for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
                                    for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                                        if (validTile(rowOff, colOff)){
                                            allTiles[rowOff][colOff].setBackground(Color.GRAY);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e){ // undoes shading & clicks tile
                        switch (e.getButton()) {
                            case 1 -> {
                                if (board != null){
                                    for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
                                        for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                                            if (validTile(rowOff,colOff))
                                                allTiles[rowOff][colOff].setBackground(null);
                                        }
                                    }
                                } else {
                                    board = new Board(col, row, mines, tileLoc);
                                    board.printBoard();
                                }

                                if (board.mineCheck()) {
                                    dispLose();
                                }

                                response = board.leftClickTile(tileLoc); // returns null if tile is flagged
                                if (response != null) {
                                    System.out.println(board.getTilesClicked());
                                    for (TileLoc tl : response) {
                                        TileGUI tileGUI = board.getTileGui(tl);
                                        JButton changeGUI = allTiles[tl.row()][tl.col()];
                                        switch (tileGUI) {
                                            case NUMBER:
                                                changeGUI.setBackground(Color.WHITE);
                                                changeGUI.setFont(font);
                                                changeGUI.setMargin(new Insets(0, 0, 0, 0));
                                                changeGUI.setText(String.valueOf(board.getTileVal(tl.row(), tl.col())));
                                                break;
                                            case BOMB:
                                                if (allTiles[tl.row()][tl.col()].getBackground() != Color.ORANGE)
                                                    allTiles[tl.row()][tl.col()].setBackground(Color.RED);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                if (board.winCheck()) {
                                    dispWin();
                                }
                            }
                            case 3 -> {
                                response = board.rightClickTile(tileLoc);
                                if (response != null) { // returns null if tile is clicked
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
                            }
                            default -> e.consume();
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
                });
                allTiles[height][width] = tileToAdd;
                boardGUI.add(tileToAdd);
            }
        }
        boardGUI.setVisible(true);

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
