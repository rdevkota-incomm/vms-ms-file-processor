import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        Path path = Paths.get("C:\\Users\\rdevkota\\programs\\return-files\\del\\test.cvs");

        System.out.println(path.getParent());
        System.out.println(System.getProperty("file.separator"));
//         System.getProperty("file.separator");

    }
}
