package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;

public class Parse implements Runnable{

    private Queue<String> queue;
    Parse(Queue<String> queue){
        this.queue = queue;
    }

    public void run() {

        Set<String> stopWords = new HashSet<String>();

        String fileName = "stopWords.txt";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                stopWords.add(line);
            }
            bufferedReader.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!queue.isEmpty()){
            String word = queue.remove();

            if (stopWords.contains(word))
                continue;

            if (isNumber(word)){
                double number = Double.parseDouble(word.replace(",",""));
                word = parseNumbers(queue, number);
            }
            System.out.println(word);
        }
    }

    private String parseNumbers(Queue<String> wordStack, double number) {
        int mult = 1000;
        if(number < mult){
            char letter = nextWord(wordStack);
            if(letter=='T') {
                number *= 1000;
                letter = 'B';
            }
            if(isInteger(number))
                return ""+new Double(number).intValue()+letter;
            return ""+number+letter;
        }
        mult *= 1000;
        if( number < mult) {
            return (number/(mult/1000))+"K";
        }
        mult *= 1000;
        if( number < mult)
            return (number/(mult/1000))+"M";
        return (number/mult)+"B";
    }

    private char nextWord(Queue<String> wordQueue) {
        char letter=' ';
        if (!wordQueue.isEmpty()) {
            String nextWord = wordQueue.peek();
            if (nextWord.equals("Thousand")) {
                wordQueue.remove();
                letter = 'K';
            } else if (nextWord.equals("Million")) {
                wordQueue.remove();
                letter = 'M';
            } else if (nextWord.equals("Billion")) {
                wordQueue.remove();
                letter = 'B';
            } else if (nextWord.equals("Trillion")) {
                wordQueue.remove();
                letter = 'T';
            } else if (nextWord.equals("percent") || nextWord.equals("percentage")) {
                wordQueue.remove();
                letter = '%';
            }
        }
        return letter;
    }

    private boolean isInteger(double word) {
        return String.valueOf(word).endsWith(".0");
    }

    private boolean isNumber(String word) {
        for(int i = 0; i < word.length(); i++)
            if(word.charAt(i) < '0' || word.charAt(i) > '9')
                if(!(word.charAt(i)=='.') && !(word.charAt(i)==','))
                    return false;
        return true;
    }
}
