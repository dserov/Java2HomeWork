import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class EntryPoint extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene scene = new Scene(root, 600, 300);
//        scene.getStylesheets().add((getClass().getResource("/css/style.css").toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Chat");
        primaryStage.show();

        ListView<String> users_list = (ListView) scene.lookup("#users_list");
        ObservableList<String> names = FXCollections.observableArrayList(
         "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        users_list.setItems(names);
    }
}
