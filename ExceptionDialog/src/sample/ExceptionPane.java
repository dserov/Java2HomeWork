package sample;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionPane extends BorderPane {

    private ObjectProperty<Exception> exception;

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
//        loader.setController(this);
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
