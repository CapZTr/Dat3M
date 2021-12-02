package com.dat3m.dartagnan.solver.newcaat.misc;


import com.dat3m.dartagnan.solver.newcaat.predicates.relationGraphs.Edge;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.*;

// NOTE: Right now, this is almost identical to ArrayList<Edge> but with reversed iteration order
// But we use it to allow the possibility of changing implementations later
public class EdgeList implements List<Edge> {

    private final static IteratorPool<Edge, EdgeList> ITERATOR_POOL = new IteratorPool<>(EdgeIterator::new, 10);

    private final ArrayList<Edge> edgeList;

    public EdgeList() {
        this(10);
    }

    public EdgeList(int initialCapacity) {
        edgeList = new ArrayList<>(initialCapacity);
    }

    @Override
    public int size() {
        return edgeList.size();
    }

    @Override
    public boolean isEmpty() {
        return edgeList.isEmpty();
    }

    public boolean contains(Edge e) {
        for (int i = 0; i < size(); i++) {
            if (edgeList.get(i).equals(e)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Edge && contains((Edge) o);
    }

    @Override
    public Iterator<Edge> iterator() {
        return ITERATOR_POOL.get(this);
    }

    @Override
    public Object[] toArray() {
        return edgeList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return edgeList.toArray(a);
    }

    @Override
    public boolean add(Edge edge) {
        return edgeList.add(edge);
    }

    @Override
    public boolean remove(Object o) {
        return edgeList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return edgeList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Edge> c) {
        return edgeList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Edge> c) {
        return edgeList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return edgeList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return edgeList.retainAll(c);
    }

    @Override
    public void clear() {
        edgeList.clear();
    }

    @Override
    public Edge get(int index) {
        return edgeList.get(index);
    }

    @Override
    public Edge set(int index, Edge element) {
        return edgeList.set(index, element);
    }

    @Override
    public void add(int index, Edge element) {
        edgeList.add(index, element);
    }

    @Override
    public Edge remove(int index) {
        return edgeList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return edgeList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return edgeList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Edge> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Edge> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Edge> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<Edge> stream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(),
                SIZED | DISTINCT | SUBSIZED | NONNULL | SUBSIZED), false);
    }


    private final static class EdgeIterator extends ReusableIterator<Edge, EdgeList> {
        private int index;
        private EdgeList edgeList;

        public EdgeIterator(IteratorPool<Edge, EdgeList> pool) {
            super(pool);
        }

        @Override
        public void reuseFor(EdgeList collection) {
            this.edgeList = collection;
            this.index = edgeList.size();
        }

        /*
           TODO: Ideally, this would not cause a closing by itself
            and we would instead call close (using try-finally) from the calling code
        */
        @Override
        public boolean hasNext() {
            boolean hasNext = index > 0;
            if (!hasNext) {
                close();
            }
            return hasNext;
        }

        @Override
        public Edge next() {
            return edgeList.get(--index);
        }

        @Override
        public void close() {
            edgeList = null;
            super.close();
        }
    }
}
