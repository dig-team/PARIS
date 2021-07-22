/*
 *  Primitive Collections for Java.
 *  Copyright (C) 2002, 2003  S&oslash;ren Bak
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bak.pcj.set;

import bak.pcj.BooleanIterator;
import bak.pcj.BooleanCollection;
import bak.pcj.util.Exceptions;
import java.util.NoSuchElementException;

/**
 *  This class represents sets of boolean values. The elements of the
 *  set are represented by a single state variable:
 *  0 -&gt; {}, 1 -&gt; {F}, 2 -&gt; {T}, and 3 -&gt; {F, T}.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 20:22
 *  @since      1.0
 */
public class BooleanDirectSet extends AbstractBooleanSet {

    private int values;

    private static final int EMPTY      = 0;
    private static final int FALSE      = 1;
    private static final int TRUE       = 2;
    private static final int FALSETRUE  = 3;

    /**
     *  Creates a new empty set.
     */
    public BooleanDirectSet() {
        values = EMPTY;
    }

    /**
     *  Creates a new set with the same values as a specified
     *  collection.
     *
     *  @param      c
     *              the collection whose elements to add to the
     *              new set.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public BooleanDirectSet(BooleanCollection c) {
        this();
        addAll(c);
    }

    private void removeError() {
        Exceptions.noElementToRemove();
    }

    private void nextError() {
        Exceptions.endOfIterator();
    }

    public boolean contains(boolean v) {
        switch (values) {
        case EMPTY:     return false;
        case FALSE:     return !v;
        case TRUE:      return v;
        case FALSETRUE: return true;
        default:        throw new RuntimeException("Internal error");
        }
    }

    public boolean add(boolean v) {
        switch (values) {
        case EMPTY:     if (v) values = TRUE; else values = FALSE; return true;
        case FALSE:     if (v) { values = FALSETRUE; return true; } return false;
        case TRUE:      if (!v) { values = FALSETRUE; return true; } return false;
        case FALSETRUE: return false;
        default:        throw new RuntimeException("Internal error");
        }
    }

    public boolean remove(boolean v) {
        switch (values) {
        case EMPTY:     return false;
        case FALSE:     if (!v) { values=EMPTY; return true; } return false;
        case TRUE:      if (v) { values=EMPTY; return true; } return false;
        case FALSETRUE: if (v) values=FALSE; else values=TRUE; return true;
        default:        throw new RuntimeException("Internal error");
        }
    }

    public int size() {
        switch (values) {
        case EMPTY:     return 0;
        case FALSE:     return 1;
        case TRUE:      return 1;
        case FALSETRUE: return 2;
        default:        throw new RuntimeException("Internal error");
        }
    }

    public boolean isEmpty()
    { return values == EMPTY; }

    public void clear()
    { values = EMPTY; }

    public BooleanIterator iterator() {
        return new BIterator();
    }

    private class BIterator implements BooleanIterator {
        private static final int I_EMPTY    =  0;
        private static final int I_F_0      =  1;    // before F
        private static final int I_F_1      =  2;    // after F
        private static final int I_F_2      =  3;    // F removed
        private static final int I_T_0      =  4;    // before T
        private static final int I_T_1      =  5;    // after T
        private static final int I_T_2      =  6;    // T removed
        private static final int I_FT_0     =  7;    // before FT
        private static final int I_FT_1     =  8;    // between FT
        private static final int I_FT_2     =  9;    // between FT, F removed
        private static final int I_FT_3     = 10;    // after FT
        private static final int I_FT_4     = 11;    // after FT, T removed
        private static final int I_FT_5     = 12;    // after FT, F removed
        private static final int I_FT_6     = 13;    // after FT, FT removed

        private int state;

        BIterator() {
            switch (values) {
            case EMPTY:     state = I_EMPTY; break;
            case FALSE:     state = I_F_0; break;
            case TRUE:      state = I_T_0; break;
            case FALSETRUE: state = I_FT_0; break;
            default:        throw new RuntimeException("Internal error"); 
            }
        }

        public boolean hasNext() {
            switch (state) {
            case I_EMPTY: return false;
            case I_F_0: return true;
            case I_F_1: return false;
            case I_F_2: return false;
            case I_T_0: return true;
            case I_T_1: return false;
            case I_T_2: return false;
            case I_FT_0: return true;
            case I_FT_1: return true;
            case I_FT_2: return true;
            case I_FT_3: return false;
            case I_FT_4: return false;
            case I_FT_5: return false;
            case I_FT_6: return false;
            default: throw new RuntimeException("Internal error");
            }
        }

        public boolean next() {
            switch (state) {
            case I_EMPTY:
                nextError();
            case I_F_0:
                state = I_F_1;
                return false;
            case I_F_1:
                nextError();
            case I_F_2:
                nextError();
            case I_T_0:
                state = I_T_1;
                return true;
            case I_T_1:
                nextError();
            case I_T_2:
                nextError();
            case I_FT_0:
                state = I_FT_1;
                return false;
            case I_FT_1:
                state = I_FT_3;
                return true;
            case I_FT_2:
                state = I_FT_5;
                return true;
            case I_FT_3:
                nextError();
            case I_FT_4:
                nextError();
            case I_FT_5:
                nextError();
            case I_FT_6:
                nextError();
            default:
                throw new RuntimeException("Internal error"); 
            }
        }

        public void remove() {
            switch (state) {
            case I_EMPTY:
                removeError();
            case I_F_0:
                removeError();
            case I_F_1:
                values = EMPTY;
                state = I_F_2;
                break;
            case I_F_2:
                removeError();
            case I_T_0:
                removeError();
            case I_T_1:
                values = EMPTY;
                state = I_T_2;
                break;
            case I_T_2:
                removeError();
            case I_FT_0:
                removeError();
            case I_FT_1:
                values = TRUE;
                state = I_FT_2;
                break;
            case I_FT_2:
                removeError();
            case I_FT_3:
                values = FALSE;
                state = I_FT_4;
                break;
            case I_FT_4:
                removeError();
            case I_FT_5:
                values = EMPTY;
                state = I_FT_6;
                break;
            case I_FT_6:
                removeError();
            default:
                throw new RuntimeException("Internal error");
            }
        }
    }

}
