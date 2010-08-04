package dela.ui.account

import com.vaadin.terminal.ExternalResource
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.TextField
import com.vaadin.ui.Window
import dela.StoreService

/**
 * @author vedi
 * date 21.07.2010
 * time 8:28:04
 */
class ConfirmRegistrationWindow extends Window implements ClickListener {

    private String uuid

    private TextField password
    private TextField confirmPassword

    def void handleParameters(Map<String, String[]> parameters) {
        super.handleParameters(parameters);

        uuid = parameters.get('uuid')?.getAt(0)

        removeAllComponents()

        if (uuid) {
            buildComponents()
        }
   }

    def buildComponents() {

        password = new TextField()
        password.caption = i18n('field.password.label');
        password.secret = true
        password.setNullRepresentation('')
        addComponent(password)

        confirmPassword = new TextField()
        confirmPassword.caption = i18n('field.confirmPassword.label');
        confirmPassword.secret = true
        confirmPassword.setNullRepresentation('')
        addComponent(confirmPassword)

        Button button = new Button()
        button.caption = i18n('button.ok.label');
        button.addListener(this)
        addComponent(button)

    }

    void buttonClick(ClickEvent event) {
        assert confirmPassword.value.equals(password.value)

        StoreService storeService = getBean(StoreService.class)

        if (storeService.confirmRegistration(uuid, password.value.toString())) {
            application.refreshTopPanelContent()
            open(new ExternalResource(application.mainWindow.URL))
            application.mainWindow.showNotification(i18n('confirmation.complete.message'))
        } else {
            showNotification(i18n('confirmation.failed.message'))
        }
    }
}
