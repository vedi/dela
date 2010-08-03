package dela.ui.common

import dela.container.DomainLazyContainer

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
public class EntityTable extends AbstractEntityTable {

    def counter = {
        metaDomain.domainClass.count()
    }

    def selector = {startIndex, count, sortProperty, ascendingState ->
        if (sortProperty) {
            metaDomain.domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
        } else {
            metaDomain.domainClass.list(offset:startIndex,  max:count)
        }
    }

    protected DomainLazyContainer createContainer(metaDomain) {
        return new DomainLazyContainer(metaDomain.domainClass, getSelector(), getCounter(), metaDomain.columns)
    }

    protected void refreshContainer() {
        ((DomainLazyContainer)container).refresh()
    }
}
