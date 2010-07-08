package dela.container

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer

/**
 * @author vedi
 * date 23.06.2010
 * time 15:07:28
 */
class DomainContainer extends LazyQueryContainer {

    def DomainContainer(domainView, shortList = false) {

        super(new DomainQueryFactory(
                domainClass: domainView.domainClass,
                selector: domainView.selector,
                counter: domainView.counter,
        ));

        def columns = shortList ? domainView.shortColumns : domainView.columns

        columns.each {
            addContainerProperty(it.field, it.type as Class, it.defaultValue, it.readOnly, it.sortable)
        }

    }
}
