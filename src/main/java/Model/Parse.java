package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Queue;
import java.util.Stack;

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
                int number = Integer.parseInt(word);
                String parsedNumber = parseNumbers(wordStack, number);
                System.out.println(parsedNumber);
            }
        }
    }

    private String parseNumbers(Queue<String> wordStack, int number) {
        int mult = 1000;
        if(number < mult){
            if (!wordStack.isEmpty()){
                if(wordStack.peek().equals("Thousand")){
                    wordStack.remove();
                    return (number/mult)+"K";
                }
                mult *= 1000;
                if(wordStack.peek().equals("Million")){
                    wordStack.remove();
                    return (number/mult)+"M";
                }
                mult *= 1000;
                if(wordStack.peek().equals("Billion")){
                    wordStack.remove();
                    return (number/mult)+"B";
                }
                if(wordStack.peek().equals("Trillion")){
                    wordStack.remove();
                    return (number*1000)+"T";
                }
            }
        }
        if( number < mult) {
            return (number/mult)+"K";
        }
        mult *= 1000;
        if( number < mult)
            return (number/mult)+"M";
        return (number/mult)+"B";
    }

    private boolean isNumber(String word) {
        for(int i = 0; i < word.length(); i++)
            if(word.charAt(i) < '0' || word.charAt(i) > '9'){
                if( !(word.charAt(i)==',') && !(word.charAt(i)=='.'))
                    return false;
            }
        return true;
    }


}
