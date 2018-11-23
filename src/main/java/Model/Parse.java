package Model;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Parse implements Runnable{

    private Queue<String> queue;
    private CorpusDocument corpus_doc;
    private boolean stm;
    private static String[] shortMonth = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private static String[] longMonth = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    public Parse(Queue<String> queue, boolean stm){
        this.queue = queue;
        this.stm = stm;
    }

    public Parse(CorpusDocument corpus_doc, boolean stm){
        this.corpus_doc = corpus_doc;
        this.stm = stm;
    }

    private static Queue<String> StringToQueue(String[] split) {
        Queue<String> queue = new LinkedList<String>();
        Collections.addAll(queue,split);
        return queue;
    }

    public void run() {
        queue = StringToQueue(StringUtils.split(corpus_doc.getM_docText()," .\n\r\t"));
        while(!queue.isEmpty()){
            String term = queue.remove();
            /*if(stm)
                doStem*/
            String nextWord = "";
            if (!term.equals(",")) {
                if (isNumber(term)) { //if current term is a number
                    nextWord = nextWord();
                    if (isMonth(nextWord) != -1) //if it is rule Hei - it is a Month term
                        term = handleMonthDay(nextWord, term);

                    else if (nextWord.equals("Dollars"))  //if it is rule Dalet - it is a Dollar term
                        term = handleDollars(Double.parseDouble(term.replace(",", "")));

                    else if (nextWord.equals("%")) // if it is rule Gimel - it is a percent term
                        term = handlePercent(term,nextWord);

                    else {
                        term = handleNumber(Double.parseDouble(term.replace(",", "")));
                        if (!(term.charAt(term.length() - 1) > 'A' && term.charAt(term.length() - 1) < 'Z')) { //if a number returned is smaller than 1000
                            if (nextWord.equals("T")) {
                                term = numberValue(Double.parseDouble(term) * 1000);
                                nextWord = "B";
                            }
                            term += nextWord;

                            nextWord = queue.peek();
                            if (nextWord != null && isFraction(queue.peek())) { //rule Alef 2 - fraction rule
                                queue.remove();
                                term += " " + nextWord;
                                nextWord = nextWord();
                                if (nextWord.equals("Dollars"))
                                    term += " " + nextWord;

                            }
                            else if (nextWord != null && nextWord.equals("U.S.")) {
                                queue.remove();
                                nextWord = queue.peek();
                                if (nextWord != null && nextWord.equalsIgnoreCase("dollars")) {
                                    queue.remove();
                                    double d = Double.parseDouble(term.substring(0, term.length() - 1));
                                    if (term.charAt(term.length() - 1) == 'M')
                                        d*=1000000;
                                    else if (term.charAt(term.length() - 1) == 'B') {
                                        d*=1000000000;
                                    }
                                    term = handleDollars(d);
                                }
                            }

                        }
                    }
                }
                else if (isNumber(term.substring(1))) {
                    if (term.charAt(0) == '$') //rule Dalet - dollar sign at the begining of a number
                        term = handleDollars(Double.parseDouble(term.substring(1).replace(",", "")));

                } else if (isNumber(term.substring(0, term.length() - 1))) {
                    if (!term.substring(0, term.length() - 1).equals("%")) {
                        nextWord = nextWord();
                        if (term.substring(term.length() - 1).equals("m") && nextWord.equals("Dollars"))
                            term = numberValue(Double.parseDouble(term.substring(0, term.length() - 1))) + " M " + nextWord;

                    }
                } else if (isNumber(term.substring(0, term.length() - 2))) {
                    nextWord = nextWord();
                    if (term.substring(term.length() - 2).equals("bn") && nextWord.equals("Dollars"))
                        term = numberValue(Double.parseDouble(term.substring(0, term.length() - 2)) * 1000) + " M " + nextWord;

                } else if (isMonth(term) != -1) { // rule Vav - month year rule
                    if (!queue.isEmpty()) {
                        nextWord = queue.peek();
                        if (isNumber(nextWord)) {
                            queue.remove();
                            term = handleMonthYear(term,nextWord);
                        }
                    }
                }
            }
            if(!ReadFile.stopWords.contains(term))
                System.out.println(term);
        }

    }

    private String handlePercent(String term, String percentSign) {
        return term+percentSign;
    }

    private String handleMonthDay(String month, String day){
        int monthNum = isMonth(month);
        String newTerm = isMonth(month) + "-" + day;
        if (monthNum < 9)
            newTerm = "0" + newTerm;
        return newTerm;
    }

    private String handleMonthYear (String month, String year){
        int monthNum = isMonth(month);
        String newTerm = year + "-";
        if (monthNum < 9)
            newTerm += "0" + monthNum;
        return newTerm + monthNum;
    }

    /**
     * Rule DALET - changed number according to the rule
     * @param number the number to be changed
     * @return the number after rule
     */
    private String handleDollars(double number) {
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
            return addCommas(numberValue(number))+ " Dollars";
        return numberValue(number)+ " " + ans + " Dollars";
    }

    /**
     * Rule ALEF - change numbers according to their size
     * @param number - number to be changed
     * @return the number after changed
     */
    private String handleNumber(double number){
        String ans = "";
        int multi = 1000;
        if(number > multi){//smaller than 1000
            multi *= 1000;
            if( number > multi){
                multi *= 1000;
                if( number > multi) { // is billion or trillion
                    ans = "B";
                    number = (number/multi);
                }
                else{ // is million
                    ans = "M";
                    multi /= 1000;
                    number = number/multi;
                }
            }
            else{ // is thousand
                ans = "K";
                multi /= 1000;
                number = number/multi;
            }
        }
        return numberValue(number)+ans;

    }

    /**
     * adds commas to a number
     * @param number number to add commas to
     * @return returns number as a String with commas
     */
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

    /**
     * checks if the number is int or double
     * @param d number to be checked
     * @return returns a string with the correct number
     */
    private String numberValue(Double d){
        if(isInteger(d))
            return ""+d.intValue();
        return ""+d;
    }

    /**
     * Checks if the next word is one of certain rules given to the parser
     * @return returns a string according to the rules
     */
    private String nextWord() {
        String nextWord="";
        if (!queue.isEmpty()) {
            String queuePeek = queue.peek();
            if (queuePeek.equalsIgnoreCase("Thousand")) {
                queue.remove();
                nextWord = "K";
            } else if (queuePeek.equalsIgnoreCase("Million")) {
                queue.remove();
                nextWord = "M";
            } else if (queuePeek.equalsIgnoreCase("Billion")) {
                queue.remove();
                nextWord = "B";
            } else if (queuePeek.equalsIgnoreCase("Trillion")) {
                queue.remove();
                nextWord = "T";
            } else if (queuePeek.equalsIgnoreCase("percent") || queuePeek.equalsIgnoreCase("percentage")) {
                queue.remove();
                nextWord = "%";
            } else if (queuePeek.equalsIgnoreCase("Dollars")) {
                queue.remove();
                nextWord = "Dollars";
            } else if(isMonth(queuePeek)!=-1){
                queue.remove();
                nextWord = queuePeek;
            }
        }
        return nextWord;
    }

    /**
     * Checks if the string given is a fraction of a number
     * @param nextWord string to be checked
     * @return true if string is a fraction, false otherwise
     */
    private boolean isFraction(String nextWord) {
        int idx =nextWord.indexOf('/');
        if (idx!=-1)
            return isNumber(nextWord.substring(0,idx)) && isNumber(nextWord.substring(idx+1));
        return false;
    }

    /**
     * Checks if a number is integer or double
     * @param word number to be checked
     * @return returns true if it is integer, false it is double
     */
    private boolean isInteger(double word) {
        return word == Math.floor(word) && !Double.isInfinite(word);
    }

    /**
     * Checks if a string is a month
     * @param month - the string to be checked
     * @return true if it is a month, false otherwise
     */
    private int isMonth(String month){
        for (int i = 0; i < shortMonth.length; i++)
            if(month.equalsIgnoreCase(shortMonth[i]) || month.equalsIgnoreCase(longMonth[i]))
                return i+1;
        return -1;
    }

    /**
     * Checks is a string is a number
     * @param word - the string to be checked
     * @return returns true if it is a number, false otherwise
     */
    private boolean isNumber(String word) {
        for(int i = 0; i < word.length(); i++)
            if(word.charAt(i) < '0' || word.charAt(i) > '9')
                if(!(word.charAt(i)=='.') && !(word.charAt(i)==','))
                    return false;
        return true;
    }
}
