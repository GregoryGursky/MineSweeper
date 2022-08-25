import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class BoardGUI {
    private final JFrame boardGUI;
    private final JPanel gamePanel;
    private JPanel minePanel;
    private final InfoPanel infoPanel;
    private Board board;
    private JButton[][] allTiles;
    private final Font font;
    private boolean gameInProgress;
    private Long startTime;
    private Difficulty difficulty;
    private short row;
    private short col;
    private short mines;
    private short flags;
    public BoardGUI(Level level){
        setLevelSettings(level);
        flags = 0;
        boardGUI = new JFrame(); // contains the game panel and the info panel
        boardGUI.setTitle("Minesweeper");
        boardGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gamePanel = new JPanel(); // has a set size, info panel on left, board on right
        gamePanel.setSize(new Dimension(1500,800));
        setMinePanelSettings();
        gameInProgress = true;
        font = new Font("Arial", Font.BOLD, 20);
        infoPanel = new InfoPanel(level); // set size
        infoPanel.setPreferredSize(new Dimension(275, gamePanel.getHeight()));
        boardGUI.setLayout(new BorderLayout());
        boardGUI.setSize(new Dimension(infoPanel.getWidth() + gamePanel.getWidth(), gamePanel.getHeight()));
        createMinePanel();
        gamePanel.setLayout(new GridBagLayout());
        gamePanel.add(minePanel);
        boardGUI.add(gamePanel, BorderLayout.CENTER);
        boardGUI.add(infoPanel, BorderLayout.WEST);
        boardGUI.setLocationRelativeTo(null);
        boardGUI.setVisible(true);
    }

    private void setMinePanelSettings() {
        minePanel = new JPanel();
        minePanel.setPreferredSize(new Dimension(col * difficulty.getTileSize(), row * difficulty.getTileSize()));
        minePanel.setLayout(new GridLayout(row,col));
        allTiles = new JButton[row][col];
    }

    private void setLevelSettings(Level level) {
        difficulty = level.getDifficulty();
        col = difficulty.getCol();
        row = difficulty.getRow();
        mines = difficulty.getMines();
    }
    private void createMinePanel() {
        for (short newRow = 0; newRow < row; newRow++) {
            for (short newCol = 0; newCol < col; newCol++) {
                createJButton(newRow,newCol);
            }
        }
    }
    private void createJButton(short newRow,short newCol) {
        JButton tileToAdd = new JButton();
        tileToAdd.setSize(difficulty.getTileSize(),difficulty.getTileSize());
        TileLoc tileLoc = new TileLoc(newCol, newRow);
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
                            if (board == null) {
                                board = new Board(col, row, mines, tileLoc);
                                startTime = System.currentTimeMillis();
                            } else {
                                unshadeAdj(tileLoc);
                            }
                            response = board.leftClickTile(tileLoc); // clicks tile
                            if (response != null) { // returns null if tile is flagged
                                setGUI(response);
                            }
                        }
                        gameInProgressCheck();
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
        allTiles[newRow][newCol] = tileToAdd;
        minePanel.add(tileToAdd);
    }
    private void gameInProgressCheck() {
        mineCheck();
        winCheck();
    }
    private void mineCheck(){
        if (board.mineCheck()) {
            gameInProgress = false;
            dispLose();
        }
    }
    private void winCheck(){
        if (board != null && board.winCheck()) {
            gameInProgress = false;
            dispWin();
        }
    }
    private void setFlag(TileLoc @NotNull [] response) {
        for (TileLoc tl : response) {
            TileGUI tileGUI = board.getTileGui(tl);
            switch (tileGUI) {
                case FLAG -> {
                    allTiles[tl.row()][tl.col()].setBackground(Color.ORANGE);
                    flags++;
                }
                case UNFLAG -> {
                    allTiles[tl.row()][tl.col()].setBackground(null);
                    flags--;
                }
                default -> {
                }
            }
        }
    }
    private void setGUI(TileLoc @NotNull [] response) {
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
    private void setBackground(@NotNull JButton changeGUI) {
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
    private void shadeAdj(@NotNull TileLoc tileLoc) {
        for (short rowOff = (short) (tileLoc.row() - 1); rowOff < tileLoc.row() + 2; rowOff++) {
            for (short colOff = (short) (tileLoc.col() - 1); colOff < tileLoc.col() + 2; colOff++) {
                if (validTile(rowOff, colOff)){
                    allTiles[rowOff][colOff].setBackground(Color.GRAY);
                }
            }
        }
    }
    private void unshadeAdj(@NotNull TileLoc tileLoc) {
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
        if (JOptionPane.showConfirmDialog(null, "Play Again?", "You Win", JOptionPane.YES_NO_OPTION) == 0)
            playAgain();
    }
    private void dispLose() {
        if (JOptionPane.showConfirmDialog(null, "Play Again?", "Game Over, You Lost", JOptionPane.YES_NO_OPTION) == 0)
            playAgain();
    }
    private void playAgain(){
        resetGui();
        resetOthers();
    }
    private void resetOthers() {
        board = null;
        gameInProgress = true;
        flags = 0;
        infoPanel.resetTime();
    }
    protected void changeLevel(Level level){
        setLevelSettings(level);
        gamePanel.removeAll();
        setMinePanelSettings();
        createMinePanel();
        resetOthers();
        gamePanel.add(minePanel);
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    private void resetGui(){
        for (short resetRow = 0; resetRow < row; resetRow++){
            for (short resetCol = 0; resetCol < col; resetCol++){
                allTiles[resetRow][resetCol].setBackground(null);
                allTiles[resetRow][resetCol].setForeground(null);
                allTiles[resetRow][resetCol].setText(null);
            }
        }
    }
    class InfoPanel extends JPanel{
        private final Level level;
        private long elapsed = 0;

        JButton changeDiff;
        Timer timer;
        InfoPanel(Level level){
            this.level = level;
            this.setBackground(Color.GRAY);
            changeDiff = new JButton();
            changeDiff.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showLevels();
                }
            });
            this.add(changeDiff);
            timer  = new Timer(10,
                    e -> repaint()
            );
            timer.start();
        }

        private void showLevels() {
            level.offerDifficulties();
        }

        public long getTime(){
            return elapsed;
        }
        public void resetTime(){
            elapsed = 0;
        }
        @Override
        public void paint(Graphics g){
            super.paint(g);
            g.setColor(Color.DARK_GRAY);
            short timerX = 125;
            short timerY = 100;
            g.fillRect(timerX - 70 , timerY - 40,150,60);
            short mineCountX = 125;
            short mineCountY = 350;
            g.fillRect(mineCountX - 70, mineCountY - 40, 150,60);

            g.setFont(new Font("Ariel", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("TIME", timerX - 35, timerY - 50);
            if (board != null && gameInProgress)
                elapsed = (System.currentTimeMillis() - startTime) / 1000;
            g.drawString(String.valueOf(elapsed), timerX, timerY);
            g.drawString("MINES LEFT", mineCountX - 85, mineCountY - 50);
            g.drawString(String.valueOf(mines - flags), mineCountX, mineCountY);
        }
    }
}
