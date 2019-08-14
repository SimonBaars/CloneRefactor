package com.simonbaars.clonerefactor.context.relation;

@FunctionalInterface
interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}