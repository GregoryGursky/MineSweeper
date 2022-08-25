import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Level {
    private JFrame levelSelect;
    private Difficulty difficulty;
    private BoardGUI boardGUI;
    public Level(){
        showDifficulties();
    }

    private void showDifficulties(){
        levelSelect = new JFrame();
        levelSelect.setSize(250,450);
        levelSelect.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String text = "Easy \n 9x9 grid -- 10 mines";
        JButton easy = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        easy.setBackground(new Color(194, 237, 59));
        easy.setForeground(Color.WHITE);
        easy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setDifficulty((short) 1);
            }
        });

        text = "Medium \n 16x16 grid -- 40 mines ";
        JButton medium = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        medium.setBackground(new Color(255, 188, 0));
        medium.setForeground(Color.WHITE);
        medium.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setDifficulty((short) 2);
            }
        });

        text = "Hard \n 30x16 grid -- 99 mines";
        JButton hard = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        hard.setBackground(new Color(227, 85, 34));
        hard.setForeground(Color.WHITE);
        hard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setDifficulty((short) 3);
            }
        });

        ButtonGroup difficulties = new ButtonGroup();
        difficulties.add(easy);
        difficulties.add(medium);
        difficulties.add(hard);

        levelSelect.setLayout(new GridLayout(3,1,10,10));
        levelSelect.add(easy);
        levelSelect.add(medium);
        levelSelect.add(hard);
        levelSelect.setLocationRelativeTo(null);
        levelSelect.setVisible(true);
    }
    private void setDifficulty(short s){
        switch (s) {
            case 1 -> difficulty = Difficulty.Easy;
            case 2 -> difficulty = Difficulty.Medium;
            default -> difficulty = Difficulty.Hard;
        }
        levelSelect.setVisible(false);
        if (boardGUI == null) boardGUI = new BoardGUI(this);
        else boardGUI.changeLevel(this);
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void offerDifficulties() {
        levelSelect.setVisible(true);
    }
}
