/*
 * Copyright (C) 2018 dserov
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

package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Пример окошка для отображения исключения
 * идея отсюда - https://stackoverflow.com/questions/42443971/javafx-creating-custom-dialogs-using-fxml
 *
 * @author DSerov
 * @version dated March 18, 2018
 */

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
