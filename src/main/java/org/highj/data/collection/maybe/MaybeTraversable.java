package org.highj.data.collection.maybe;

import org.highj._;
import org.highj.data.collection.Maybe;
import org.highj.typeclass0.group.Monoid;
import org.highj.typeclass1.foldable.Traversable;
import org.highj.typeclass1.monad.Applicative;

import java.util.function.Function;

import static org.highj.data.collection.Maybe.narrow;
import static org.highj.data.collection.Maybe.µ;

//Todo extends to Traversable1
public interface MaybeTraversable extends MaybeFunctor, Traversable<µ> {

    @Override
    default <A, B> B foldMap(Monoid<B> mb, Function<A, B> fn, _<µ, A> as) {
        return narrow(as).<B>cata(mb.identity(), fn::apply);
    }

    @Override
    default <A, B> B foldr(Function<A, Function<B, B>> fn, B b, _<µ, A> as) {
        return narrow(as).cata(b, a -> fn.apply(a).apply(b));
    }

    @Override
    default <A, B> A foldl(Function<A, Function<B, A>> fn, A a, _<µ, B> bs) {
        return narrow(bs).cata(a, b -> fn.apply(a).apply(b));
    }

    @Override
    default <A, B> Maybe<B> map(Function<A, B> fn, _<µ, A> nestedA){
        return MaybeFunctor.super.map(fn, nestedA);
    }

    @Override
    default <A, B, X> _<X, _<µ, B>> traverse(Applicative<X> applicative, Function<A, _<X, B>> fn, _<µ, A> traversable) {
        //traverse _ Nothing = pure Nothing
        //traverse f (Just x) = Just <$> f x
        Maybe<A> maybe = narrow(traversable);
        return maybe.isNothing()
                ? applicative.<_<µ, B>>pure(Maybe.newNothing())
                : applicative.<B,_<µ, B>>map(Maybe::newJust, fn.apply(maybe.get()));
    }

}
