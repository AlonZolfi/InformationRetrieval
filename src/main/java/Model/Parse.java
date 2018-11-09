package Model;

import java.util.Stack;

public class Parse {
    public void Parse(Stack<String> wordStack){
        while(!wordStack.empty()){
            String word = wordStack.pop();
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
