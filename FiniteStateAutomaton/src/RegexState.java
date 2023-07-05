/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author @ALIREZA_KAY
 */
public class RegexState {
    private int ID;
    private Set<Integer> name;
    private HashMap<String, RegexState> move;
    
    private boolean IsAcceptable;
    private boolean IsMarked;
    public boolean canReachFinalState;
    
    public RegexState(int ID){
        this.ID = ID;
        move = new HashMap<>();
        name = new HashSet<>();
        IsAcceptable = false;
        IsMarked = false;
        this.canReachFinalState = false;
    }
    
    public void addMove(String symbol, RegexState s){
        move.put(symbol, s);
    }
    
    public void addToName(int number){
        name.add(number);
    }
    public void addAllToName(Set<Integer> number){
        name.addAll(number);
    }
    
    public void setIsMarked(boolean bool){
        IsMarked = bool;
    }
    
    public boolean getIsMarked(){
        return IsMarked;
    }
    
    public Set<Integer> getName(){
        return name;
    }

    public void setAccept() {
        IsAcceptable = true;
    }
    
    public boolean getIsAcceptable(){
        return  IsAcceptable;
    }
    
    public RegexState getNextStateBySymbol(String str){
        return this.move.get(str);
    }
    
    public HashMap<String, RegexState> getAllMoves(){
        return move;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RegexState)) {
            return false;
        }

        RegexState other = (RegexState) obj;
        return ID == other.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
