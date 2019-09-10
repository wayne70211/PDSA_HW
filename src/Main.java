import java.util.Scanner;
import java.io.File;
public class Main{
    public static void main(String[] args){

        int foundCount = 0;
        int repeatCount = 0;

        //Scanner (use this scanner when uploading your code)
        Scanner in = new Scanner(System.in);

        //scanner for local test (delete this part when uploading to onlinejudge)
//        File file = new File("input_HW0.txt") ;
//        Scanner in = null;
//        try {
//            in = new Scanner(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        //Read int k
        int readCount=Integer.parseInt(in.nextLine());

        String[] array =new String[readCount];

        //Read k nucleotide sequences
        for (int i=0;i<readCount;i++){
            String keyword = in.nextLine();
            array [i] = keyword;
        }
        //Read reference sequence
        String target = in.nextLine();
        //Mapping (you might use "indexof")
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
        //print out Mapping consequences
        System.out.println(foundCount);
        System.out.println(repeatCount);
    }
}