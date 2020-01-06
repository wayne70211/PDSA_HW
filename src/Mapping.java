import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
public class Mapping{
    public static void main(String[] args) throws FileNotFoundException {

        int foundCount = 0;
        int repeatCount = 0;

        // Scanner
        File file = new File("input_HW0.txt") ;
        Scanner in = new Scanner(file);

        // Read int k
        int readCount=Integer.parseInt(in.nextLine());

        String[] array =new String[readCount];

        // Read k nucleotide sequences
        for (int i=0;i<readCount;i++){
            String keyword = in.nextLine();
            array [i] = keyword;
        }

        // Read reference sequence
        String target = in.nextLine();

        // Mapping
        for (int i=0;i<readCount;i++){
            int search_idx = target.indexOf(array[i]);
            if (search_idx != -1){
                foundCount += 1;
            }
            search_idx = target.indexOf(array[i],search_idx+1);
            if (search_idx != -1){
                repeatCount += 1;
            }
        }

        // Print out Mapping consequences
        System.out.println(foundCount);
        System.out.println(repeatCount);
    }
}