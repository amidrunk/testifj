package org.testifj;

@FunctionalInterface
public interface Procedure {

    void call() throws Exception;

    interface WithoutException extends Procedure {

        @Override
        void call();

    }

}
