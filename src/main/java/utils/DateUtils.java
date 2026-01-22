package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public final class DateUtils {
    private DateUtils() {}

    public static final DateTimeFormatter UI_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate parseUi(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return LocalDate.parse(s, UI_FMT); } catch (DateTimeParseException e) { return null; }
    }

    public static String formatUi(LocalDate d) {
        if (d == null) return null;
        return d.format(UI_FMT);
    }

    public static long exclusiveDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) return -1;
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long inclusiveDays(LocalDate start, LocalDate end) {
        long diff = exclusiveDays(start, end);
        return diff < 0 ? diff : diff + 1;
    }

    public static LocalDate todayPlusDays(int days) {
        return LocalDate.now().plusDays(days);
    }
}
