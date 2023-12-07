package pe.gob.essalud.apps.common.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static int calculateAge(Date pBirthDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = pBirthDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        if (birthDate != null) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public static String format(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static String format(LocalDate date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public static String format(LocalDateTime date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public static String iso(LocalDateTime date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String isoHour(LocalDateTime date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return date.format(formatter);
    }

    public static String fullFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static String formatDateTime(LocalDateTime date) {
        if (date == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }

    public static LocalDateTime datetimeFromTime(LocalTime localTime) {
        LocalDateTime localDateTime = null;
        if (localTime != null)
            localDateTime = localTime.atDate(LocalDate.now());
        return localDateTime;
    }

    public static LocalDate StringToLocalDateTime(String fecha) {
        if (fecha == null && fecha.trim().length() == 0)
            return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(fecha, formatter);

        } catch (Exception e) {
        }

        return null;
    }

    public static int getYearCurrent() {
        LocalDate fechaActualTmp = LocalDate.now();
        return fechaActualTmp.getYear();
    }

    public static String getMonthString(LocalDateTime date) {
        Month mes = date.now().getMonth();
        return  mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

}
