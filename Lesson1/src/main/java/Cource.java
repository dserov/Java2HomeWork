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
import Interfaces.IObstacle;

/**
 * Полоса препятсвий. Состоит из объектов типа стена, вода, кросс
 *
 * @author DSerov
 * @version dated 03 Mar, 2018
 */
public class Cource {
    private IObstacle[] mObstacles;

    Cource(IObstacle...mObstacles) {
        this.mObstacles = mObstacles;
    }

    /**
     * прогоняем данного персонажа по всей полосе препятствий
     *
     * @param competitor персонаж, прогоняемый через данную полосу препятствий
     */
    public void testIt(ICompetitor competitor) {
        for (IObstacle o : mObstacles) {
            o.doIt(competitor);
            if (!competitor.isOnDistance()) break; // сошел с дистанции
        }
    }
}
