package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Parse {

    public void Parse(Stack<String> wordStack) {

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

        while(!wordStack.empty()){
            String word = wordStack.pop();

            if (stopWords.contains(word))
                break;

            if (isNumber(word)){
                int number = Integer.parseInt(word);
                //switch(numberSize(number)):

            }
        }
    }

    private char numberSize(int number) {
        if(number < 1000)
            return 's';
        return 'g';
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
