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

package Animals;

import Interfaces.ICompetitor;

/**
 * класс жЫвотное, реализует интерфейс конкурента
 *
 * @author DSerov
 * @version dated 03 Mar, 2018
 */
public class Animal implements ICompetitor {
    private TypeAnimal typeAnimal;
    private String name;

    private int maxRunDistance;
    private int maxSwimDistance;
    private int maxJumpHeihgt;

    private boolean onDistance = true;

    Animal(TypeAnimal typeAnimal, String name, int maxRunDistance,
                  int maxSwimDistance, int maxJumpHeight) {
        this.typeAnimal = typeAnimal;
        this.name = name;
        this.maxRunDistance = maxRunDistance;
        this.maxSwimDistance = maxSwimDistance;
        this.maxJumpHeihgt = maxJumpHeight;
    }

    public void run(int distance) {
        if (distance <= maxRunDistance) {
            System.out.println(typeAnimal + " " + name + " справился с кроссом");
        } else {
            System.out.println(typeAnimal + " " + name + " не справился с кроссом");
            onDistance = false;
        }
    }

    public void swim(int distance) {
        if (distance <= maxSwimDistance) {
            System.out.println(typeAnimal + " " + name + " справился с плаванием");
        } else {
            System.out.println(typeAnimal + " " + name + " не справился с плаванием");
            onDistance = false;
        }
    }

    public void jump(int height) {
        if (height <= maxRunDistance) {
            System.out.println(typeAnimal + " " + name + " справился с прыжком");
        } else {
            System.out.println(typeAnimal + " " + name + " не справился с прыжком");
            onDistance = false;
        }
    }

    public boolean isOnDistance() {
        return onDistance;
    }

    public void info() {
        System.out.println(typeAnimal + " " + name + " " + onDistance);
    }
}
