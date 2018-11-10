package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;

public class Parse {

    public void Parse(Queue<String> wordStack) {

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

        while(!wordStack.isEmpty()){
            String word = wordStack.remove();

            if (stopWords.contains(word))
                break;

            if (isNumber(word)){
                double number = Double.parseDouble(word.replace(",",""));
                word = parseNumbers(wordStack, number);
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

    private char nextWord(Queue<String> wordStack) {
        char letter=' ';
        if (!wordStack.isEmpty()) {
            String nextWord = wordStack.peek();
            if (nextWord.equals("Thousand")) {
                wordStack.remove();
                letter = 'K';
            } else if (nextWord.equals("Million")) {
                wordStack.remove();
                letter = 'M';
            } else if (nextWord.equals("Billion")) {
                wordStack.remove();
                letter = 'B';
            } else if (nextWord.equals("Trillion")) {
                wordStack.remove();
                letter = 'T';
            } else if (nextWord.equals("percent") || nextWord.equals("percentage")) {
                wordStack.remove();
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
