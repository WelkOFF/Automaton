import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author @ALIREZA_KAY
 */
public class RegexToDfa {

    private Set<Integer>[] followPos;
    private Node root;
    private Set<RegexState> DRegexStates;

    private Set<String> input; //set of characters is used in input regex

    /**
     * a number is assigned to each characters (even duplicate ones)
     *
     * @param symbNum is a hash map has a key which mentions the number and has
     * a value which mentions the corresponding character or sometimes a string
     * for characters is followed up by backslash like "\*"
     */
    private HashMap<Integer, String> symbNum;

    public RegexToDfa(String regex) {
        regex += "#";
        initialize(regex);
    }

    public void initialize(String regex) {
        //allocating
        DRegexStates = new HashSet<>();
        input = new HashSet<String>();

        getSymbols(regex);

        /**
         * giving the regex to SyntaxTree class constructor and creating the
         * syntax tree of the regular expression in it
         */
        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        followPos = st.getFollowPos(); //the followpos of the syntax tree
    }

    private static String getRegex(Scanner in) {
        System.out.print("Enter a regex: ");
        String regex = in.nextLine();
        return regex+"#";
    }

    private void getSymbols(String regex) {
        /**
         * op is a set of characters have operational meaning for example '*'
         * could be a closure operator
         */
        Set<Character> op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            /**
             * if a character which is also an operator, is followed up by
             * backslash ('\'), then we should consider it as a normal character
             * and not an operator
             */
            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
        }
    }

    public RegexState createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        RegexState q0 = new RegexState(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(followPos.length)) {
            q0.setAccept();
        }
        DRegexStates.clear();
        DRegexStates.add(q0);

        while (true) {
            boolean exit = true;
            RegexState s = null;
            for (RegexState regexState : DRegexStates) {
                if (!regexState.getIsMarked()) {
                    exit = false;
                    s = regexState;
                }
            }
            if (exit) {
                break;
            }

            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbNum.get(p).equals(a)) {
                        U.addAll(followPos[p - 1]);
                    }
                }
                boolean flag = false;
                RegexState tmp = null;
                for (RegexState regexState : DRegexStates) {
                    if (regexState.getName().equals(U)) {
                        tmp = regexState;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    RegexState q = new RegexState(id++);
                    q.addAllToName(U);
                    if (U.contains(followPos.length)) {
                        q.setAccept();
                    }
                    DRegexStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }

        return q0;
    }

    private static String getStr(Scanner in) {
        System.out.print("Enter a string: ");
        String str;
        str = in.nextLine();
        return str;
    }
}
