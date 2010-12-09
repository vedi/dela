package dela.ui.account

import com.vaadin.data.Container
import dela.IDataService

import dela.ui.common.EntityForm
import dela.ui.common.EntityTable
import dela.DataView

/**
 * @author vedi
 * date 25.11.2010
 * time 09:14:37
 */
class AccountTable extends EntityTable {

    def gridFields = ['login', 'email']

    protected EntityForm createForm() {
        return new AccountForm()
    }

}
