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

import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.scene.control.ListCell;

/**
 * Задает правила отрисовки строк контакт-листа
 * Для упрощения, к забаненым никам спереди будем добавлять "!", типа служебный символ.
 * Не хочется со стрингов перелезать на какую-то более сложную структуру.
 *
 * @author DSerov
 * @version dated March 25, 2018
 */

public class UserCell extends ListCell<String> {
    final private Icon icon_user = new Icon(AwesomeIcon.USER, "1em", "", "");
    final private Icon icon_ban = new Icon(AwesomeIcon.BAN, "1em", "", "");

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            if (item.length() > 0 && item.charAt(0) == '!') {
                setGraphic(icon_ban);
                setText(item.substring(1));
            }
            else {
                setGraphic(icon_user);
                setText(item);
            }
        } else {
            setGraphic(null);
            setText("");
        }
    }
}