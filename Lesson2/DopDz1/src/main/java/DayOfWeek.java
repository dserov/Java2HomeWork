public enum DayOfWeek {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    // номер по порядку
    private int npp;

    DayOfWeek(int npp) {
        this.npp = npp;
    }

    // дает номер по порядку
    public int getNpp() {
        return npp;
    }
}
