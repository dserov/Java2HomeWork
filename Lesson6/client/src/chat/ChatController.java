/*
 * Copyright (C) 2018 geekbrains homework lesson5
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chat;

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Логика клиентского чата
 *
 * @author DSerov
 * @version dated March 16, 2018
 */
public class ChatController implements Initializable, TCPConnectionListener {
    @FXML
    TextField msgField;

    @FXML
    TextArea textChat;

    @FXML
    ListView<String> usersList;

    final private String SERVER_ADDRESS = "localhost";
    final private int SERVER_PORT = 8189;
    private Stage primaryStage;
    private TCPConnection connection;
    private LoginController loginController;
    final private StringProperty nickName = new SimpleStringProperty();
    final private String DEFAULT_TITLE = "My chat";

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // true, если подключен и авторизован. логин прячем, нормальная работа. иначе показываем логин скрин
    private volatile boolean isAuthenticated = false;

    @FXML
    private void showSmile(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Не реализовано", ButtonType.OK);
        alert.show();
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        msgField.requestFocus();

        String msg = msgField.getText();
        msgField.clear();

        if (msg.length() == 0) return;

        if (connection == null) {
            initConnection("");
            return;
        }

        connection.sendString(msg);
    }

    public TextField getMsgField() {
        return msgField;
    }

    public void setMsgField(TextField msgField) {
        this.msgField = msgField;
    }

    public TextArea getTextChat() {
        return textChat;
    }

    public void setTextChat(TextArea textChat) {
        this.textChat = textChat;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> names = FXCollections.observableArrayList(
                "Маша", "Наташа", "Иван", "Настя", "Вася", "Света", "Андрей");
        usersList.setItems(names);

        usersList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new UserCell();
            }
        });

        // Сделаем замену заголовка при смене ника
        nickName.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(()-> {
                    if (newValue.length() != 0)
                        getPrimaryStage().setTitle(newValue);
                    else
                        getPrimaryStage().setTitle("My chat");
                });
            }
        });
        nickName.setValue(DEFAULT_TITLE);

        try {
            // создадим в памяти диалог для ввода логина - пароля
            loginController = LoginController.makeDialog();
        } catch (IOException e) {
            // не создался диалог - повод для паники
            throw new RuntimeException(e);
        }
    }

    public void initConnection(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String msg = message;
                // никуда не идем, пока не законнектимся
                while (!isAuthenticated) {
                    System.out.println("init connection");
                    loginController.showAndWait(msg); // тут подвисаем, ждем закрытия окна

                    // может, мы выйти хотим?
                    if(loginController.isNeedExit()) {
                        closeConnectionAndExit();
                        break;
                    }

                    if (connection != null) {
                        String authCmd = "/auth " + loginController.getLoginName() + " " + loginController.getLoginPassword();
                        connection.sendString(authCmd);
                        return;
                    }
                    // запуск соединения
                    try {
                        connection = new TCPConnection(ChatController.this, new Socket(SERVER_ADDRESS, SERVER_PORT));

                        // сразу авторизуемся
                        String authCmd = "/auth " + loginController.getLoginName() + " " + loginController.getLoginPassword();
                        connection.sendString(authCmd);
                        return;
                    } catch (IOException e) {
                        // тут главное не терять надежды, а попробовать еще раз
                        msg = e.getMessage();
                        System.out.println("initConnectionException: " + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        if (value == null) return;
        if (value.equals("")) return;
        System.out.println(value);

        String parts[] = value.split(" ");
        String cmd = parts[0];
        if (cmd.equals("/authok")) {
            // авторизовался успешно
            isAuthenticated = true;
            textChat.appendText("Подключились к серверу.\r\n");
            // мне мой ник прислали
            System.out.print("Мой ник: ");
            if (parts.length == 2) {
                System.out.print(parts[1]);
                nickName.setValue(DEFAULT_TITLE + " - " + parts[1]);
            }
            System.out.println("");
            return;
        }
        if (cmd.equals("/autherr")) {
            // деавторизация
            nickName.setValue(DEFAULT_TITLE);
            isAuthenticated = false;
            initConnection(value.substring("/autherr".length()));
            return;
        }

        if (cmd.equals("/begin")) {
            // подключился новый клиент
            System.out.print("Новый клиент: ");
            if (parts.length == 2)
                System.out.print(parts[1]);
            System.out.println("");
            return;
        }

        textChat.appendText(value + "\r\n");
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        tcpConnection.sendString("/end");
        connection = null;
        isAuthenticated = false;
        nickName.setValue("");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

    }

    public void closeConnectionAndExit() {
        if (connection != null)
            connection.disconnect();
        Platform.exit();
    }
}

class UserCell extends ListCell<String> {
    final private Icon icon = new Icon(AwesomeIcon.USER, "1em", "", "");

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setGraphic(icon);
            setText(item);
        } else {
            setGraphic(null);
            setText("");
        }
    }
}
