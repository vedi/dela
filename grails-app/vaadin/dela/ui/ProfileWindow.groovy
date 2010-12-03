package dela.ui

import com.vaadin.data.util.BeanItem
import com.vaadin.ui.Form
import com.vaadin.ui.Window
import dela.AccountService
import dela.context.DataContext
import dela.ui.account.AccountForm
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 02.12.2010
 * time 17:52:45
 */
class ProfileWindow extends Window {

    def sessionContext
    def dataContext

    def void attach() {

        super.attach();

        def vaadinService = getBean(dela.VaadinService)

        this.caption = i18n('entity.account.caption', 'account')

        dataContext = new DataContext(sessionContext: sessionContext, metaDomain: sessionContext.metaProvider.accountMeta)

        def accountItem = new BeanItem(sessionContext.account)

        EntityForm entityForm = new AccountForm()
        entityForm.dataContext = this.dataContext
        entityForm.editable = true

        entityForm.setItemDataSource(accountItem, vaadinService.getEditVisibleColumns(dataContext))
        entityForm.saveHandler = saveAccount

        this.addComponent(entityForm)

        this.layout.setSizeUndefined()
        this.center()

        entityForm.layout.components[0].focus()
    }

    def saveAccount = {item ->
        def accountService = getBean(AccountService)
        accountService.save(dataContext, item.bean)
    }

}
