package dela.common

/**
 * @author vedi
 * date 06.08.2010
 * time 19:42:09
 */
class ItemUtils {

    public static void itemToDomain(item, domain) {
        item.getItemPropertyIds().each {
            if (!'id'.equals(it)) {
                domain[it] = item.getItemProperty(it).value;
            }
        }
    }
}
