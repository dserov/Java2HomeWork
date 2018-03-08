import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntryPoint {
    public static void main(String[] args) {
        System.out.println("----------------- Домашнее задание, часть 1 ------------------");

        // take text
        String text = "В Судаке Вы не будете искать сравнения с другими курортами - настолько он своеобразен. Те, кто приезжают сюда из года в год, на протяжении десятилетий, наверное, тоже не смогут объяснить на словах и даже с помощью видео - в чем его притягательность. Судак необходимо увидеть, почувствовать, ощутить лично. И этого ощущения Вы не забудете никогда и точно так же, как те, от кого Вы когда-то впервые услышали про этот город курорт, не сможете объяснить, в чем его притягательность. Как курорт, город Судак привлекает гостей уже более 100 лет. Сохранив и сегодня во многом прелести тихого курорта, с отдыхом в увитых виноградом легких коттеджах, в последние годы Судакский курорт резко прорвался к самому современному уровню предоставляемых здесь летом аттракционов, развлечений и услуг.";

        // make array
        String arrText[] = text.split(" ");

        // count words
        HashMap<String, Integer> hm = new HashMap<>();
        for (String key : arrText)
            hm.merge(key, 1, (currValue, value) -> currValue + value);

        // print results
//        for (HashMap.Entry<String, Integer> item : hm.entrySet())
//            System.out.println(item);
        hm.forEach((item, count) -> System.out.println(item + "(" + count + ")"));

        System.out.println("----------------- Домашнее задание, часть 2 ------------------");
        Phones addrbook = new Phones();
        addrbook.add("Ivanov", "+79781234567");
        addrbook.add("Petrov", "+79782345678");
        addrbook.add("Petrov", "+79783456789");
        addrbook.add("Petrov", "+79784567890");

        System.out.println("Sidorov = " + addrbook.get("Sidorov"));
        System.out.println("Ivanov  = " + addrbook.get("Ivanov"));
        System.out.println("Petrov  = " + addrbook.get("Petrov"));
    }
}

class Phones {
    private Map<String, ArrayList<String>> storage = new HashMap<>();

    public void add(String family, String phone) {
        ArrayList<String> phoneList = storage.computeIfAbsent(family, s -> new ArrayList<>());
        phoneList.add(phone);
    }

    public ArrayList<String> get(String family) {
        return storage.get(family);
    }
}

