package dela.ui.common

import com.vaadin.data.Container
import dela.context.DataContext

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
public class EntityTable extends AbstractEntityTable {

    protected Container createContainer(DataContext dataContext) {
        return vaadinService.createDefaultLazyContainer(dataContext)
    }

    protected void refreshContainer() {
        // TODO: Supports DomainLazyContainer only
        container.refresh()
    }
}
