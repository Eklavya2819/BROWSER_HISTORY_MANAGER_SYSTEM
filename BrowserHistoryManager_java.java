import java.util.Scanner;

public class BrowserHistoryManager {

    static final int STACK_CAPACITY = 50;
    static final int TABLE_SIZE = 101;

    // ---------- Stack (array based) : Back / Forward navigation ----------
    static class Stack {
        private String[] arr = new String[STACK_CAPACITY];
        private int top = -1;
        boolean isEmpty() { return top == -1; }
        boolean isFull() { return top == STACK_CAPACITY - 1; }
        void push(String u) { if (!isFull()) arr[++top] = u; }
        String pop() { return arr[top--]; }
        String peek() { return arr[top]; }
        void clear() { top = -1; }
    }

    // ---------- Singly Linked List : full chronological history ----------
    static class HNode {
        int visitId; String url; HNode next;
        HNode(int id, String u, HNode n) { visitId = id; url = u; next = n; }
    }

    static class HistoryList {
        private HNode head = null;
        private int counter = 0;
        void insertAtHead(String u) { head = new HNode(++counter, u, head); }
        void display() {
            if (head == null) { System.out.println("History is empty."); return; }
            System.out.println("---- Browsing History (Latest First) ----");
            for (HNode t = head; t != null; t = t.next)
                System.out.println("  #" + t.visitId + "  " + t.url);
        }
        void clear() { head = null; counter = 0; }
    }

    // ---------- HashMap (Separate Chaining) : URL -> visit count ----------
    static class Entry {
        String url; int visits; Entry next;
        Entry(String u, int v, Entry n) { url = u; visits = v; next = n; }
    }

    static class HashMap {
        private Entry[] table = new Entry[TABLE_SIZE];
        private int hashFunction(String s) {
            long h = 0;
            for (int i = 0; i < s.length(); i++) h = (h * 31 + s.charAt(i)) % TABLE_SIZE;
            return (int) h;
        }
        void record(String u) {
            int i = hashFunction(u);
            for (Entry e = table[i]; e != null; e = e.next)
                if (e.url.equals(u)) { e.visits++; return; }
            table[i] = new Entry(u, 1, table[i]);
        }
        int search(String u) {
            for (Entry e = table[hashFunction(u)]; e != null; e = e.next)
                if (e.url.equals(u)) return e.visits;
            return 0;
        }
        void clear() { table = new Entry[TABLE_SIZE]; }
    }

    // ---------- Browser Controller ----------
    static class Browser {
        private Stack backStack = new Stack(), forwardStack = new Stack();
        private HistoryList history = new HistoryList();
        private HashMap visits = new HashMap();
        private String current = "";

        void visit(String u) {
            if (!current.isEmpty()) backStack.push(current);
            forwardStack.clear();
            current = u;
            history.insertAtHead(u);
            visits.record(u);
            System.out.println("Opened: " + u);
        }
        void back() {
            if (backStack.isEmpty()) { System.out.println("No previous page."); return; }
            forwardStack.push(current);
            current = backStack.pop();
            System.out.println("Back to: " + current);
        }
        void forward() {
            if (forwardStack.isEmpty()) { System.out.println("No next page."); return; }
            backStack.push(current);
            current = forwardStack.pop();
            System.out.println("Forward to: " + current);
        }
        void showCurrent() {
            if (current.isEmpty()) System.out.println("No page open.");
            else System.out.println("Current Page: " + current);
        }
        void showHistory() { history.display(); }
        void search(String u) {
            int v = visits.search(u);
            if (v > 0) System.out.println(u + " found. Visited " + v + " time(s).");
            else System.out.println(u + " not found in history.");
        }
        void clearAll() {
            history.clear(); visits.clear();
            backStack.clear(); forwardStack.clear(); current = "";
            System.out.println("History cleared.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Browser b = new Browser();
        int ch;
        do {
            System.out.println("\n===== BROWSER HISTORY MANAGER =====");
            System.out.println("1. Visit URL\n2. Back\n3. Forward\n4. Show Current Page");
            System.out.println("5. View History\n6. Search URL\n7. Clear History\n0. Exit");
            System.out.print("Enter choice: ");
            ch = sc.nextInt();
            switch (ch) {
                case 1: System.out.print("Enter URL: "); b.visit(sc.next()); break;
                case 2: b.back(); break;
                case 3: b.forward(); break;
                case 4: b.showCurrent(); break;
                case 5: b.showHistory(); break;
                case 6: System.out.print("Enter URL: "); b.search(sc.next()); break;
                case 7: b.clearAll(); break;
                case 0: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice.");
            }
        } while (ch != 0);
        sc.close();
    }
}
