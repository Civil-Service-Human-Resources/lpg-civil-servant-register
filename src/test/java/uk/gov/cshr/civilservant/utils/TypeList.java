package uk.gov.cshr.civilservant.utils;

import java.util.Iterator;

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
                return currentIndex < currentSize && arrayList[currentIndex] != null;
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

}