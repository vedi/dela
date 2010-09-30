package dela.ui

import com.vaadin.Application
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.MessageService
import dela.StoreService
import dela.context.DataContext
import dela.ui.account.ConfirmRegistrationWindow
import dela.ui.account.ForgetPasswordWindow
import dela.ui.account.LoginWindow
import dela.ui.account.RegisterWindow
import dela.ui.task.TaskTable

public class DelaApplication extends Application {

    final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def sessionContext

    def table

    StoreService storeService
    MessageService messageService

    HorizontalLayout topLayout

    ConfirmRegistrationWindow confirmRegistrationWindow

    @Override
	public void init() {

        messageService = getBean(MessageService.class)
        storeService = getBean(StoreService.class)
        sessionContext = storeService.sessionContext
    
        Window mainWindow
		mainWindow = new Window("Dela");

        VerticalLayout verticalLayout = new VerticalLayout()
        verticalLayout.setSizeFull()
        verticalLayout.setMargin true
        mainWindow.setContent(verticalLayout)

        initTopPanel(verticalLayout)

        HorizontalLayout horizontalLayout = new HorizontalLayout()
        horizontalLayout.setSizeFull()
        verticalLayout.addComponent(horizontalLayout)
        verticalLayout.setExpandRatio(horizontalLayout, 1.0f)

        def taskDataContext = new DataContext(sessionContext: sessionContext, metaDomain: sessionContext.metaProvider.taskMeta)
        table = new TaskTable(dataContext: taskDataContext)
        table.setSizeFull()

		horizontalLayout.addComponent(table)
        horizontalLayout.setExpandRatio(table, 1.0f)

		setMainWindow(mainWindow)

	}

    void initTopPanel(layout) {

        topLayout = new HorizontalLayout()
        topLayout.setWidth "100%"

        layout.addComponent(topLayout)

        refreshTopPanelContent()
    }

    def refreshTopPanelContent() {
        if (!storeService.isLoggedIn()) {
            showAnonymousPanel()
        } else {
            showLoggedInPanel()
        }
    }

    def loginCallback = {login, password ->
        def foundAccount = this.storeService.auth(login, password)
        if (foundAccount) {
            showLoggedInPanel()
            this.table.refresh()
        } else {
            this.mainWindow.showNotification(this.messageService.getAuthFailedMsg())
        }
    }

    def forgetPasswordCallback = {
        this.mainWindow.addWindow(new ForgetPasswordWindow(this.resetPasswordCallback))
    }

    def resetPasswordCallback = {email ->
        if (this.storeService.resetPassword(email, getWindow(CONFIRM_REGISTRATION_NAME).getURL().toString())) {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordSuccessMsg())
        } else {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordFailedMsg())
        }
    }

    def registerCallback = {account ->
        if (this.storeService.register(account, getWindow(CONFIRM_REGISTRATION_NAME).getURL().toString())) {
            this.mainWindow.showNotification(this.messageService.getRegistrationSuccessMsg())
        } else {
            this.mainWindow.showNotification(this.messageService.getRegistrationFailedMsg())
        }
    }

    private def showAnonymousPanel() {
        topLayout.removeAllComponents()

        HorizontalLayout anonymousLayout = new HorizontalLayout()

        Button loginButton = new Button(this.messageService.getLoginButtonLabel())
        loginButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new LoginWindow(loginCallback, forgetPasswordCallback))
            }
        })
        anonymousLayout.addComponent(loginButton)

        Button registerButton = new Button(this.messageService.getRegisterButtonLabel())
        registerButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new RegisterWindow(registerCallback))
            }
        })
        anonymousLayout.addComponent(registerButton)

        topLayout.addComponent anonymousLayout
        topLayout.setComponentAlignment(anonymousLayout, Alignment.TOP_RIGHT)
    }

    private def showLoggedInPanel() {
        topLayout.removeAllComponents()

        HorizontalLayout loggedInLayout = new HorizontalLayout()
        loggedInLayout.spacing = true

        Label label = new Label();
        label.setValue(messageService.getLoggedInfoMsg(sessionContext.account))
        loggedInLayout.addComponent(label)

        Button logoutButton = new Button(messageService.getLogoutButtonLabel())
        logoutButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.storeService.logout()
                DelaApplication.this.showAnonymousPanel()
                DelaApplication.this.table.refresh()
            }
        })
        loggedInLayout.addComponent(logoutButton)

        topLayout.addComponent loggedInLayout
        topLayout.setComponentAlignment(loggedInLayout, Alignment.TOP_RIGHT)
    }

    def Window getWindow(String name) {
        if (CONFIRM_REGISTRATION_NAME.equals(name)) {
            if (!confirmRegistrationWindow) {
                confirmRegistrationWindow = new ConfirmRegistrationWindow()
                confirmRegistrationWindow.name = name
                addWindow(confirmRegistrationWindow)
            }
            confirmRegistrationWindow
        } else {
            return super.getWindow(name);
        }
    }


}