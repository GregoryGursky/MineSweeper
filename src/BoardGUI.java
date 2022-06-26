import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardGUI {
    private boolean createBoard;
    private boolean contGame;
    private JFrame boardGUI;
    private JButton[][] allTiles;
    private Board board;
    private Font font;

    public BoardGUI(){
        short[] level = askLevel();
        short col = level[0];
        short row = level[1];
        short mines = level[2];
        createBoard = true;
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
                    public void mouseClicked(MouseEvent e){ // 1 is left click, 3 is right click
                        switch (e.getButton()){
                            case 1:
                                if (board == null){
                                    board = new Board(col,row,mines,tileLoc);
                                    board.printBoard();
                                    createBoard = false;
                                }

                                response = board.leftClickTile(tileLoc); // returns null if tile is flagged
                                if (response != null){
                                    for (TileLoc tl: response){
                                        TileGUI tileGUI = board.getTileGui(tl);
                                        JButton changeGUI = allTiles[tl.row()][tl.col()];
                                        switch (tileGUI){
                                            case NUMBER:
                                                changeGUI.setBackground(Color.WHITE);
                                                changeGUI.setFont(font);
                                                changeGUI.setMargin(new Insets(0,0,0,0));
                                                changeGUI.setText(String.valueOf(board.getTileVal(tl.row(),tl.col())));
                                                break;
                                            case BOMB:
                                                if (allTiles[tl.row()][tl.col()].getBackground() != Color.ORANGE)
                                                    allTiles[tl.row()][tl.col()].setBackground(Color.RED);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
//                                    contGame = response.continueGame();
//                                    TileGUI tGUI = response.tileGUI();
//                                    for (TileLoc tLoc: response.tileLocs()){
//                                        switch (tGUI){
//                                            case NUMBER:
//                                                JButton changeGUI = allTiles[tLoc.row()][tLoc.col()];
//                                                changeGUI.setBackground(Color.WHITE);
//                                                changeGUI.setFont(font);
//                                                changeGUI.setMargin(new Insets(0,0,0,0));
//                                                changeGUI.setText(String.valueOf(board.getTileVal(tLoc.row(),tLoc.col()))); // quick fix, I forgot to make the GUI_Response return the tile val
//                                                break;
//                                            case BOMB:
//                                                if (allTiles[tLoc.row()][tLoc.col()].getBackground() != Color.ORANGE)
//                                                    allTiles[tLoc.row()][tLoc.col()].setBackground(Color.RED);
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                    }
                                }
                                break;
                            case 3:
                                response = board.rightClickTile(tileLoc);
                                if (response != null) { // returns null if tile is clicked
                                    for (TileLoc tl: response){
                                        TileGUI tileGUI = board.getTileGui(tl);
                                        switch (tileGUI){
                                            case FLAG -> allTiles[tl.row()][tl.col()].setBackground(Color.ORANGE);
                                            case UNFLAG -> allTiles[tl.row()][tl.col()].setBackground(null);
                                            default -> {
                                            }
                                        }
                                    }
//                                    TileGUI tGUI = response.tileGUI();
//                                    for (TileLoc tLoc: response.tileLocs()){
//                                        switch (tGUI) {
//                                            case FLAG -> allTiles[tLoc.row()][tLoc.col()].setBackground(Color.ORANGE);
//                                            case UNFLAG -> allTiles[tLoc.row()][tLoc.col()].setBackground(null);
//                                            default -> {
//                                            }
//                                        }
//                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e){ // shades surrounding tiles
                        if (e.getButton() == 1){
                            long stopWatch = e.getWhen() + 75;
                            while (System.currentTimeMillis() < stopWatch){
                                // wait to see if not released
                            }
                            for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
                                for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                                    if (validTile(rowOff, colOff)){
                                        allTiles[rowOff][colOff].setBackground(Color.GRAY);
                                    }
                                }
                            }
                        }
                    }



                    @Override
                    public void mouseReleased(MouseEvent e){ // undoes shading & clicks tile
                        if (e.getButton() == 1 && !createBoard){
                            for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
                                for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                                    if (validTile(rowOff,colOff))
                                        allTiles[rowOff][colOff].setBackground(null);
                                }
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

                });
                allTiles[height][width] = tileToAdd;
                boardGUI.add(tileToAdd);
            }
        }

        boardGUI.setVisible(true);

    }

    private void dispEnd() {
        JOptionPane.showMessageDialog(null,"Invalid, Try Again","Invalid Guesser's Circle",JOptionPane.PLAIN_MESSAGE);
    }

    private short[] askLevel() {
        return new short[]{30, 16, 99};
    }
}
