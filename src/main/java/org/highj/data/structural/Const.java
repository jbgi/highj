package org.highj.data.structural;

import org.highj._;
import org.highj.__;
import org.highj.data.structural.constant.*;
import org.highj.typeclass0.group.Monoid;
import org.highj.typeclass0.group.Semigroup;
import org.highj.typeclass1.contravariant.Contravariant;
import org.highj.typeclass1.functor.Functor;
import org.highj.typeclass1.monad.Applicative;
import org.highj.typeclass1.monad.Apply;

public class Const<A, B> implements __<Const.µ, A, B> {

    public static class µ {
    }

    private final A value;

    public Const(A value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <A, B> Const<A, B> narrow(_<_<µ, A>, B> value) {
        return (Const) value;
    }

    public A get() {
        return value;
    }

    public static <S> ConstFunctor<S> functor() {
        return new ConstFunctor<S>() {
        };
    }

    public static <S> ConstApply<S> apply(final Semigroup<S> semigroup) {
        return () -> semigroup;
    }

    public static <S> ConstApplicative<S> applicative(final Monoid<S> monoid) {
        return () -> monoid;
    }

    public static <S> ConstContravariant<S> contravariant() {
        return new ConstContravariant<S>() {
        };
    }

    public static final ConstBiapplicative biapplicative = new ConstBiapplicative() {
    };
}
