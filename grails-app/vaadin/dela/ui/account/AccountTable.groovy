package dela.ui.account

import dela.ui.common.EntityForm
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 25.11.2010
 * time 09:14:37
 */
class AccountTable extends EntityTable {

    def gridFields = ['login', 'email']

    protected def createForm() {
        return new AccountForm()
    }

}
