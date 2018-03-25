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

import java.io.IOException;


/**
 * Точка входа клиента
 *
 * @author DSerov
 * @version dated March 16, 2018
 */
public class Client extends Application {
    private ChatController mainForm;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatForm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 300);
        mainForm = loader.getController();
        primaryStage.setScene(scene);
        primaryStage.setOnShown((event) -> {
            mainForm.setPrimaryStage(primaryStage);
            mainForm.initConnection();
        });

        // при закрытии окна
        primaryStage.setOnCloseRequest((event -> mainForm.closeConnectionAndExit()));

        primaryStage.show();
    }
}
