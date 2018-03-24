package chat;

import javafx.application.Platform;
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
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends GridPane implements Initializable {
    @FXML private TextField loginName;
    @FXML private PasswordField loginPassword;
    @FXML private Text loginMessage;
    @FXML private Button loginButton;

    private Stage stage;

    public boolean isNeedExit() {
        return needExit;
    }

    public void setNeedExit(boolean needExit) {
        this.needExit = needExit;
    }

    private boolean needExit = false;

    public static LoginController makeDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/fxml/loginForm.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load()));
        LoginController controller = fxmlLoader.getController();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        controller.setStage(stage);
        stage.setOnCloseRequest((event) -> {
            // говорим, что хотим выйти из приложения
            controller.setNeedExit(true);
        });
        return controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // логин - пароль по-умолчанию
        loginName.setText("dserov");
        loginPassword.setText("dserov");
        loginButton.setOnAction((event) -> stage.close());
    }

    public String getLoginName() {
        return loginName.getText();
    }

    public String getLoginPassword() {
        return loginPassword.getText();
    }

    /**
     * отображаем логинскрин с заданным сообщением и ждем нажатия "Enter"
     */
    public void showAndWait(String message) {
        loginMessage.setText(message);
        stage.showAndWait();
    }
}
