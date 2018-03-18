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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Компонент для отображения исключения
 * Идея отсюда - https://stackoverflow.com/questions/42443971/javafx-creating-custom-dialogs-using-fxml
 *
 * @author DSerov
 * @version dated March 16, 2018
 */

public class ExceptionPane extends BorderPane {

    private ObjectProperty<Exception> exception = new SimpleObjectProperty<>();

    public ObjectProperty<Exception> exceptionProperty() {
        return exception ;
    }

    public final Exception getException() {
        return exceptionProperty().get();
    }

    public final void setException(Exception exception) {
        exceptionProperty().set(exception);
    }

    @FXML
    private TextArea stackTrace;

    @FXML
    private Label message;

    public ExceptionPane() throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("exceptionForm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        exception.addListener((obs, oldException, newException) -> {
            if (newException == null) {
                message.setText(null);
                stackTrace.setText(null);
            } else {
                message.setText(newException.getMessage());
                StringWriter sw = new StringWriter();
                newException.printStackTrace(new PrintWriter(sw));
                stackTrace.setText(sw.toString());
            }
        });
    }
}
