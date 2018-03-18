package client.loginForm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController extends GridPane {
    @FXML private TextField loginName;
    @FXML private PasswordField loginPassword;
    @FXML private Text loginMessage;

    @FXML protected void loginAction(ActionEvent event) {
        loginMessage.setText("Enter button pressed");
    }

    public LoginController() throws IOException {
        FXMLLoader loader = FXMLLoader.load(getClass().getResource("/login.fxml"));
        loader.setController(this);
        loader.setRoot(this);
    }

    public void show() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this));
        stage.show();
    }
}

