public class DayOfWeekMain {
    public static void main(String[] args) {
        System.out.println(getWorkingHours(DayOfWeek.MONDAY));
    }

    private static int getWorkingHours(DayOfWeek dayOfWeek) {
        // примем, что рабочих часов в день - 8
        // 5 - количество рабочих дней с начала недели
        int days = Math.max(0, 5 - dayOfWeek.getNpp()); // не должен стать отрицательным
        return days * 8;
    }
}
