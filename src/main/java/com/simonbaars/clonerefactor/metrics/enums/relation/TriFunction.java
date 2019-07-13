package com.simonbaars.clonerefactor.metrics.enums.relation;
@FunctionalInterface
interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}