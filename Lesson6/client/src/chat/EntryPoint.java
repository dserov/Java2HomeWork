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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Точка входа клиента
 *
 * @author DSerov
 * @version dated March 16, 2018
 */
public class EntryPoint extends Application {
    LoginController loginController;
    ChatController chatController;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatForm.fxml"));
        Parent root = loader.load();
        chatController = loader.getController();
        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Chat");
        primaryStage.show();

        // make login dialog
        loginController = LoginController.makeDialog();
        loginController.getStage().show();

        new Thread(new Runnable() {
            final private String SERVER_ADDRESS = "localhost";
            final private int SERVER_PORT = 8189;
            private Socket socket = null;
            private DataInputStream inputStream = null;
            private DataOutputStream outputStream = null;

            @Override
            public void run() {
                try {
                    // Попытка подключиться к серверу
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

                    // мапим дата стримы
                    inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = new DataOutputStream(socket.getOutputStream());

                    // основной поток, обслуживающий сообщения
                    try {
                        while (!socket.isClosed()) {
                            // пришло с сервера
                            String message = inputStream.readUTF();
                            chatController.getTextChat().appendText(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    //chatController.getTextChat().appendText(e.getMessage());
                    return;
                }

                try {
                    if (inputStream != null) inputStream.close();
                } catch (IOException e) {
                    chatController.getTextChat().appendText(e.toString());
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
        }).start();
    }
}
