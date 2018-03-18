package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Exception dialog example");
        try {
            int a = 1 / 0;
        } catch (ArithmeticException exc) {
            ExceptionPane pane = new ExceptionPane();
            pane.setException(exc);
            primaryStage.setScene(new Scene(pane, 300, 275));
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
