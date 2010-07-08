package dela.container

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer

/**
 * @author vedi
 * date 23.06.2010
 * time 15:07:28
 */
class DomainContainer extends LazyQueryContainer {

    def DomainContainer(domainClass, selector, counter, columns) {

        super(new DomainQueryFactory(domainClass: domainClass, selector: selector, counter: counter));

        columns.each {
            addContainerProperty(it.field, it.type as Class, it.defaultValue, it.readOnly, it.sortable)
        }

    }
}
