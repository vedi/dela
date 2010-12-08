package dela.ui.account

import com.vaadin.data.Container
import dela.IDataService
import dela.context.DataContext
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 25.11.2010
 * time 09:14:37
 */
class AccountTable extends EntityTable {

    def vaadinService

    def gridVisibleColumns = ['login', 'email']

    def AccountTable() {
        this.vaadinService = getBean(dela.VaadinService.class)
    }

    protected IDataService initDataService() {
        return getBean(dela.AccountService.class)
    }

    protected Container createContainer(DataContext dataContext) {
        return vaadinService.createAccountDefaultContainer(dataContext, gridVisibleColumns)
    }

    protected EntityForm createForm() {
        return new AccountForm()
    }

}
