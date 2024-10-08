package com.github.wrdlbrnft.primitivecollections.chars;

import com.github.wrdlbrnft.primitivecollections.PrimitiveCollection;
import androidx.annotation.Keep;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 09/12/2016
 */
@Keep
public interface CharCollection extends PrimitiveCollection {
    boolean contains(char value);
    boolean add(char value);
    char get(int index);
    char[] toArray();
}
