package exercises.AlexChat.chat;

import exercises.AlexChat.controller.ChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author Lukas Allermann */

public class Chat extends Application {



    private ChatController chatController;
    public ChatClient chatClient;

    public ChatController getChatController() {
        return chatController;
    }
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
        chatClient = new ChatClient(username,2001);
        chatClient.setChat(this);
        chatClient.init();
        new Thread(chatClient).start();
    }

    private String Username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();

        try {
            loader.setLocation(new File("src/main/java/AlexChat/view/ChatWindow.fxml").toURI().toURL());
            Parent root = loader.load();
            chatController = loader.getController();
            chatController.setChat(this);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
