
import java.util.*;
import static java.util.Collections.sort;

public class LexiconMinimalAutomaton {
    private State startState;
    public Set<State> states;
    public Set<State> finalStates;
    public Map<Integer, Set<State>> inverseMapping;
    public int onlyHashEquals = 0;

    public Set<State> getStates() {
        return states;
    }

    public LexiconMinimalAutomaton(List<String> words) {
        sort(words);
        this.inverseMapping = new HashMap<>();
        this.startState = new State(inverseMapping, false);
        this.states = new HashSet<>();
        this.states.add(startState);
        this.finalStates = new HashSet<>();


        String lastWord = "";
        boolean firstIteration = true;

        for (String word : words) {
            if (firstIteration) {
                firstIteration = false;
                insertWordNaive(word);
            } else {
                List<State> seqCurr = getExistingStateSequence(word);

                reduceExcept(seqCurr.size(), lastWord);
                insertWordNaive(word);
            }

            lastWord = word;
        }

        reduceExcept(0, lastWord);
    }

    void insertWordNaive(String word) {
        State currentState = this.startState;

        for (Character symbol : word.toCharArray()) {
            if(!currentState.hasTransition(symbol)) {
                State newState = new State(inverseMapping, false);
                states.add(newState);

                currentState.addTransition(symbol, newState);
            }

            // currentState has a transition to a state with given symbol
            currentState = currentState.getNextStateBySymbol(symbol);
        }

        finalStates.add(currentState);
        currentState.setFinal(true);
    }

    private List<State> getExistingStateSequence(String word) {
        List<State> stateSequence = new ArrayList<>();
        State currentState = this.startState;
        stateSequence.add(this.startState);

        for (char symbol : word.toCharArray()) {

            if (!currentState.hasTransition(symbol)) {
                break;
            } else {
                currentState = currentState.getNextStateBySymbol(symbol);
                stateSequence.add(currentState);
            }
        }

        return stateSequence;
    }

    State findEquivalent(State currentState) {
        Set<State> equivalentStates =  this.inverseMapping.get(currentState.equivalenceHashCode());

        for(State equivalentState : equivalentStates) {
            if (currentState.getStateID() != equivalentState.getStateID()
                    && currentState.isFinal() == equivalentState.isFinal()
                    && currentState.getNonRecursiveMap().equals(equivalentState.getNonRecursiveMap())) {
                return equivalentState;
            }
        }
        return null;
    }



    void reduceExcept(int len, String a) {
        List<State> stateSequence = getExistingStateSequence(a);

        for (int i = stateSequence.size() - 1; i >= len; i--) {
            State equivalentState = findEquivalent(stateSequence.get(i));
            if (equivalentState != null) {
                states.remove(stateSequence.get(i));
                inverseMapping.get(stateSequence.get(i).equivalenceHashCode()).remove(stateSequence.get(i));
                finalStates.remove(stateSequence.get(i));

                stateSequence.get(i-1).redirectFromTo(stateSequence.get(i), equivalentState);
            } else {
                break;
            }
        }
    }

    State clone(State from, char symbol, State to) {
        State newState = new State(inverseMapping, to.isFinal());
        newState.setTransition(to.getTransitions());
        states.add(newState);

        if (finalStates.contains(to)) {
            finalStates.add(newState);
        }

        from.redirectWithCharacter(symbol, newState);
        return newState;
    }

    boolean isDirectlyConvergent(State target) {
        int count = 0;
        //Consider case when the states points to itself
        for (State current : states) {
            if(current.hasTransitionTo(target)) {
                count++;
                if (count >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    void increaseExcept(String word) {
        State current = startState;

        for (char symbol : word.toCharArray()) {
            if (!current.hasTransition(symbol)) {
                break;
            }

            State last = current;
            current = current.getNextStateBySymbol(symbol);

            if (isDirectlyConvergent(current)) {
                current = clone(last, symbol, current);
            }
        }
    }

    void insertWord(String s) {
        increaseExcept(s);
        insertWordNaive(s);
        reduceExcept(0, s);
    }

    void removeState(State state) {

        System.out.println("Size before deletion: " + states.size());
        states.remove(state);
        inverseMapping.get(state.equivalenceHashCode()).remove(state);
        finalStates.remove(state);

        for(State currentState : states) {
            currentState.removeTransitionsTo(state);
        }

        System.out.println("Size after deletion: " + states.size());

    }

    boolean accept(String word) {
        List<State> stateSequence = getExistingStateSequence(word);
        return stateSequence.size() == word.length() + 1 && finalStates.contains(stateSequence.get(stateSequence.size() - 1));
    }

    void deleteUnusedStates(String s) {
        List<State> seq = getExistingStateSequence(s);

        if (seq.get(seq.size()-1).getTransitions().isEmpty()) {
            int m = seq.size() - 2;
            while (m != -1 && !finalStates.contains(seq.get(m)) && seq.get(m).getTransitions().size() <= 1) {
                m--;
            }

            for (int i = m + 1; i < seq.size(); i++) {
                removeState(seq.get(i));
            }
        } else {
            finalStates.remove(seq.get(seq.size() - 1));
        }

        reduceExcept(0, s);
    }

    void deleteWord(String s) {
        if (accept(s)) {
            increaseExcept(s);
            deleteUnusedStates(s);
        }
    }

    public RegexState getStateTreeRoot() {
        Map<Integer, RegexState> indexToState = new TreeMap<>();
        for(State current : states){
            indexToState.put(current.getStateID(), new RegexState(current.getStateID()));
            if (finalStates.contains(current)) {
                indexToState.get(current.getStateID()).setAccept();
            }
        }

        for(State current : states){
            for(Map.Entry<Character, Integer> entry : current.getNonRecursiveMap().entrySet()){
                indexToState.get(current.getStateID()).addMove(String.valueOf(entry.getKey()), indexToState.get(entry.getValue()));
            }
        }

        return indexToState.get(startState.getStateID());
    }

    static void test1() {
        List<String> words =
                Arrays.asList("aa", "bb", "abaa", "aabaabb", "baabba", "aaababa", "bababa", "bbaab", "bbab");
//        sort(words);

        LexiconMinimalAutomaton automaton = new LexiconMinimalAutomaton(words);
        assert automaton.states.size() == 18;


//        System.out.println(automaton.getStates().size());
    }

    static void test2() {

        List<String> initialWords = Arrays.asList("aa");
        LexiconMinimalAutomaton automaton = new LexiconMinimalAutomaton(initialWords);
        List<String> wordsToAdd =
                Arrays.asList("bb", "abaa", "aabaabb", "baabba", "aaababa", "bababa", "bbaab", "bbab");


        for(String word : wordsToAdd) {
            automaton.insertWord(word);
        }
//        System.out.println(automaton.transition);


//        System.out.println(automaton.states.stream().sorted().collect(Collectors.toList()));

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
    }

    static void test3() {
        State s1 = new State(new HashMap<>(), false);
        State s2 = new State(new HashMap<>(), false);

        assert s1.equivalenceHashCode() == s2.equivalenceHashCode();
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();

    }
}
