package dela.ui.account

import com.vaadin.terminal.ExternalResource
import com.vaadin.ui.Label
import com.vaadin.ui.Link
import com.vaadin.ui.Window
import dela.StoreService

/**
 * @author vedi
 * date 21.07.2010
 * time 8:28:04
 */
class ConfirmRegistrationWindow extends Window {

    private String uuid

    def void handleParameters(Map<String, String[]> parameters) {
        super.handleParameters(parameters);

        uuid = parameters.get('uuid')?.getAt(0)

        removeAllComponents()

        if (uuid) {
            StoreService storeService = getBean(StoreService.class)

            if (storeService.confirmRegistration(uuid)) {
                application.refreshTopPanelContent()
                addComponent(new Label('registration.complete.message'))
                addComponent(new Link('main.redirect.message', new ExternalResource(application.mainWindow.URL)))
            } else {
                addComponent(new Label('registration.failed.message'))
            }

        }
   }

}
