package org.highj.data.structural.constant;

import org.highj._;
import org.highj.__;
import org.highj.data.structural.Const;
import org.highj.typeclass1.functor.Functor;

import java.util.function.Function;

import static org.highj.data.structural.Const.µ;
import static org.highj.data.structural.Const.narrow;

public interface ConstFunctor<S> extends Functor<_<µ, S>> {
    @Override
    public default <A, B> Const<S, B> map(Function<A, B> fn, _<_<µ, S>, A> nestedA) {
        return new Const<>(narrow(nestedA).get());
    }
}
