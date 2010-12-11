package dela.container

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer

/**
 * @author vedi
 * date 23.06.2010
 * time 15:07:28
 */
class DomainLazyContainer extends LazyQueryContainer {

    def DomainLazyContainer(domainClass, selector, counter, columns, int batchSize = 500) {

        super(new DomainQueryFactory(domainClass: domainClass, selector: selector, counter: counter), batchSize);

        columns.each {
            addContainerProperty(it.field, it.type as Class, it.defaultValue, it.readOnly, it.sortable)
        }

    }
}
