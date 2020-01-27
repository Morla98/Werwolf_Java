package controller;

import game.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import models.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Lukas Allermann */

public class EventController {
    @FXML
    AnchorPane Event;
    @FXML
    AnchorPane CupidEvent;
    @FXML
    AnchorPane WitchEvent;
    @FXML
    AnchorPane WitchEventHeal;
    @FXML
    AnchorPane WitchEventKill;
    @FXML
    AnchorPane HunterEvent;
    @FXML
    AnchorPane SeerEvent;
    @FXML
    AnchorPane MayorEvent;
    @FXML
    Rectangle background;
    @FXML
    Label WitchHealLabel;
    @FXML
    ListView<Player> SeerListView;
    @FXML
    Label CupidWarningLabel;
    @FXML
    ListView<Player> CupidListView1;
    @FXML
    ListView<Player> CupidListView2;
    @FXML
    ListView<Player> WitchKillListView;
    @FXML
    ListView<Player> HunterListView;
    @FXML
    ListView<Player> MayorListView;
    @FXML
    AnchorPane MayorCandidateVote;
    @FXML
    ListView<Player> MayorElectionListView;

    private Game game;
    public void setGame(Game game) {
        this.game = game;
    }

    void hideAllEvents(){
        endCupidEvent();
        endHunterNo();
        endMayorEvent();
        endSeer();
        endWitchHealNo();
        endWitchKillNo();
    }
    void initCupidEvent(){
        ObservableList<Player> p1 = FXCollections.observableArrayList();
        ObservableList<Player> p2 = FXCollections.observableArrayList();
        Platform.runLater(()-> {
            CupidListView1.setItems(p1);
            p1.setAll(game.getPlayers());
            p2.setAll(game.getPlayers());
            CupidListView2.setItems(p2);
            Event.setVisible(true);
            CupidEvent.setVisible(true);
        });

    }
    void initSeerEvent(){
        ObservableList<Player> p = FXCollections.observableArrayList();
        p.setAll(game.getAlivePlayers());
        Platform.runLater(()-> SeerListView.setItems(p));
        Event.setVisible(true);
        SeerEvent.setVisible(true);
    }
    void initMayorEvent(){
        ObservableList<Player> p = FXCollections.observableArrayList();
        p.setAll(game.getMayorDecisionPossibilities());
        Platform.runLater(()-> MayorListView.setItems(p));
        Event.setVisible(true);
        MayorEvent.setVisible(true);
    }
    void initHunterEvent(){
        ObservableList<Player> p = FXCollections.observableArrayList();
        p.setAll(game.getAlivePlayers());
        Platform.runLater(()-> HunterListView.setItems(p));
        Event.setVisible(true);
        HunterEvent.setVisible(true);
    }
    void initWitchEventHeal(Player p){
        Platform.runLater(()->{
            WitchHealLabel.setText(p.getName() + " is dying!");
            Event.setVisible(true);
            WitchEvent.setVisible(true);
            WitchEventHeal.setVisible(true);
            });
        }
    void initWitchEventKill(){
        ObservableList<Player> p = FXCollections.observableArrayList();
        p.setAll(game.getAlivePlayers());
        Platform.runLater(()-> WitchKillListView.setItems(p));
        Event.setVisible(true);
        WitchEvent.setVisible(true);
        WitchEventKill.setVisible(true);
    }
    @FXML
    void endCupidEvent(){
        Player choosen1 = CupidListView1.getSelectionModel().getSelectedItem();
        Player choosen2 = CupidListView2.getSelectionModel().getSelectedItem();
        if(choosen1 != null && choosen2 != null) {
            if (choosen1.getName().equals(choosen2.getName())) {
                CupidWarningLabel.setVisible(true);
            } else {
                CupidEvent.setVisible(false);
                Event.setVisible(false);
                game.getDayScreenController().addText("SERVER: You shot your love arrows at "+ choosen1.getName() + " and " + choosen2.getName() + ". They fell in love!\n");
                game.CupidDecision(choosen1, choosen2);
            }
        }else{
            ArrayList<Player> p = game.getAlivePlayers();
            Random r = new Random();
            int player1Index;
            int player2Index;
            do{
                player1Index = r.nextInt(p.size());
                player2Index = r.nextInt(p.size());
            }while (player1Index == player2Index);
            choosen1 = p.get(player1Index);
            choosen2 = p.get(player2Index);
            CupidEvent.setVisible(false);
            Event.setVisible(false);
            game.getDayScreenController().addText("SERVER: You shot your love arrows at "+ choosen1.getName() + " and " + choosen2.getName() + ". They fell in love!\n");
            game.CupidDecision(choosen1, choosen2);
        }
    }
    public void initMayorElection(){
        ObservableList<Player> p = FXCollections.observableArrayList();
        p.setAll(game.getAlivePlayers());
        Platform.runLater(()-> MayorElectionListView.setItems(p));
        game.getDayScreenController().setPhase("Mayor Election");
        MayorCandidateVote.setVisible(true);
        Event.setVisible(true);
    }
    @FXML
    void endMayorElection(){
        Player choosen = MayorElectionListView.getSelectionModel().getSelectedItem();
        MayorCandidateVote.setVisible(false);
        Event.setVisible(false);
        game.MayorElectionDecision(choosen);
    }
    @FXML
    void endMayorEvent(){
        Player choosen = MayorListView.getSelectionModel().getSelectedItem();
        MayorEvent.setVisible(false);
        Event.setVisible(false);
        game.MayorDecision(choosen);
    }
    @FXML
    void endWitchHealYes(){
        WitchEventHeal.setVisible(false);
        WitchEvent.setVisible(false);
        Event.setVisible(false);
        game.WitchHealDecisionYes();
    }
    @FXML
    void endWitchHealNo(){
        WitchEventHeal.setVisible(false);
        WitchEvent.setVisible(false);
        Event.setVisible(false);
        game.WitchHealDecisionNo();
    }
    @FXML
    void endWitchKillYes(){
        Player choosen = WitchKillListView.getSelectionModel().getSelectedItem();
        WitchEventKill.setVisible(false);
        WitchEvent.setVisible(false);
        Event.setVisible(false);
        game.WitchKillDecisionYes(choosen);
    }
    @FXML
    void endWitchKillNo(){
        WitchEventKill.setVisible(false);
        WitchEvent.setVisible(false);
        Event.setVisible(false);
        game.WitchKillDecisionNo();
    }
    @FXML
    void endSeer(){
        Player choosen = SeerListView.getSelectionModel().getSelectedItem();
            if(choosen.getRole().getEvil()){
                game.getNightScreenController().addText("Seer Vision: Your vision tells you that your choice has an evil soul");
            }else{
                game.getNightScreenController().addText("Seer Vision: Your vision tells you that your choice has a good soul");
            }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SeerEvent.setVisible(false);
            Event.setVisible(false);
            game.SeerDecision(choosen);
    }
    @FXML
    void endHunterYes(){
        Player choosen = HunterListView.getSelectionModel().getSelectedItem();
        HunterEvent.setVisible(false);
        Event.setVisible(false);
        game.HunterDecisionYes(choosen, game.getHunterJumpPoint());
    }
    @FXML
    void endHunterNo(){
        HunterEvent.setVisible(false);
        Event.setVisible(false);
        game.HunterDecisionNo(game.getHunterJumpPoint());
    }
    void initTest(){
        initCupidEvent();
    }


}
