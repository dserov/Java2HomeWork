package client.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends GridPane implements Initializable {
    @FXML private TextField loginName;
    @FXML private PasswordField loginPassword;
    @FXML private Text loginMessage;
    @FXML private Button loginButton;

    public static LoginController makeDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/loginForm.fxml"));
        LoginController controller = fxmlLoader.getController();
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // логин - пароль по-умолчанию
        loginName.setText("dserov");
        loginPassword.setText("dserov");

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loginMessage.setText("Enter button pressed");
            }
        });
    }
}
