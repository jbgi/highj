package org.highj.data.structural;

import org.highj._;
import org.highj.__;
import org.highj.data.structural.dual.DualCategory;
import org.highj.typeclass2.arrow.Category;

public class Dual<M, A, B> implements __<_<Dual.µ, M>, A, B> {

    public static class µ {}

    private final __<M, B, A> value;

    public Dual(__<M, B, A> value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <M, A, B> Dual<M, A, B> narrow(__<_<Dual.µ, M>, A, B> dual) {
        return (Dual) dual;
    }

    @SuppressWarnings("unchecked")
    public static <M, A, B> Dual<M, A, B> narrow(_<_<_<Dual.µ, M>, A>, B> dual) {
        return (Dual) dual;
    }

    public __<M, B, A> get() {
        return value;
    }

    public static <M,A,B> __<M, B, A> get(__<_<Dual.µ, M>, A, B> dual) {
        return narrow(dual).get();
    }

    public static <M,A,B> __<M, B, A> get(_<_<_<Dual.µ, M>, A>, B> dual) {
        return narrow(dual).get();
    }

    public static <M> DualCategory<M> category(final Category<M> category) {
        return () -> category;
    }

}
