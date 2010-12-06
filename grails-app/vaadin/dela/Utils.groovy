package dela

import com.vaadin.data.util.BeanItem

/**
 * @author vedi
 * date 06.12.10
 * time 17:43
 */
@Category(Object)
class Utils {

    final public def getDomain(item) {
        (item as BeanItem).bean
    }
}
