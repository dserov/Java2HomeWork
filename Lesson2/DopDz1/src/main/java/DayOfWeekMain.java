package DopDz1;

public class DayOfWeekMain {
    public static void main(String[] args) {
        System.out.println(getWorkingHours(DayOfWeek.MONDAY));
    }

    private static int getWorkingHours(DayOfWeek dayOfWeek) {
        // примем, что рабочих часов в день - 8, и выходных нету...
        return (7 - dayOfWeek.getNpp()) * 8;
    }
}
