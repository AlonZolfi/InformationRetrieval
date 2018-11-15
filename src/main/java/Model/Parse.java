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
            String nextWord = "";
            if (isNumber(term)) {
                nextWord = nextWord();
                if (nextWord.equals("Dollars")){
                    term = parseDollars(Double.parseDouble(term.replace(",", ""))) + nextWord;
                }
                else if(nextWord.equals("%")) {
                    term = term + nextWord;
                }
                else {
                    term = parseNumber(Double.parseDouble(term.replace(",", "")));
                    if (!(term.charAt(term.length() - 1) > 'A' && term.charAt(term.length() - 1) < 'Z')) {
                        if (nextWord.equals("T")) {
                            term = intOrDouble(Double.parseDouble(term) * 1000);
                            nextWord = "B";
                        }
                        term += nextWord;
                    }
                }
            }
            else if( isNumber(term.substring(1))){
                if(term.charAt(0)=='$')
                    term = parseDollars(Double.parseDouble(term.substring(1).replace(",", ""))) + "Dollars";
            }
            else if(isNumber(term.substring(0,term.length()-1))) {
                if(!term.substring(0,term.length()-1).equals("%")){
                    nextWord = nextWord();
                    if (term.substring(term.length()-1).equals("m") && nextWord.equals("Dollars")){
                        term = term.substring(0,term.length()-1)+ " M " + nextWord;
                    }
                }
            }
            else if(isNumber(term.substring(0,term.length()-2))){
                nextWord = nextWord();
                if (term.substring(term.length()-2).equals("bn") && nextWord.equals("Dollars")){
                    String s = intOrDouble(Double.parseDouble(term.substring(0,term.length()-2))*1000);
                    term = s+ " M " + nextWord;
                }
            }
            nextWord = nextWord();



            System.out.println(term);
        }
    }

    private String parseDollars(double number) {
        String ans = "";
        int multi = 1000000;
        if(number >= multi) {
            ans = "M";
            number /= multi;
        }
        String nextWord = nextWord();
        if (nextWord.equals("M"))
            ans = "M";
        else if (nextWord.equals("B")){
            number *= 1000;
            ans = "M";
        }
        if (ans.equals(""))
            return addCommas(intOrDouble(number))+ " " +ans;
        return intOrDouble(number)+ " " +ans + " ";
    }

    private String addCommas(String number) {
        String saveFraction="";
        if(number.indexOf('.')!=-1) {
            saveFraction = number.substring(number.indexOf('.'));
            number = number.substring(0, number.indexOf('.'));
        }
        for (int i = number.length()-3; i > 0; i-=3) {
            number = number.substring(0,i)+","+number.substring(i);
        }
        return number+saveFraction;
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
            } else if (nextWord.equalsIgnoreCase("Dollars")) {
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
