import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        var builder = new ChecksumBuilder(List.of("c:\\users\\dwelz\\downloads"));

        System.out.println("Starting...");
        try {
            builder.calculateChecksums();

            var checksumMap = builder.getChecksumMap();

            System.out.println("Scan complete");
            System.out.println("Map keys: " + checksumMap.size());

            for (Map.Entry<String, List<File>> k : checksumMap.entrySet()) {
                if (k.getValue().size() > 1) {
                    System.out.print("Duplicates detected: ");
                    k.getValue().forEach(x -> System.out.print(x + ", "));
                    System.out.println();
                }
            }

            System.out.println("Organizing files...");

            var org = new Organizer(checksumMap, "");
            org.organizeFiles();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}