package utils;

import java.util.Map;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 25-mrt-2013, 10:26:57
 */
public final class Utils {

    public static <T, E> T getKeyByValue( Map<T, E> map, E value ) {
        for ( Map.Entry<T, E> entry : map.entrySet() ) {
            if ( value.equals( entry.getValue() ) ) {
                return entry.getKey();
            }
        }
        return null;
    }

}
