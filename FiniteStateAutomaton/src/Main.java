import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Main {
    static void testConstructorWithNaiveInsertion() {
        List<String> words =
                Arrays.asList("aa", "bb", "abaa", "aabaabb", "baabba", "aaababa", "bababa", "bbaab", "bbab");

        LexiconMinimalAutomaton automaton = new LexiconMinimalAutomaton(words);
        assert automaton.states.size() == 18 : automaton.states.size();
        System.out.println("Test 1: " + automaton.getStates().size());

        RegexToDfa regexToDfa1 = new RegexToDfa("((a|b)*)");
        RegexState dfa1 = regexToDfa1.createDFA();
        RegexState automatonRegexState = automaton.getStateTreeRoot();
        DFAIntersector intersector = new DFAIntersector(dfa1, automatonRegexState);
        Set<String> allWords = intersector.getIntersection();
        System.out.printf("Matching words count: %s %s \n", allWords.size(), allWords);
    }

    static void test2() {

        List<String> initialWords = Arrays.asList("aa");
        LexiconMinimalAutomaton automaton = new LexiconMinimalAutomaton(initialWords);
        List<String> wordsToAdd =
                Arrays.asList("bb", "abaa", "aabaabb", "baabba", "aaababa", "bababa", "bbaab", "bbab");


        for(String word : wordsToAdd) {
            automaton.insertWord(word);
        }

//        for(String word : wordsToAdd) {
//            System.out.print(automaton.accept(word));
//        }
//        System.out.println(automaton.states.size());
        assert automaton.states.size() == 18;


        automaton.deleteWord("baabba");
//        System.out.println(automaton.states.size());
        assert automaton.states.size() == 16;


//        System.out.println("Remove " + automaton.accept("baabba"));

//        System.out.println(automaton.states.size());

        automaton.insertWord("baabba");
        assert automaton.states.size() == 18;

//        System.out.println("Insert " + automaton.accept("baabba"));

        assert automaton.accept("baabba");
        System.out.println("Test 2: " + automaton.getStates().size());
    }

    static void test3() {
        Map<Integer, Set<State>> inverseMapping = new HashMap<>();
        State s1 = new State(inverseMapping, false);
        State s2 = new State(inverseMapping, false);
        State s3 = new State(inverseMapping, false);
        State s4 = new State(inverseMapping, false);

        s1.addTransition('a', s2);
        s1.addTransition('b', s3);
        s2.addTransition('a', s4);

        assert s1.getNonRecursiveMap().size() == 2;
        assert s2.getNonRecursiveMap().size() == 1;
        assert s3.getNonRecursiveMap().size() == 0;
        assert s4.getNonRecursiveMap().size() == 0;

        assert s1.getNonRecursiveMap().get('a') == s2.getStateID();
        assert s1.getNonRecursiveMap().get('b') == s3.getStateID();
        assert s2.getNonRecursiveMap().get('a') == s4.getStateID();
    }

    static void test4() {
        Map<Integer, Set<State>> inverseMapping = new HashMap<>();
        State s1 = new State(inverseMapping, false);

        int s1Hash = s1.equivalenceHashCode();
        assert inverseMapping.get(s1Hash).contains(s1);

        State s2 = new State(inverseMapping, false);

        int s2Hash = s2.equivalenceHashCode();
        assert inverseMapping.get(s2Hash).contains(s2);

        s1.addTransition('a', s2);
        assert s1Hash != s1.equivalenceHashCode();

        s1Hash = s1.equivalenceHashCode();
        assert inverseMapping.get(s1Hash).contains(s1);

        assert s1Hash == s1.equivalenceHashCode();

        State s3 = new State(inverseMapping, false);

        assert s1Hash == s1.equivalenceHashCode();

        s2.addTransition('b', s3);
        assert s1Hash == s1.equivalenceHashCode();
    }


    static void dictionaryTest() {

        try {
        BufferedReader fr=new BufferedReader(new FileReader("/Users/petar.velkov/IdeaProjects/FiniteStateAutomaton/src/dict.txt"));

        String st;
        List<String> dictionaryWords = new ArrayList<>();
        while ((st = fr.readLine()) != null){
            dictionaryWords.add(st);
        }

//        dictionaryWords = dictionaryWords.subList(0, 100000);

        long startTime = System.nanoTime();

        LexiconMinimalAutomaton automaton = new LexiconMinimalAutomaton(dictionaryWords);

        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime)/1e9 + " s");


        System.out.println(automaton.getStates().size());
        Integer transitionCount = automaton.states.stream().map(state -> state.getTransitions().size()).reduce(0, Integer::sum);
        System.out.println(transitionCount);

            RegexToDfa regexToDfa1 = new RegexToDfa("((a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|S)*)");
            RegexState dfa1 = regexToDfa1.createDFA();
            RegexState automatonRegexState = automaton.getStateTreeRoot();
            DFAIntersector intersector = new DFAIntersector(dfa1, automatonRegexState);
            Set<String> allWords = intersector.getIntersection();
            System.out.printf("Matching words count: %s %s \n", allWords.size(), dictionaryWords.size());

            dictionaryWords.removeAll(allWords);
            System.out.println(dictionaryWords);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void testTime(Runnable test) {
        long startTime = System.nanoTime();

        test.run();

        long endTime = System.nanoTime();

//        System.out.println("Took " + (endTime - startTime)/1e9 + " s");
    }

    public static void main(String[] args) throws IOException {
        testConstructorWithNaiveInsertion();
        test2();
        test3();
        test4();


        testTime(Main::dictionaryTest);





//
////        System.out.println(automaton.getStates().stream().sorted().collect(Collectors.toList()));
//
//        for(String word : wordsToAdd) {
////            System.out.print(automaton.accept(word));
//        }
//
//        automaton.deleteWord("baabba");
////        System.out.println("Remove " + automaton.accept("baabba"));
//
//        automaton.insertWord("baabba");
////        System.out.println("Insert " + automaton.accept("baabba"));
//
//
//        State dfa3 = automaton.getStateTreeRoot();
//
//        RegexToDfa regexToDfa1 = new RegexToDfa("(((a*)b(a*)b(a*))|(a*))");
//        RegexToDfa regexToDfa2 = new RegexToDfa("(((a|b)(a|b))|(a*))");
//
//        State dfa1 = regexToDfa1.createDFA();
//        State dfa2 = regexToDfa2.createDFA();
//
//        DFAIntersector intersector = new DFAIntersector(dfa1, dfa3);
//        Set<String> allWords = intersector.getIntersection();
//
//        for (String word : allWords) {
//            System.out.println(word);
//        }

//
//        DFAIntersector intersector = new DFAIntersector(dfa1, dfa2);
//        Set<String> allWords = intersector.getIntersection();
//
//        for (String word : allWords) {
//            System.out.println(word);
//        }
    }
}
