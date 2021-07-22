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
package bak.pcj.benchmark;

import bak.pcj.list.IntList;
import bak.pcj.list.IntArrayDeque;

/**
 *  This class represents benchmark tests for 
 *  {@link IntArrayDeque IntArrayDeque}s.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/9/1
 *  @since      1.0
 */
public class IntArrayDequeBenchmark extends IntListBenchmark {

    public IntArrayDequeBenchmark() {
        super(
            new IntListFactory() {
                public IntList create(int[] elements) {
                    IntList s = new IntArrayDeque(elements.length);
                    for (int i = 0; i < elements.length; i++)
                        s.add(elements[i]);
                    return s;
                }
            }
        );
    }

}
