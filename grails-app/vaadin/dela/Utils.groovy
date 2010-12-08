package dela

import com.vaadin.data.util.BeanItem

/**
 * @author vedi
 * date 06.12.10
 * time 17:43
 */
@Category(Object)
class Utils {

    def messageService

    final public def getDomain(item) {
        (item as BeanItem).bean
    }

    final String getEntityListCaption(dataContext) {
        messageService.getEntityListCaptionMsg(dataContext.domainClass.simpleName.toLowerCase())
    }

    final String getEntityCaption(dataContext) {
        messageService.getEntityCaptionMsg(dataContext.domainClass.simpleName.toLowerCase())
    }

    final String getFieldLabel(dataContext, column) {
        messageService.getFieldLabelMsg(dataContext.domainClass.simpleName.toLowerCase(), column)
    }

    def List<String> getGridVisibleColumns(dataContext) {
        dataContext.domainClass.properties.collect {it.key}   //  TODO: Tests
    }

    def List<String> getEditVisibleColumns(dataContext) {
        getGridVisibleColumns(dataContext)
    }

}
