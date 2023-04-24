package pe.gob.essalud.apps.common.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String timeString) throws Exception {
        if (timeString != null) return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME);
        else return null;
    }

    @Override
    public String marshal(LocalTime time) throws Exception {
        if (time != null) return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
        else return null;

    }
}