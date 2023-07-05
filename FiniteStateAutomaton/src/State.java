import java.util.*;

public class State {

    private static int stateCount = 0;
    private final int stateID;
    private Map<Character, State> transition;
    private boolean isFinal;
    private Map<Integer, Set<State>> inverseMapping;
    public State(Map<Integer, Set<State>> inverseMapping, boolean isFinal){
        this.stateID = ++stateCount;
        this.isFinal = isFinal;
        this.transition = new HashMap<>();
        this.inverseMapping = inverseMapping;
        this.inverseMapping.putIfAbsent(this.equivalenceHashCode(), new HashSet<>());
        this.inverseMapping.get(this.equivalenceHashCode()).add(this);
    }

    boolean hasTransition(char symbol){
        return transition.containsKey(symbol);
    }

    boolean hasTransitionTo(State state){
        return transition.containsValue(state);
    }

    public void addTransition(char symbol, State s){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        if(!this.inverseMapping.containsKey(oldEquivalenceHashCode)) {
            System.out.println("ERROR: inverseMapping does not contain key " + oldEquivalenceHashCode);
        }
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        transition.put(symbol, s);

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public void redirectFromTo(State from, State to){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        for(Map.Entry<Character, State> entry : transition.entrySet()){
            if(entry.getValue().equals(from)){
                entry.setValue(to);
            }
        }

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public void redirectWithCharacter(char symbol, State to){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        transition.put(symbol, to);

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public void removeTransitionsTo(State target){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        List<Map.Entry<Character, State>> toRemove = new ArrayList<>();
        for(Map.Entry<Character, State> entry : transition.entrySet()){
            if(entry.getValue().equals(target)){
                toRemove.add(entry);
            }
        }

        for(Map.Entry<Character, State> entry : toRemove){
            transition.remove(entry.getKey());
        }

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public State getNextStateBySymbol(char Label){
        return transition.get(Label);
    }
    public Map<Character, State> getTransitions(){
        return transition;
    }

    public void setTransition(Map<Character, State> transition){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        this.transition = new HashMap<>(transition);

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public int getStateID(){
        return stateID;
    }

    public boolean isFinal(){
        return isFinal;
    }

    public void setFinal(boolean isFinal){
        int oldEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.get(oldEquivalenceHashCode).remove(this);

        this.isFinal = isFinal;

        int newEquivalenceHashCode = this.equivalenceHashCode();
        this.inverseMapping.putIfAbsent(newEquivalenceHashCode, new HashSet<>());
        this.inverseMapping.get(newEquivalenceHashCode).add(this);
    }

    public Map<Character, Integer> getNonRecursiveMap() {
        return
                transition.entrySet().stream()
                        .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue().getStateID()), HashMap::putAll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return stateID == state.stateID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateID);
    }

    public int equivalenceHashCode() {
        return Objects.hash(getNonRecursiveMap(), isFinal);
    }

    @Override
    public String toString() {
        return "{" +
                "stateID=" + stateID +
                ", transition=" + getNonRecursiveMap() +
                ", isFinal=" + isFinal +
                '}';
    }
}

