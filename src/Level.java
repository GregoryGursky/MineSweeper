import javax.swing.*;
import java.awt.*;

public class Level {
    private JFrame levelSelect;
    private JButton easy;
    private JButton medium;
    private JButton hard;

    public Level(){
        levelSelect = new JFrame();
        levelSelect.setSize(100,300);

        easy = new JButton();
        easy.setText("Easy");
        easy.setBackground(Color.GRAY);
        easy.setForeground(new Color(194, 237, 59));

        medium = new JButton();
        medium.setText("Medium");
        medium.setBackground(Color.GRAY);
        medium.setForeground(new Color(255, 188, 0));

        hard = new JButton();
        hard.setText("Hard");
        hard.setBackground(Color.GRAY);
        hard.setForeground(new Color(227, 85, 34));


        ButtonGroup difficulties = new ButtonGroup();
        difficulties.add(easy);
        difficulties.add(medium);
        difficulties.add(hard);

        levelSelect.setLayout(new GridLayout(3,1,10,10));
        levelSelect.add(easy);
        levelSelect.add(medium);
        levelSelect.add(hard);
        levelSelect.setVisible(true);
    }
}
