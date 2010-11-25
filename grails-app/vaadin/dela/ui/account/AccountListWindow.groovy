package dela.ui.account

import com.vaadin.ui.Window
import dela.context.DataContext

/**
 * @author vedi
 * date 25.11.2010
 * time 09:11:45
 */
class AccountListWindow extends Window {

    def sessionContext

    def void attach() {

        super.attach()

        def dataContext = new DataContext(sessionContext: sessionContext, metaDomain: sessionContext.metaProvider.accountMeta)

        def table = new AccountTable(dataContext: dataContext)
        table.setWidth "100%"
        table.setHeight "100%"
        this.addComponent(table)
        this.content.setWidth "300px"
        this.content.setHeight "350px"
        this.center()
    }
}
