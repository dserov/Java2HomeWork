Java 2

Домашнее задание 3

1 Создать массив с набором слов (10-20 слов, должны встречаться повторяющиеся). Найти и
вывести список уникальных слов, из которых состоит массив (дубликаты не считаем).
Посчитать, сколько раз встречается каждое слово.

2 Написать простой класс Телефонный Справочник, который хранит в себе список фамилий и
телефонных номеров. В этот телефонный справочник с помощью метода add() можно
добавлять записи, а с помощью метода get() искать номер телефона по фамилии. Следует
учесть, что под одной фамилией может быть несколько телефонов (в случае
однофамильцев), тогда при запросе такой фамилии должны выводиться все телефоны.


Дополнительное задание
1. Замерить время добавления, поиска и удаления объектов из коллекции в LinkedList, ArrayList, TreeSet, HashSet.
2. Сравнить время и сделать вывод о том, в каких условиях какая коллекция работает быстрее.

Результат для элементов типа "строка":
Add 10000 elements to LinkedList -   0s   0ms 691mcs 291ns
Add 10000 elements to ArrayList  -   0s   0ms 944mcs 562ns
Add 10000 elements to TreeSet    -   0s  16ms 678mcs 807ns
Add 10000 elements to HashSet    -   0s   3ms 225mcs 518ns
Search 1000 elements in LinkedList -   0s  43ms 975mcs 708ns
Search 1000 elements in ArrayList  -   0s  42ms 804mcs 139ns
Search 1000 elements in TreeSet    -   0s   0ms 953mcs 920ns
Search 1000 elements in HashSet    -   0s   1ms 374mcs 429ns
Remove 1000 elements from LinkedList -   0s  33ms 462mcs 364ns
Remove 1000 elements from ArrayList  -   0s  37ms 902mcs 318ns
Remove 1000 elements from TreeSet    -   0s   3ms 242mcs 724ns
Remove 1000 elements from HashSet    -   0s   0ms 622mcs 765ns




Сборка и компиляция проекта (сборка jar-файла) :
```
mvn package
```
Запуск
```
java -jar MainDz\MainDz.jar
java -jar DopDz1\DopDz1.jar
```
