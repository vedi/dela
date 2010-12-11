package dela.ui.account

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField
import com.vaadin.ui.Window
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 30.07.2010
 * time 08:16:06
 */
class ForgetPasswordWindow extends Window implements FormFieldFactory {

    class ForgetPasswordBean {
        String email
    }

    def messageService
    def form

    TextField emailField

    def resetPasswordCallback

    def resetPassword = {item ->
        resetPasswordCallback(item.getItemProperty('email').value)
    }

    def ForgetPasswordWindow(resetPasswordCallback) {

        this.resetPasswordCallback = resetPasswordCallback

        messageService = getBean(dela.MessageService)
        this.caption = messageService.getForgetPasswordWindowCaptionMsg()

        form = new EntityForm()

        form.editable = true
        form.formFieldFactory = this
        form.data = new BeanItem(new ForgetPasswordBean())
        form.formFields = ['email']

        form.saveHandler = resetPassword

        this.addComponent(form)

        this.modal = true;

        this.layout.setSizeUndefined()
        this.center()
    }

    def void attach() {
        super.attach();
        emailField.focus()
    }

    Field createField(Item item, Object propertyId, Component uiContext) {
        String label = messageService.getFieldLabelMsg('account', propertyId.toString())
        if (propertyId == 'email') {
            emailField = new TextField(label)
            emailField.setNullRepresentation('')
            return emailField
        } else {
            return null
        }
    }
}