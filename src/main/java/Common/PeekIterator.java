package Common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;

public class PeekIterator<T> implements Iterator<T> {
    private static final int CACHE_SIZE = 10;
    private Iterator<T> it;
    private LinkedList<T> queueCache = new LinkedList<T>();
    private LinkedList<T> stackPutBacks = new LinkedList<T>();
    private T _endToken = null;

    public PeekIterator(Stream<T> stream) {
        it = stream.iterator();
    }

    public PeekIterator(Stream<T> stream, T endToken) {
        it = stream.iterator();
        this._endToken = endToken;
    }

    @Override
    public boolean hasNext() {
        return this.stackPutBacks.size() > 0 || it.hasNext() || _endToken != null;
    }

    @Override
    public T next() {
        T val;
        if (stackPutBacks.size() > 0) {
            val = this.stackPutBacks.pop();
        } else {
            if (!this.it.hasNext()) {
                T temp = _endToken;
                _endToken = null;
                return temp;
            }
            val = it.next();
        }
        while (queueCache.size() > CACHE_SIZE - 1) {
            queueCache.poll();
        }
        queueCache.add(val);
        return val;
    }

    public T peek() {
        if (this.stackPutBacks.size() > 0) {
            return this.stackPutBacks.getFirst();
        }
        if (!it.hasNext()) {
            return _endToken;
        }
        T val = next();
        this.putBack();
        return val;
    }

    //cache: A -> B ->C -> D
    //putBack: D ->C -B -> A
    public void putBack() {
        if (this.queueCache.size() > 0) {
            this.stackPutBacks.push(this.queueCache.pollLast());
        }
    }
}
