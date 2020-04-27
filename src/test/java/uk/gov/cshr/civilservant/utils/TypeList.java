package uk.gov.cshr.civilservant.utils;

import java.lang.Iterable;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class TypeList<Type> implements Iterable<Type> {

    private Type[] arrayList;
    private int currentSize;

    public TypeList(Type[] newArray) {
        this.arrayList = newArray;
        this.currentSize = arrayList.length;
    }

    @Override
    public Iterator<Type> iterator() {
        Iterator<Type> it = new Iterator<Type>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                if(arrayList.length > 0) {
                    return arrayList[currentIndex] != null && currentIndex < currentSize;
                } else {
                    return false;
                }
            }

            @Override
            public Type next() {
                return arrayList[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    @Override
    public void forEach(Consumer<? super Type> action) {

    }

    @Override
    public Spliterator<Type> spliterator() {
        return null;
    }

}