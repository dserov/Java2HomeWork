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

import Interfaces.ICompetitor;

/**
 * Команда. Имеет имя, содержит в себе список персонажей
 *
 * @author DSerov
 * @version dated 03 Mar, 2018
 */
public class Team {
    private String mName;
    private ICompetitor[] mCompetitors;

    Team(String name, ICompetitor... competitors) {
        mName = name;
        mCompetitors = competitors;
    }

    /**
     * Прогоняем персонажей данной команды через данную полосу препятствий
     *
     * @param cource полоса препятствий
     */
    public void doIt(Cource cource) {
        for (ICompetitor c : mCompetitors) {
            cource.testIt(c);
        }
    }

    /**
     * Вывод информации о персонажах данной команды и не сошел ли с дистанции
     */
    public void info() {
        for (ICompetitor c : mCompetitors) c.info();
    }

    /**
     * Проша ли команда дистанцию ?
     */
    public void isAllOnDistance() {
        boolean allOnDistance = true;
        for (ICompetitor c : mCompetitors) allOnDistance &= c.isOnDistance();
        System.out.println("Команда '" + mName + "' осталась на дистанции - " + allOnDistance);
    }
}
