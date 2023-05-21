package pe.gob.essalud.apps.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UploadUtil {

    public Supplier<Stream<Row>> getRowStreamSupplier(Iterable<Row> rows) {
        return () -> getStream(rows);
    }

    public <T> Stream<T> getStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public Supplier<Stream<Integer>> cellIteratorSupplier(int end) {
        return () -> numberStream(end);
    }

    public Stream<Integer> numberStream(int end) {
        return IntStream.range(0, end).boxed();
    }

    public static String saveFileBase64(String filePath, String fileBase64) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(fileBase64);
            FileUtils.writeByteArrayToFile(new File(filePath), decodedBytes);
        } catch (IOException e) {
            filePath = "";
            e.printStackTrace();
        }
        return filePath;
    }

    public static String getFileBase64(String filePath) {
        String fileBase64;
        try {
            Path path = Path.of(filePath);
            byte[] fileBytes = Files.readAllBytes(path);
            fileBase64 = Base64.getEncoder().encodeToString(fileBytes);;
        } catch (IOException e) {
            fileBase64 = "";
            e.printStackTrace();
        }
        return fileBase64;
    }

}
