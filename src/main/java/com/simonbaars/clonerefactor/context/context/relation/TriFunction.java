package com.simonbaars.clonerefactor.context.context.relation;

@FunctionalInterface
interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}