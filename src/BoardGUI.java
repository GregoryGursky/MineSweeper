import javax.swing.*;
import java.awt.*;

public class BoardGUI {
    JFrame board;

    public BoardGUI(){
        int[] level = askLevel();
        board = new JFrame();
        board.setLayout(new GridLayout(level[0],level[1], 5,5 ));
        board.setTitle("Minesweeper");
        board.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        board.setVisible(true);
    }

    private int[] askLevel() {
        return new int[]{30, 16, 99};
    }
}
