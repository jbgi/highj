package org.highj.data.transformer;

import org.derive4j.Data;
import org.derive4j.Derive;
import org.derive4j.Visibility;
import org.derive4j.hkt.__;
import org.derive4j.hkt.__3;
import org.highj.data.Either;
import org.highj.data.Maybe;
import org.highj.function.F1;
import org.highj.data.tuple.T0;
import org.highj.data.tuple.T1;
import org.highj.data.tuple.T2;
import org.highj.typeclass1.monad.Monad;
import org.highj.typeclass1.monad.MonadRec;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.highj.data.transformer.generator.GeneratorTApplicative;
import org.highj.data.transformer.generator.GeneratorTApply;
import org.highj.data.transformer.generator.GeneratorTBind;
import org.highj.data.transformer.generator.GeneratorTFunctor;
import org.highj.data.transformer.generator.GeneratorTMonad;
import org.highj.data.transformer.generator.GeneratorTMonadRec;
import org.highj.data.transformer.generator.GeneratorTMonadTrans;

/**
 * JavaScript / Python style generator, expressed as a Monad.
 * 
 * @param <E> The element type that gets yielded.
 * @param <M> The base monad type.
 * @param <A> The final return type after the full execution.
 */
@Data(@Derive(inClass = "GeneratorTImpl", withVisibility = Visibility.Package))
public abstract class GeneratorT<E,M,A> implements __3<GeneratorT.µ,E,M,A> {
    public static class µ {
    }

    public static <E,M,A> GeneratorT<E,M,A> narrow(__<__<__<µ,E>,M>,A> a) {
        return (GeneratorT<E,M,A>)a;
    }

    public interface Cases<R,E,M,A> {
        R done(A result);
        R emit(E emitValue, GeneratorT<E,M,A> rest);
        R bind(Bound<E,M,?,A> bound);
        R suspend(Supplier<GeneratorT<E,M,A>> suspend);
        R lift(__<M,A> ma);
    }

    public abstract <R> R match(Cases<R,E,M,A> cases);

    public static class Bound<E,M,A,B> {
        private final GeneratorT<E,M,A> _m;
        private final F1<A,GeneratorT<E,M,B>> _f;

        private Bound(GeneratorT<E,M,A> m, F1<A,GeneratorT<E,M,B>> f) {
            this._m = m;
            this._f = f;
        }

        public static <E,M,A,B> Bound<E,M,A,B> mkBound(GeneratorT<E,M,A> m, F1<A,GeneratorT<E,M,B>> f) {
            return new Bound(m, f);
        }

        public GeneratorT<E,M,A> m() {
            return _m;
        }

        public F1<A,GeneratorT<E,M,B>> f() {
            return _f;
        }
    }

    /**
     * Executes a step of the generator.
     * 
     * @param mMonadRec The base monad. (It must support a MonadRec instance.)
     * @return Either a completion value (left side of Either), or an yielded element tupled with the remaining
     *         computation to be resumed later (right side of Either).
     */
    public __<M,Either<A, T2<E,GeneratorT<E,M,A>>>> run(MonadRec<M> mMonadRec) {
        return mMonadRec.tailRec(
            GeneratorTImpl
                .<E,M,A>cases()
                .done((A result) -> mMonadRec.pure(Either.<GeneratorT<E,M,A>,Either<A, T2<E,GeneratorT<E,M,A>>>>Right(Either.<A,T2<E,GeneratorT<E,M,A>>>Left(result))))
                .emit((E emitValue, GeneratorT<E, M, A> rest) -> mMonadRec.pure(Either.<GeneratorT<E,M,A>,Either<A, T2<E,GeneratorT<E,M,A>>>>Right(Either.<A,T2<E,GeneratorT<E,M,A>>>Right(T2.of(emitValue, rest)))))
                .bind((Bound<E,M,?,A> bound) -> runBound(mMonadRec, bound))
                .suspend((Supplier<GeneratorT<E,M,A>> suspend) -> mMonadRec.pure(Either.<GeneratorT<E,M,A>,Either<A, T2<E,GeneratorT<E,M,A>>>>Left(suspend.get())))
                .lift((__<M,A> ma) -> mMonadRec.map(
                    (A a) -> Either.<GeneratorT<E,M,A>,Either<A, T2<E,GeneratorT<E,M,A>>>>Right(Either.<A,T2<E,GeneratorT<E,M,A>>>Left(a)),
                    ma
                )),
            this
        );
    }

    /**
     * Converts a Generator based on the identity monad into a Java Iterator.
     * 
     * @param <E> The element type that gets yielded.
     * @param generator The generator to be converted into an iterator.
     * @return An iterator that will produce the same elements as the generator.
     */
    public static <E> Iterator<E> toIterator(GeneratorT<E,T1.µ,T0> generator) {
        final Maybe<T2<E,GeneratorT<E,T1.µ,T0>>> initState = T1.narrow(generator.run(T1.monadRec))._1().maybeRight();
        return new Iterator<E>() {
            private Maybe<T2<E,GeneratorT<E,T1.µ,T0>>> state = initState;
            @Override
            public boolean hasNext() {
                return state.isJust();
            }
            @Override
            public E next() {
                if (state.isNothing()) {
                    throw new NoSuchElementException();
                }
                T2<E,GeneratorT<E,T1.µ,T0>> x = state.get();
                E element = x._1();
                state = T1.narrow(x._2().run(T1.monadRec))._1().maybeRight();
                return element;
            }
        };
    }

