import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Level {
    private JFrame levelSelect;
    private JFrame changeLevel;
    private JButton easy;
    private JButton medium;
    private JButton hard;
    private Difficulty difficulty;
    private BoardGUI boardGUI;
    public Level(){
        showDifficulties();
    }

    private void showDifficulties(){
        levelSelect = new JFrame();
        levelSelect.setSize(250,450);
        levelSelect.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        String text = "Easy \n 9x9 grid -- 10 mines";
        easy = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        easy.setBackground(new Color(194, 237, 59));
        easy.setForeground(Color.WHITE);
        easy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setDifficulty((short) 1);
            }
        });

        text = "Medium \n 16x16 grid -- 40 mines ";
        medium = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        medium.setBackground(new Color(255, 188, 0));
        medium.setForeground(Color.WHITE);
        medium.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                setDifficulty((short) 2);
            }
        });

        text = "Hard \n 30x16 grid -- 99 mines";
        hard = new JButton("<html>" + text.replaceAll("\n", "<br>") + "</html>");
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
        if (changeLevel == null) levelSelect.setVisible(false);
        else changeLevel.setVisible(false);
        if (boardGUI == null) boardGUI = new BoardGUI(this);
        else boardGUI.changeLevel(this);

    }
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void offerDifficulties() {
        if (changeLevel == null){
            changeLevel = new JFrame();
            changeLevel.setSize(250,450);
            changeLevel.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            changeLevel.setLayout(new GridLayout(3,1,10,10));
            changeLevel.add(easy);
            changeLevel.add(medium);
            changeLevel.add(hard);
            changeLevel.setLocationRelativeTo(null);
            changeLevel.setUndecorated(true);
        }
        changeLevel.setVisible(true);

//        changeLevel.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
//        levelSelect.setVisible(true);
    }
}
