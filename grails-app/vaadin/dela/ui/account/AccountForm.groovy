package dela.ui.account

import com.vaadin.data.Item
import com.vaadin.ui.Button.ClickEvent
import dela.Account
import dela.ui.common.EntityForm
import com.vaadin.ui.*

/**
 * @author vedi
 * date 30.11.10
 * time 6:41
 */
class AccountForm extends EntityForm implements FormFieldFactory {

    AccountForm() {
        this.formFieldFactory = this
    }

    protected void initButtons(ComponentContainer componentContainer) {
        super.initButtons(componentContainer);

        if (editable) {
        }
    }

    def void buttonClick(ClickEvent clickEvent) {
        super.buttonClick(clickEvent)
    }

    Field createField(Item item, Object propertyId, Component component) {
        String caption = getColumnLabel(propertyId)
        Field field = null
        if ('role'.equals(propertyId)) {
            def select = field = new OptionGroup(caption: caption, immediate: true)
            select.addItem(Account.ROLE_ANONYMOUS)
            select.addItem(Account.ROLE_USER)
            select.addItem(Account.ROLE_ADMIN)
        } else if ('state'.equals(propertyId)) {
            def select = field = new OptionGroup(caption: caption, immediate: true)
            select.addItem(Account.STATE_ACTIVE)
            select.addItem(Account.STATE_CREATING)
            select.addItem(Account.STATE_BLOCKED)
        } else {
            TextField textField = field = new TextField(caption)
            textField.setNullRepresentation('')
        }

        addDomainValidator(field, item, propertyId)

        return field
    }

}
