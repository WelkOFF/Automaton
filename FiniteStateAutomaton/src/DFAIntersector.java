import java.util.*;

public class DFAIntersector {
    RegexState q1, q2;

    DFAIntersector(RegexState q1, RegexState q2) {
        this.q1 = q1;
        this.q2 = q2;

        this.init();
    }

    private static void markStates(RegexState q) {
        Set<RegexState> allRegexStates = new HashSet<>();

        Map<RegexState, List<RegexState>> invGraph = new HashMap<>();

        Queue<RegexState> queue = new LinkedList<>();
        queue.add(q);
        allRegexStates.add(q);

        while (!queue.isEmpty()) {
            RegexState curr = queue.poll();

            if(curr == null) {

                int a = 0;
                System.out.println("curr is null");
                System.out.println("curr is null");
            }
            for (Map.Entry<String, RegexState> entry : curr.getAllMoves().entrySet()) {
                if (!invGraph.containsKey(entry.getValue())) {
                    invGraph.put(entry.getValue(), new LinkedList<>());
                }
                invGraph.get(entry.getValue()).add(curr);


                if(entry.getValue() == null) {
                    System.out.println("entry.getValue() is null");
                }

                if (!allRegexStates.contains(entry.getValue())) {
                    allRegexStates.add(entry.getValue());
                    queue.add(entry.getValue());
                }
            }
        }

        queue.clear();
        for (RegexState s : allRegexStates) {
            if (s.getIsAcceptable()) {
                queue.add(s);
            }
        }

        while (!queue.isEmpty()) {
            RegexState curr = queue.poll();

            if (invGraph.containsKey(curr)) {
                for (RegexState adj : invGraph.get(curr)) {
                    if (!adj.canReachFinalState) {
                        adj.canReachFinalState = true;
                        queue.add(adj);
                    }
                }
            }
        }
    }

    private void init() {
        markStates(q1);
        markStates(q2);
    }

    void dfs(RegexState s1, RegexState s2, StringBuilder str, Set<String> allStrings) {
        if (s1.getIsAcceptable() && s2.getIsAcceptable()) {
            allStrings.add(str.toString());
        }
        if (!s1.canReachFinalState || !s2.canReachFinalState) {
            return;
        }

        for (Map.Entry<String, RegexState> entry1 : s1.getAllMoves().entrySet()) {
            if (s2.getAllMoves().containsKey(entry1.getKey())) {
                RegexState newS1 = entry1.getValue();
                RegexState newS2 = s2.getAllMoves().get(entry1.getKey());

                StringBuilder newStr = new StringBuilder(str.toString());
                newStr.append(entry1.getKey());

                dfs(newS1, newS2, newStr, allStrings);
            }
        }
    }

    public Set<String> getIntersection() {
        Set<String> allStrings = new HashSet<>();
        dfs(q1, q2, new StringBuilder(), allStrings);

        return allStrings;
    }
}
