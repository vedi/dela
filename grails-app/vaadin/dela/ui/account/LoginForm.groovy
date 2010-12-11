package dela.ui.account

import com.vaadin.data.Item
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import dela.ui.common.EntityForm
import com.vaadin.ui.*

/**
 * @author vedi
 * date 10.12.10
 * time 18:22
 */
class LoginForm extends EntityForm implements FormFieldFactory, ClickListener  {

    def formFields = ['login', 'password']

    def loginCallback
    def forgetPasswordCallback

    def login = {item ->
        def account = item.bean
        loginCallback(account.login, account.password)
    }

    private TextField loginField
    private Button forgetPasswordButton

    def LoginForm() {
        this.formFieldFactory = this
        this.saveHandler = login
        this.messageService = getBean(dela.MessageService)
    }

    def void attach() {
        super.attach();

        loginField.focus()
    }

    protected void initButtons(ComponentContainer componentContainer) {
        super.initButtons(componentContainer)

        forgetPasswordButton = new Button()
        forgetPasswordButton.caption = messageService.getForgetPasswordButtonLabelMsg()
        forgetPasswordButton.addListener(this as ClickListener)
        componentContainer.addComponent forgetPasswordButton
    }

    Field createField(Item item, Object propertyId, Component uiContext) {
        String label = messageService.getFieldLabelMsg('account', propertyId.toString())
        if (propertyId == 'login') {
            loginField = new TextField(label)
            loginField.setNullRepresentation('')
            return loginField
        } else if (propertyId == 'password') {
            TextField textField = new TextField(label)
            textField.secret = true
            textField.setNullRepresentation('')
            return textField
        } else {
            throw new IllegalArgumentException("Wrong propertyId = [$propertyId]")
        }
    }

    void buttonClick(ClickEvent event) {
        if (event.button == this.forgetPasswordButton) {
            forgetPasswordCallback()
            this.window.close()
        } else {
            super.buttonClick(event)
        }
    }

}
