/*
 * Copyright (C) 2018 geekbrains homework
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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
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
    private TCPConnection connection = null;
    private LoginController loginController;
    private String lastLoginMessage = "";
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

        System.out.println("sendMessage, " + connection);
        if (connection == null) {
            lastLoginMessage = "Нет соединения с сервером";
//            initConnection();
            return;
        }

        connection.sendString(msg);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                Platform.runLater(() -> {
                    getPrimaryStage().setTitle(DEFAULT_TITLE + (
                            newValue.isEmpty()
                                    ? ""
                                    : " - " + newValue));

                    // нет имени - нет аутентификации
                    isAuthenticated = !newValue.isEmpty();
                });
            }
        });
        nickName.setValue("");

        try {
            // создадим в памяти диалог для ввода логина - пароля
            loginController = LoginController.makeDialog();
        } catch (IOException e) {
            // не создался диалог - повод для паники
            throw new RuntimeException(e);
        }
    }

    public void initConnection() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("init connection" + connection);
                System.out.println("nickanameIsEmpty: " + nickName.getValue().isEmpty());
                textChat.appendText(lastLoginMessage + "\r\n");
                usersList.setItems(FXCollections.observableArrayList());
                nickName.setValue("");
                while (connection == null || nickName.getValue().isEmpty()) {
                    // тут подвисаем, ждем закрытия окна
                    loginController.showAndWait(lastLoginMessage);

                    // может, мы выйти хотим?
                    if (loginController.isNeedExit()) {
                        closeConnectionAndExit();
                        return;
                    }

                    // запуск соединения
                    try {
                        if (connection == null)
                            connection = new TCPConnection(ChatController.this, new Socket(SERVER_ADDRESS, SERVER_PORT));

                        // сразу авторизуемся
                        if (connection != null && nickName.getValue().isEmpty()) {
                            String authCmd = "/auth " + loginController.getLoginName() + " " + loginController.getLoginPassword();
                            connection.sendString(authCmd);
                            return;
                        }
                    } catch (IOException e) {
                        // тут главное не терять надежды, а попробовать еще раз
                        lastLoginMessage = e.getMessage();
                        System.out.println("initConnectionException: " + e.getMessage());
                    }
                } // end while
            }
        });
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        System.out.println("onReceiveString, " + tcpConnection);
        System.out.println("onReceiveString, value: " + value);
        if (value == null) throw new RuntimeException("value == null");
        if (value == null) return;
        if (value.equals("")) return;

        String parts[] = value.split(" ");
        String cmd = parts[0];
        String otherValue = value.replace(cmd, "");
        otherValue = otherValue.trim();

        if (cmd.equals("/authok")) {
            // авторизовался успешно
            textChat.appendText("Подключились к серверу.\r\n");

            // мне мой ник прислали
            nickName.setValue(otherValue);
            return;
        }

        if (cmd.equals("/autherr")) {
            // деавторизация
            lastLoginMessage = otherValue;
            initConnection();
            return;
        }

        if (cmd.equals("/clientlist")) {
            // изменился список контактов
            System.out.println("Новый клиент:" + otherValue);
            updateContactList(otherValue);
            return;
        }

        if (cmd.equals("/end")) {
            tcpConnection.disconnect();
            lastLoginMessage = "Сервер деавторизовал соединение";
            initConnection();
            return;
        }

        textChat.appendText(value + "\r\n");
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("onDisconnect, " + tcpConnection);
//        tcpConnection.sendString("/end");
        connection = null;
        nickName.setValue("");
//        initConnection();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("onException, " + tcpConnection + e);
    }

    private void updateContactList(String contactString) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> names = FXCollections.observableArrayList(contactString.split(","));
                usersList.setItems(names);
                usersList.refresh();
            }
        });
    }

    public void closeConnectionAndExit() {
        if (connection != null) {
            connection.sendString("/end");
            connection.disconnect();
        }
        Platform.exit();
    }

    @FXML
    private void banUser(ActionEvent event) {
        System.out.println(event);
        System.out.println("Selected item: " + usersList.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void unbanUser(ActionEvent event) {
        System.out.println(event);
    }
}

