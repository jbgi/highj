package org.highj.data.transformer.maybe;

import org.highj._;
import org.highj.data.collection.Maybe;
import org.highj.data.transformer.MaybeT;
import org.highj.typeclass1.monad.Bind;
import org.highj.typeclass1.monad.Monad;

import java.util.function.Function;

/**
 * @author Clinton Selke
 */
public interface MaybeTBind<M> extends MaybeTApply<M>, Bind<_<MaybeT.µ, M>> {

    Monad<M> get();

    @Override
    default <A, B> MaybeT<M, B> bind(_<_<MaybeT.µ, M>, A> nestedA, Function<A, _<_<MaybeT.µ, M>, B>> fn) {
        _<M, Maybe<B>> m_maybeB = get().bind(
                MaybeT.narrow(nestedA).get(),
                maybeA -> maybeA.cata(
                        get().pure(Maybe.<B>newNothing()),
                        (A a) -> MaybeT.narrow(fn.apply(a)).get()
                )
        );
        return new MaybeT<>(m_maybeB);
    }
}