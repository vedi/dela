package dela.ui.account

import com.vaadin.data.Container
import com.vaadin.data.Item
import com.vaadin.ui.Button.ClickEvent
import dela.IDataService
import dela.MessageService
import dela.AccountService
import dela.VaadinService
import dela.context.DataContext
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable
import com.vaadin.ui.*
import dela.Account

/**
 * @author vedi
 * date 25.11.2010
 * time 09:14:37
 */
class AccountTable extends EntityTable implements FormFieldFactory {

    def vaadinService
    def messageService
    def accountService

    def gridVisibleColumns = ['login', 'email']
    def formFieldFactory = this

    def AccountTable() {
        this.vaadinService = getBean(VaadinService.class)
        this.messageService = getBean(MessageService.class)
    }

    protected IDataService initDataService() {
        return getBean(AccountService.class)
    }

    protected Container createContainer(DataContext dataContext) {
        return vaadinService.createAccountDefaultContainer(dataContext)
    }

    Field createField(Item item, Object propertyId, Component component) {
        String caption = getColumnLabel(propertyId)
        if ('isPublic'.equals(propertyId)) {
            def checkBox = new CheckBox(caption)
            checkBox.readOnly = !this.dataContext.account.isAdmin()
            checkBox
        } else if ('role'.equals(propertyId)) {
            def select = new OptionGroup(caption: caption, immediate: true)
            select.addItem(Account.ROLE_ANONYMOUS)
            select.addItem(Account.ROLE_USER)
            select.addItem(Account.ROLE_ADMIN)

            select

        } else if ('state'.equals(propertyId)) {
            def select = new OptionGroup(caption: caption, immediate: true)
            select.addItem(Account.STATE_ACTIVE)
            select.addItem(Account.STATE_CREATING)
            select.addItem(Account.STATE_BLOCKED)

            select

        } else {
            TextField textField = new TextField(caption)
            textField.setNullRepresentation('')

            textField
        }

    }

    protected EntityForm createForm() {
        return new AccountForm()
    }

    class AccountForm extends EntityForm {

        protected void initButtons(ComponentContainer componentContainer) {
            super.initButtons(componentContainer);

            if (editable) {
            }
        }

        def void buttonClick(ClickEvent clickEvent) {
            super.buttonClick(clickEvent)
        }
    }

}
