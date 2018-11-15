package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;

public class Parse implements Runnable{

    private Queue<String> queue;
    public Parse(Queue<String> queue){
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
            String term = queue.remove();
            if (stopWords.contains(term))
                continue;

            String letter = nextWord();
            if(letter.equals("%"))
                term = term + letter;
            if(term.charAt(0)=='$')
                term = term.substring(1) + " Dollars";
            if(letter.equals("Dollars"))
                term += " "+letter;
            else if (isNumber(term)) {
                term = parseNumber(Double.parseDouble(term.replace(",", "")));
                if( !(term.charAt(term.length()-1)>'A' && term.charAt(term.length()-1)<'Z')) {
                    if (letter.equals("T")) {
                        term = intOrDouble(Double.parseDouble(term) * 1000);
                        letter = "B";
                    }
                    term += letter;
                }

            }
            System.out.println(term);
        }
    }

    private String parseNumber(Double number){
        String ans = "";
        int multi = 1000;
        if(number > multi){
            multi *= 1000;
            if( number > multi){
                multi *= 1000;
                if( number > multi) {
                    ans = "B";
                    number = (number/multi);
                }
                else{
                    ans = "M";
                    number = number/(multi/1000);
                }
            }
            else{
                ans = "K";
                number = number/(multi/1000);
            }
        }
        return intOrDouble(number)+ans;

    }

    private String intOrDouble(Double d){
        if(isInteger(d))
            return ""+d.intValue();
        return ""+d;
    }


    private String nextWord() {
        String suffix="";
        if (!queue.isEmpty()) {
            String nextWord = queue.peek();
            if (nextWord.equalsIgnoreCase("Thousand")) {
                queue.remove();
                suffix = "K";
            } else if (nextWord.equalsIgnoreCase("Million")) {
                queue.remove();
                suffix = "M";
            } else if (nextWord.equalsIgnoreCase("Billion")) {
                queue.remove();
                suffix = "B";
            } else if (nextWord.equalsIgnoreCase("Trillion")) {
                queue.remove();
                suffix = "T";
            } else if (nextWord.equalsIgnoreCase("percent") || nextWord.equalsIgnoreCase("percentage")) {
                queue.remove();
                suffix = "%";
            }
            else if (nextWord.equalsIgnoreCase("Dollars")) {
                queue.remove();
                suffix = "Dollars";
            }
        }
        return suffix;
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
