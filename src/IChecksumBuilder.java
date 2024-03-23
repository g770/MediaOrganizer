import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public interface IChecksumBuilder {
    Map<String, List<AbstractMap.SimpleEntry<String, File>>> getChecksumMap();

    void calculateChecksums() throws IOException;
}
