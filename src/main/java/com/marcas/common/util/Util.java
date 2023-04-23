package com.marcas.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcas.common.constants.Constantes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class Util {

    private static final Logger logger = LogManager.getLogger(Util.class);

    Util() {
        super();
    }

    public static String addOneDay(String date) {
        String nextDate = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
            LocalDate localDate = LocalDate.parse(date, formatter).plusMonths(1);
            String dd = String.valueOf(localDate.getDayOfMonth());
            dd = dd.length() == 1 ? "0" + dd : dd;
            String mm = String.valueOf(localDate.getMonthValue());
            mm = mm.length() == 1 ? "0" + mm : mm;
            String yy = String.valueOf(localDate.getYear());

            nextDate = dd + "/" + mm + "/" + yy;
        } catch (Exception e) {
            logger.info(e);
        }
        return nextDate;
    }

    public static Double getNumberFormat(Double number) {
        Double outNumber = 0.0;
        DecimalFormat df2;
        try {
            Locale currentLocale = Locale.getDefault();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
            otherSymbols.setDecimalSeparator('.');
            df2 = new DecimalFormat(".##", otherSymbols);
            outNumber = Double.parseDouble(df2.format(number));
        } catch (Exception e) {
            logger.info(e);
        }
        return outNumber;
    }

    public static Integer integerTryParse(String obj) {
        Integer retVal = null;
        try {
            retVal = Integer.parseInt(obj);
        } catch (NumberFormatException nfe) {
            logger.error(nfe.getMessage());
            retVal = null;
        }
        return retVal;
    }

    public static Double doubleTryParse(String obj) {
        Double value = null;
        try {
            value = Double.parseDouble(obj);
        } catch (NumberFormatException nfe) {
            logger.error(nfe.getMessage());
            value = null;
        }
        return value;
    }

    public static String mapTojson(Map<?, ?> mapa) {
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return mapperObj.writeValueAsString(mapa);
        } catch (JsonProcessingException e) {
            logger.info(e);
            return null;
        }
    }

    public static String objectToJson(Object o) {
        String jsonInString = null;
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapperObj.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
            mapperObj.registerModule(new JavaTimeModule());
            jsonInString = mapperObj.writeValueAsString(o);
        } catch (Exception e) {
            logger.info(e);
        }
        return jsonInString;
    }

    public static <T> T mapToObject(Class<T> type, Map<?, ?> mapa) {
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String jsonString = mapTojson(mapa);
            return mapperObj.readValue(jsonString, type);
        } catch (Exception e) {
            logger.info(e);
            return null;
        }
    }

    public static <T> T objectToObject(Class<T> type, Object o) {
        try {
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.registerModule(new JavaTimeModule());
            mapperObj.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapperObj.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
            mapperObj.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
            String jsonString = objectToJson(o);
            return mapperObj.readValue(jsonString, type);
        } catch (Exception e) {
            logger.info(e);
            return null;
        }
    }

    public static <T> List<T> listObjectToListObject(Class<T> type, Iterable<?> inList) {
        ArrayList<T> outList = new ArrayList<>();
        try {
            for (Object o : inList) {
                T t = objectToObject(type, o);
                outList.add(t);
            }
        } catch (Exception e) {
            logger.info(e);
        }
        return outList;
    }

    public static <T> T stringToObject(Class<T> type, String str) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(str, type);
        } catch (Exception e) {
            logger.info(e);
            return null;
        }
    }

    public static <T> List<T> listMapToListObject(Class<T> type, List<?> list) {
        List<T> lista = new ArrayList<>();
        try {
            if (list == null) {
                return lista;
            }
            ObjectMapper mapperObj = new ObjectMapper();
            mapperObj.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
            for (int i = 0; i < list.size(); i++) {
                Map<?, ?> m = (Map<?, ?>) list.get(i);
                T t = Util.mapToObject(type, m);
                lista.add(t);
            }
        } catch (Exception e) {
            logger.info(e);
        }
        return lista;
    }

    public static Boolean isNull(Object type) {
        Boolean bol = false;
        try {
            Field[] fields = type.getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.isSynthetic()) {
                    continue;
                }
                Object value = f.get(type);
                if (value == null) {
                    return false;
                }
            }
            bol = true;
        } catch (Exception e) {
            bol = false;
        }
        return bol;
    }

    public static Double getDoubleRandom(Double rangeMin, Double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public static <T> List<T> getListFromMapObject(Object obj, Class<T> c) {
        List<T> lista = new ArrayList<>();
        try {
            if (obj != null) {
                if (obj instanceof List<?>) {
                    lista = Util.listMapToListObject(c, (ArrayList<?>) obj);
                } else {
                    lista.add(Util.mapToObject(c, (Map<?, ?>) obj));
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }
        return lista;
    }

    public static String generateRandomSring() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public static String urlEncoder(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }


    public static String obtenerFechaString(Date fecha, String formato) {
        DateFormat df = new SimpleDateFormat(formato);
        Date timeDate = Calendar.getInstance().getTime();
        return df.format(timeDate);
    }

    public static Timestamp obtenerFechaDateTime(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA_LARGA);
        Timestamp dateFecha = null;
        String fechaNew = fecha;
        try {
            if (fecha.length() == 10) {
                fechaNew = fechaNew.concat(Constantes.FORMATO_HORA_MIN);
            }
            LocalDateTime formatDateTime = LocalDateTime.parse(fechaNew, formatter);
            dateFecha = Timestamp.from(formatDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            dateFecha = null;
        }
        return dateFecha;
    }

    public static Date obtenerFechaDate(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA_LARGA);
        String fechaNew = fecha;
        Date dateFecha = null;
        try {
            if (fecha.length() == 10) {
                fechaNew = fechaNew.concat(Constantes.FORMATO_HORA_MIN);
            }
            LocalDateTime formatDate = LocalDateTime.parse(fechaNew, formatter);
            dateFecha = Date.from(formatDate.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            dateFecha = null;
        }
        return dateFecha;
    }

    public static Optional<Object> getOptional(Object o) {
        return Optional.of(o);
    }

    public static void copyPropertiesValuesChanged(Object source, Object target) {
        final BeanWrapperImpl src = new BeanWrapperImpl(source);
        final BeanWrapperImpl trg = new BeanWrapperImpl(target);

        @SuppressWarnings("unchecked")
        List<String> srcPropertiesUpdate = ((List<String>) src.getPropertyValue("propertiesUpdate"));

        if (!CollectionUtils.isEmpty(srcPropertiesUpdate)) {
            for (String property : srcPropertiesUpdate) {
                boolean isSrcReadable = src.isReadableProperty(property);
                // TODO: Refactorizar,
                Object srcValue = null;
                if (isSrcReadable)
                    srcValue = src.getPropertyValue(property);
                if (isSrcReadable && trg.isWritableProperty(property))
                    trg.setPropertyValue(property, srcValue);

            }
        }
    }

    public static void copyProperties(Object source, Object target) throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(source, target);
    }
    
    public static String setFile(String ruta,String ext,String base64)
    {
        File path= new File(ruta);
        if(!path.exists())
        {
            path.mkdirs();
        }

        try  {
            if(base64.contains(","))
            {
                String[] split=base64.split(",");
                base64=split[1];
            }
            byte[] data = Base64.getDecoder().decode(base64);
            String nombre=""+(new Date().getTime())+"."+ext;
            OutputStream stream = new FileOutputStream(ruta+nombre);
            stream.write(data);
            stream.flush();
            stream.close();
            return nombre;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    

    public static String getBase64(String ruta, String archivo) {
        String base64 = "";

        String fileLocation = new File(ruta).getAbsolutePath() + File.separator + archivo;

        File remoteFile = new File(fileLocation);
        if (remoteFile.exists()) {
            try {
                byte[] image = Files.readAllBytes(remoteFile.toPath());
                if (image != null) {
                    base64 = javax.xml.bind.DatatypeConverter.printBase64Binary(image);
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
            }
        }
        return base64;
    }

}