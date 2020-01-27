package controller;

import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * @author Lukas Allermann */
public class EndScreenController {
    @FXML
    Label Message;
    @FXML
    Label WinnerLabel;
    private Game game;
    public void setGame(Game game) {
        this.game = game;
    }
    public void setMessage(Label message) {
        Message = message;
    }
    public void setWinnerLabel(Label winnerLabel) {
        WinnerLabel = winnerLabel;
    }

    public void initScreen(String reason) {
        // EvilWins
        // CoupleWins
        // GoodWins
        if(reason.equals("GoodWins")) {
            WinnerLabel.setFont(new Font(27));
            WinnerLabel.setText("The village is freed from evil and can continue to live peacefully");
        }
        if(reason.equals("CoupleWins")) WinnerLabel.setText("The couple are the glorious Winners");
        if(reason.equals("EvilWins")) WinnerLabel.setText("The Werewolves have won and dominate the Night!");
    }
}