    public static <E,M,A,B> __<M,Either<GeneratorT<E,M,A>,Either<A,T2<E,GeneratorT<E,M,A>>>>> runBound(Monad<M> mMonad, Bound<E,M,B,A> bound) {
        return GeneratorTImpl
            .<E,M,B>cases()
            .done((B b) -> mMonad.pure(Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Left(bound.f().apply(b))))
            .emit((E e, GeneratorT<E, M, B> rest) -> mMonad.pure(Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Right(Either.<A, T2<E, GeneratorT<E, M, A>>>Right(T2.of(e, GeneratorT.bind(rest, bound.f()))))))
            .bind((Bound<E, M, ?, B> bound2) ->
                mMonad.map(
                    (Either<GeneratorT<E, M, B>, Either<B, T2<E, GeneratorT<E, M, B>>>> x) ->
                        x.<Either<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>>either(
                            (GeneratorT<E, M, B> genB) ->
                                Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Left(GeneratorT.bind(genB, bound.f())),
                            (Either<B, T2<E, GeneratorT<E, M, B>>> x2) ->
                                x2.either(
                                    (B b) ->
                                        Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Left(bound.f().apply(b)),
                                    (T2<E, GeneratorT<E, M, B>> x3) ->
                                        Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Right(Either.<A, T2<E, GeneratorT<E, M, A>>>Right(T2.of(x3._1(), GeneratorT.bind(x3._2(), bound.f()))))
                                )
                        ),
                    runBound(mMonad, bound2)
                )
            )
            .suspend((Supplier<GeneratorT<E,M,B>> suspend) -> mMonad.pure(Either.Left(GeneratorT.bind(suspend.get(), bound.f()))))
            .lift((__<M, B> mb) -> mMonad.map((B b) -> Either.<GeneratorT<E, M, A>, Either<A, T2<E, GeneratorT<E, M, A>>>>Left(bound.f().apply(b)), mb))
            .apply(bound.m());
    }

    /**
     * A Generator that has finished.
     * 
     * @param <E> Element type.
     * @param <M> Base monad.
     * @param <A> Finished return type.
     * @param r The finished return value.
     * @return A generator in its finished state returning value r.
     */
    public static <E,M,A> GeneratorT<E,M,A> done(A r) {
        return GeneratorTImpl.done(r);
    }

    public static <E,M,A> GeneratorT<E,M,A> yield(E a, GeneratorT<E,M,A> rest) {
        return GeneratorTImpl.emit(a, rest);
    }

    /**
     * Produces a generator that will suspend its computation returning a yielded value,
     * plus the rest of the computation to be completed later.
     * 
     * @param <E> Element type.
     * @param <M> Base monad.
     * @param e The yielded element value.
     * @return The generator that performs the yield.
     */
    public static <E,M> GeneratorT<E,M,T0> yield(E e) {
        return yield(e, done(T0.of()));
    }

    public static <E,M,A,B> GeneratorT<E,M,B> bind(GeneratorT<E,M,A> ma, F1<A,GeneratorT<E,M,B>> f) {
        return GeneratorTImpl
            .<E,M,A>cases()
            .done((A a) -> f.apply(a))
            .bind((Bound<E,M,?,A> bound) -> reassociateBind(bound, f))
            .otherwise(() -> GeneratorTImpl.bind(Bound.mkBound(ma, f)))
            .apply(ma);
    }

    private static <E,M,A,B,C> GeneratorT<E,M,B> reassociateBind(Bound<E,M,C,A> m, F1<A,GeneratorT<E,M,B>> f) {
        return suspend(() -> bind(m.m(), (C c) -> bind(m.f().apply(c), f)));
    }

    public static <E,M,A> GeneratorT<E,M,A> suspend(Supplier<GeneratorT<E,M,A>> a) {
        return GeneratorTImpl.suspend(a);
    }

    public static <E,M,A> GeneratorT<E,M,A> lift(__<M,A> ma) {
        return GeneratorTImpl.lift(ma);
    }

    public static <E,M> GeneratorTFunctor<E,M> functor() {
        return new GeneratorTFunctor<E,M>() {};
    }

    public static <E,M> GeneratorTApply<E,M> apply() {
        return new GeneratorTApply<E,M>() {};
    }

    public static <E,M> GeneratorTApplicative<E,M> applicative() {
        return new GeneratorTApplicative<E,M>() {};
    }

    public static <E,M> GeneratorTBind<E,M> bind() {
        return new GeneratorTBind<E,M>() {};
    }

    public static <E,M> GeneratorTMonad<E,M> monad() {
        return new GeneratorTMonad<E,M>() {};
    }

    public static <E,M> GeneratorTMonadTrans<E,M> monadTrans() {
        return new GeneratorTMonadTrans<E,M>() {};
    }
    
    public static <E,M> GeneratorTMonadRec<E,M> monadRec() {
        return new GeneratorTMonadRec<E,M>() {};
    }
}
