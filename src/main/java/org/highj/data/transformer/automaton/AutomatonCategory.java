/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.highj.data.transformer.automaton;

import org.highj._;
import org.highj.__;
import org.highj.___;
import org.highj.data.transformer.Automaton;
import org.highj.data.tuple.T2;
import org.highj.typeclass2.arrow.Arrow;
import org.highj.typeclass2.arrow.Category;

/**
 *
 * @author clintonselke
 */
public interface AutomatonCategory<A> extends Category<_<Automaton.µ,A>> {
    
    public Arrow<A> get();

    @Override
    public default <B> Automaton<A,B,B> identity() {
        return () -> get().arr((B b) -> T2.of(b, identity()));
    }

    @Override
    public default <B, C, D> Automaton<A,B,D> dot(__<_<Automaton.µ, A>, C, D> cd, __<_<Automaton.µ, A>, B, C> bc) {
        return () -> get().dot(
            get().dot(
                get().arr((T2<T2<D,Automaton<A,C,D>>,Automaton<A,B,C>> x) -> T2.of(x._1()._1(), dot(x._1()._2(), x._2()))),
                get().<C,T2<D,Automaton<A,C,D>>,Automaton<A,B,C>>first(Automaton.narrow(cd).unAutomaton())
            ),
            Automaton.narrow(bc).unAutomaton()
        );
    }
}
