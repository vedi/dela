package dela

import com.vaadin.data.util.BeanItem

/**
 * @author vedi
 * date 06.12.10
 * time 17:43
 */
@Mixin
class Utils {

    final protected def getDomain(item) {
        (item as BeanItem).bean
    }
}
