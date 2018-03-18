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

package client;

import client.loginForm.LoginController;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
public class ChatController implements Initializable {
    @FXML
    TextField msgField;

    @FXML
    TextArea textChat;

    @FXML
    ListView<String> usersList;

    final private String SERVER_ADDRESS = "localhost";
    final private int SERVER_PORT = 8189;
    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    @FXML
    private void showSmile(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Не реализовано", ButtonType.OK);
        alert.show();
    }
    /**
     * Отображение даты и времени в окне чата
     */
    @FXML
    private void sendMessage(ActionEvent event) {
        msgField.requestFocus();

        String msg = msgField.getText();
        msgField.clear();

        if (msg.length() == 0) return;

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
        String dt = localDateTime.format(dateTimeFormatter);
        msg = dt + ": " + msg;

        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            textChat.appendText("Отправить не удалось!");
            e.printStackTrace();
        }
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

        try {
            // Попытка подключиться к серверу
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            // мапим дата стримы
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // основной поток, обслуживающий сообщения
                    try {
                        while (!socket.isClosed()) {
                            // пришло с сервера
                            String message = inputStream.readUTF();
                            textChat.appendText(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            textChat.appendText(e.getMessage());
            return;
        }

        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            textChat.appendText(e.toString());
            e.printStackTrace();
        }
        try {
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class UserCell extends ListCell<String> {
        final Icon icon = new Icon(AwesomeIcon.USER, "1em", "", "");

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
}
