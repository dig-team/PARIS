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
package bak.pcj.map;

/**
 *  This interface represents iterators over maps from
 *  objects values to byte values.
 *
 *  @see        bak.pcj.map.ObjectKeyByteMap
 *  @see        bak.pcj.ByteIterator
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/3/3
 *  @since      1.1
 */
public interface ObjectKeyByteMapIterator {

    /**
     *  Indicates whether more entries can be returned by this
     *  iterator.
     *
     *  @return     <tt>true</tt> if more byte entries can be returned
     *              by this iterator; returns <tt>false</tt>
     *              otherwise.
     *
     *  @see        #next()
     */
    boolean hasNext();

    /**
     *  Advances to the next entry of this iterator.
     *
     *  @throws     java.util.NoSuchElementException
     *              if no more entries are available from this
     *              iterator.
     *
     *  @see        #hasNext()
     */
    void next();

    /**
     *  Removes the last entry value returned from the underlying
     *  map.
     *
     *  @throws     UnsupportedOperationException
     *              if removal is not supported by this iterator.
     *
     *  @throws     IllegalStateException
     *              if no entry has been returned by this iterator
     *              yet.
     */
    void remove();

    /**
     *  Returns the key of the current entry of this iterator.
     *
     *  @return     the key of the current entry of this iterator.
     *
     *  @throws     IllegalStateException
     *              if there is no current entry (i.e. if
     *              {@link #next() next()}
     *              has not been called or
     *              {@link #remove() remove()}
     *              has just been called.
     *
     *  @see        #getValue()
     */
    Object getKey();

    /**
     *  Returns the value of the current entry of this iterator.
     *
     *  @return     the value of the current entry of this iterator.
     *
     *  @throws     IllegalStateException
     *              if there is no current entry (i.e. if
     *              {@link #next() next()}
     *              has not been called or
     *              {@link #remove() remove()}
     *              has just been called.
     *
     *  @see        #getKey()
     */
    byte getValue();

}
