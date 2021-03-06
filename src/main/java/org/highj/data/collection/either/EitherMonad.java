package org.highj.data.collection.either;

import org.highj._;
import org.highj.__;
import org.highj.data.collection.Either;
import org.highj.typeclass1.monad.Monad;
import org.highj.typeclass1.monad.MonadRec;

import java.util.function.Function;

public interface EitherMonad<S> extends EitherFunctor<S>, Monad<_<Either.µ, S>>, MonadRec<_<Either.µ, S>> {
    @Override
    default <A> Either<S, A> pure(A a) {
        return Either.newRight(a);
    }

    @Override
    default <A, B> Either<S, B> ap(_<_<Either.µ, S>, Function<A, B>> fn, _<_<Either.µ, S>, A> nestedA) {
        //a <*> b = do x <- a; y <- b; return (x y)
        return Either.narrow(fn).<Either<S, B>>either(Either::newLeft,
                fnRight -> Either.narrow(nestedA).<Either<S, B>>either(
                        Either::newLeft,
                        right -> Either.newRight(fnRight.apply(right))));
    }

    @Override
    default <A, B> Either<S, B> bind(_<_<Either.µ, S>, A> a, Function<A, _<_<Either.µ, S>, B>> fn) {
        //lazyRight m >>= k = k m
        //lazyLeft e  >>= _ = lazyLeft e
        return Either.narrow(a).<Either<S,B>>either(Either::newLeft, right -> Either.narrow(fn.apply(right)));
    }

    @Override
    default <A, B> Either<S, B> tailRec(Function<A, _<_<Either.µ, S>, Either<A, B>>> function, A startValue) {
        Either<S,Either<A, B>> step = Either.newRight(Either.newLeft(startValue));
        while(step.isRight() && step.getRight().isLeft()) {
            step = Either.narrow(function.apply(step.getRight().getLeft()));
        }
        return step.rightMap(Either::getRight);
    }
}
