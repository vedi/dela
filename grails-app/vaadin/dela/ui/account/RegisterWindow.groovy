package dela.ui.account

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField
import com.vaadin.ui.Window
import dela.Account
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 20.07.2010
 * time 19:21:06
 */
class RegisterWindow  extends Window implements FormFieldFactory {

    def form
    Account account

    TextField loginField

    def registerCallback

    def register = {item ->
        registerCallback(account)
    }

    def RegisterWindow(registerCallback) {

        this.registerCallback = registerCallback
        this.caption = i18n("window.register.caption", "register")

        form = new EntityForm()

        account = new Account()

        form.editable = true
        form.formFieldFactory = this
        form.itemDataSource = new BeanItem(account)
        form.visibleItemProperties = ['login', 'email', 'password']

        form.saveHandler = register

        this.addComponent(form)

        this.modal = true;

        this.layout.setSizeUndefined()
        this.center()
    }

    def void attach() {
        super.attach();
        loginField.focus()
    }

    Field createField(Item item, Object propertyId, Component uiContext) {
        String label = i18n("entity.account.field.${propertyId}.label", propertyId)
        if (propertyId == 'login') {
            loginField = new TextField(label)
            loginField.setNullRepresentation('')
            return loginField
        } else if (propertyId == 'email') {
            TextField textField = new TextField(label)
            textField.setNullRepresentation('')
            return textField
        } else if (propertyId == 'password') {
            TextField textField = new TextField(label)
            textField.secret = true
            textField.setNullRepresentation('')
            return textField
        } else {
            return null
        }
    }
}
