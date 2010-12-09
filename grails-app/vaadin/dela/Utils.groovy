package dela

import com.vaadin.data.util.BeanItem
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * @author vedi
 * date 06.12.10
 * time 17:43
 */
@Category(Object)
class Utils {

    final def getDomain(item) {
        (item as BeanItem).bean
    }

    final def getEntityListCaption(dataContext) {
        messageService.getEntityListCaptionMsg(dataContext.domainClass.simpleName.toLowerCase())
    }

    final String getEntityCaption(dataContext) {
        messageService.getEntityCaptionMsg(dataContext.domainClass.simpleName.toLowerCase())
    }

    final def getFieldLabel(dataContext, column) {
        messageService.getFieldLabelMsg(dataContext.domainClass.simpleName.toLowerCase(), column)
    }

    final def getFile(fileName) {
        return ApplicationHolder.application.parentContext.getResource(fileName).file
    }

}
