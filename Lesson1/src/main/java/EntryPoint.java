/*
 * Copyright (C) 2018 geekbrains homework lesson1
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

import Animals.Cat;
import Animals.Dog;
import Animals.Human;
import Obstacles.Cross;
import Obstacles.Water;
import Obstacles.Wall;

/**
 * Создаем две команды, полосу препятствий. Прогоняем команды по полосе и выводим результаты.
 *
 * @author DSerov
 * @version dated 03 Mar, 2018
 */
public class EntryPoint {
    public static void main(String[] args) {
        // команды
        Team team1 = new Team("Городские", new Cat("Vaska"), new Dog("Tuzik"), new Human("Vasya", 200, 100, 1));
        Team team2 = new Team("Деревенские", new Cat("Barsik"), new Dog("Bobik"), new Human("Petya", 300, 200, 2));

        // полоса препятствий
        Cource cource = new Cource(new Wall(1), new Water(5), new Cross(200), new Wall(1));

        // поехали
        System.out.println("------------------------ ГО! ----------------------------");
        team1.doIt(cource);
        team2.doIt(cource);

        // результаты
        System.out.println("------------------- результаты --------------------------");
        team1.info();
        team2.info();
        System.out.println("------------------- результаты команды ------------------");
        team1.isAllOnDistance();
        team2.isAllOnDistance();
    }
}
