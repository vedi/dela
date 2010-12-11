package dela.ui.account

import com.vaadin.data.util.BeanItem
import com.vaadin.ui.Window
import dela.Account
import dela.MessageService

/**
 * @author vedi
 * date 20.07.2010
 * time 8:34:32
 */
class LoginWindow extends Window {

    MessageService messageService

    def form
    Account account

    def LoginWindow(loginCallback, forgetPasswordCallback) {

        messageService = getBean(dela.MessageService)

        this.caption = messageService.getLoginWindowCaptionMsg()

        account = new Account()

        form = new LoginForm(
                loginCallback: loginCallback,
                forgetPasswordCallback: forgetPasswordCallback,
                editable: true,
                data: new BeanItem(account),
        )

        this.addComponent(form)

        this.modal = true;

        this.layout.setSizeUndefined()
        this.center()
    }

}
