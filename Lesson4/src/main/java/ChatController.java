import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ChatController {
    @FXML
    TextField msgField;

    @FXML
    TextArea textChat;

    @FXML
    ListView<String> usersList;

    /**
     * Отображение даты и временив окне чата
     *
     */
    @FXML
    public void sendMessage(ActionEvent event) {
        String msg = msgField.getText();
        if (msg.length() == 0) return;

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
        String dt = localDateTime.format(dateTimeFormatter);
        textChat.appendText(dt + ": " + msg + "\n");
        msgField.clear();
    }

    @FXML
    public void initialize() {
        ObservableList<String> names = FXCollections.observableArrayList(
                "Маша", "Наташа", "Иван", "Настя", "Вася", "Света", "Андрей");
        usersList.setItems(names);

        usersList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new UserCell();
            }
        });
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
