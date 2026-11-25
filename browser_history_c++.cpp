#include <iostream>
#include <string>
using namespace std;

const int STACK_CAPACITY = 50;
const int TABLE_SIZE = 101;

// ---------- Stack (array based) : Back / Forward navigation ----------
class Stack {
    string arr[STACK_CAPACITY];
    int top;
public:
    Stack() : top(-1) {}
    bool isEmpty() { return top == -1; }
    bool isFull() { return top == STACK_CAPACITY - 1; }
    void push(const string &u) { if (!isFull()) arr[++top] = u; }
    string pop() { return arr[top--]; }
    string peek() { return arr[top]; }
    void clear() { top = -1; }
};

// ---------- Singly Linked List : full chronological history ----------
struct HNode { int visitId; string url; HNode *next; };
class HistoryList {
    HNode *head; int counter;
public:
    HistoryList() : head(NULL), counter(0) {}
    void insertAtHead(const string &u) {
        HNode *n = new HNode{++counter, u, head};
        head = n;
    }
    void display() {
        if (!head) { cout << "History is empty.\n"; return; }
        cout << "---- Browsing History (Latest First) ----\n";
        for (HNode *t = head; t; t = t->next)
            cout << "  #" << t->visitId << "  " << t->url << "\n";
    }
    void clear() {
        while (head) { HNode *t = head; head = head->next; delete t; }
        counter = 0;
    }
};

// ---------- HashMap (Separate Chaining) : URL -> visit count ----------
struct Entry { string url; int visits; Entry *next; };
class HashMap {
    Entry *table[TABLE_SIZE];
    int hashFunction(const string &s) {
        long h = 0;
        for (char c : s) h = (h * 31 + c) % TABLE_SIZE;
        return (int)h;
    }
public:
    HashMap() { for (int i = 0; i < TABLE_SIZE; i++) table[i] = NULL; }
    void record(const string &u) {
        int i = hashFunction(u);
        for (Entry *e = table[i]; e; e = e->next)
            if (e->url == u) { e->visits++; return; }
        table[i] = new Entry{u, 1, table[i]};
    }
    int search(const string &u) {
        for (Entry *e = table[hashFunction(u)]; e; e = e->next)
            if (e->url == u) return e->visits;
        return 0;
    }
    void clear() {
        for (int i = 0; i < TABLE_SIZE; i++) {
            while (table[i]) { Entry *t = table[i]; table[i] = t->next; delete t; }
        }
    }
};

// ---------- Browser Controller ----------
class Browser {
    Stack backStack, forwardStack;
    HistoryList history;
    HashMap visits;
    string current;
public:
    void visit(const string &u) {
        if (!current.empty()) backStack.push(current);
        forwardStack.clear();
        current = u;
        history.insertAtHead(u);
        visits.record(u);
        cout << "Opened: " << u << "\n";
    }
    void back() {
        if (backStack.isEmpty()) { cout << "No previous page.\n"; return; }
        forwardStack.push(current);
        current = backStack.pop();
        cout << "Back to: " << current << "\n";
    }
    void forward() {
        if (forwardStack.isEmpty()) { cout << "No next page.\n"; return; }
        backStack.push(current);
        current = forwardStack.pop();
        cout << "Forward to: " << current << "\n";
    }
    void showCurrent() {
        if (current.empty()) cout << "No page open.\n";
        else cout << "Current Page: " << current << "\n";
    }
    void showHistory() { history.display(); }
    void search(const string &u) {
        int v = visits.search(u);
        if (v) cout << u << " found. Visited " << v << " time(s).\n";
        else cout << u << " not found in history.\n";
    }
    void clearAll() {
        history.clear(); visits.clear();
        backStack.clear(); forwardStack.clear(); current = "";
        cout << "History cleared.\n";
    }
};

int main() {
    Browser b; int ch; string u;
    do {
        cout << "\n===== BROWSER HISTORY MANAGER =====\n"
             << "1. Visit URL\n2. Back\n3. Forward\n4. Show Current Page\n"
             << "5. View History\n6. Search URL\n7. Clear History\n0. Exit\n"
             << "Enter choice: ";
        cin >> ch;
        switch (ch) {
            case 1: cout << "Enter URL: "; cin >> u; b.visit(u); break;
            case 2: b.back(); break;
            case 3: b.forward(); break;
            case 4: b.showCurrent(); break;
            case 5: b.showHistory(); break;
            case 6: cout << "Enter URL: "; cin >> u; b.search(u); break;
            case 7: b.clearAll(); break;
            case 0: cout << "Exiting...\n"; break;
            default: cout << "Invalid choice.\n";
        }
    } while (ch != 0);
    return 0;
}
