package dela.ui.account

import com.vaadin.data.util.BeanItem
import com.vaadin.ui.Form
import com.vaadin.ui.Window
import dela.AccountService
import dela.context.DataContext
import dela.ui.account.AccountForm
import dela.ui.common.EntityForm
import dela.Account

/**
 * @author vedi
 * date 02.12.2010
 * time 17:52:45
 */
class ProfileWindow extends Window {

    def sessionContext
    def dataContext

    def accountService

    def void attach() {

        accountService = getBean(AccountService)

        super.attach();

        this.caption = i18n('entity.account.caption', 'account')

        def accountService = getBean(dela.AccountService)
        assert accountService

        dataContext = accountService.createDataContext(sessionContext)
        def accountItem = new BeanItem(sessionContext.account)

        EntityForm entityForm = new AccountForm()
        entityForm.dataContext = this.dataContext
        entityForm.dataService = accountService
        entityForm.editable = true

        entityForm.data = accountItem
        entityForm.saveHandler = saveAccount

        this.addComponent(entityForm)

        this.layout.setSizeUndefined()
        this.center()

        entityForm.layout.components[0].focus()
    }

    def saveAccount = {item ->
        accountService.save(dataContext, item.bean)
    }

}
