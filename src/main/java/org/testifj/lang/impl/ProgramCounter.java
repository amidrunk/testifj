package org.testifj.lang.impl;

import org.testifj.lang.InvocationTargetRuntimeException;
import org.testifj.lang.Procedure;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ProgramCounter {

    private final List<LookAhead> lookAheadList = new LinkedList<>();

    private int value;

    public ProgramCounter() {
        this(0);
    }

    public ProgramCounter(int value) {
        this.value = value;
    }

    public void lookAhead(int targetPc, Procedure procedure) {
        assert targetPc > value : "Target PC must be > current PC (" + value + ")";
        assert procedure != null : "Procedure can't be null";

        lookAheadList.add(new LookAhead(targetPc, procedure));
    }

    public void advance() {
        value++;

        for (Iterator<LookAhead> i = lookAheadList.iterator(); i.hasNext(); ) {
            final LookAhead lookAhead = i.next();

            if (lookAhead.getPc() == value) {
                try {
                    lookAhead.getProcedure().call();
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new InvocationTargetRuntimeException(e);
                    }
                }

                i.remove();
            }
        }
    }

    public int get() {
        return value;
    }

    private static final class LookAhead {

        private final int pc;

        private final Procedure procedure;

        private LookAhead(int pc, Procedure procedure) {
            this.pc = pc;
            this.procedure = procedure;
        }

        public int getPc() {
            return pc;
        }

        public Procedure getProcedure() {
            return procedure;
        }
    }

}
