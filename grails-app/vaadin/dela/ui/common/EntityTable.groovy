package dela.ui.common

import com.vaadin.data.Container

import dela.DataView
import dela.container.DomainLazyContainer

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
public abstract class EntityTable extends AbstractEntityTable {

    protected Container createContainer(DataView dataView) {
        return new DomainLazyContainer(dataContext.domainClass,
                dataView.selector, dataView.counter,
                gridColumns)
    }

    protected void refreshContainer() {
        // TODO: Supports DomainLazyContainer only
        container.refresh()
    }
}
