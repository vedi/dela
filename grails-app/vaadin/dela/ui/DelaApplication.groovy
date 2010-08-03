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
import dela.DataService
import dela.StoreService
import dela.meta.MetaProvider
import dela.ui.account.ConfirmRegistrationWindow
import dela.ui.account.ForgetPasswordWindow
import dela.ui.account.LoginWindow
import dela.ui.account.RegisterWindow
import dela.ui.task.TaskTable

public class DelaApplication extends Application {

    private Window mainWindow
    private MetaProvider metaProvider

    def metaDomain
    def table

    StoreService storeService
    DataService dataService

    HorizontalLayout topLayout

    ConfirmRegistrationWindow confirmRegistrationWindow

    @Override
	public void init() {

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

        storeService = getBean(StoreService.class)
        dataService = getBean(DataService.class)

        metaProvider = new MetaProvider(storeService:storeService)

        metaDomain = metaProvider.taskMeta

        table = new TaskTable(metaDomain: metaDomain, metaProvider: metaProvider)
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
            this.mainWindow.showNotification i18n('auth.failed.message', "auth failed")
        }
    }

    // Show forget password window
    def forgetPasswordCallback = {
        this.mainWindow.addWindow(new ForgetPasswordWindow(this.resetPasswordCallback))
    }

    def resetPasswordCallback = {email ->
        if (this.storeService.resetPassword(email)) {
            this.mainWindow.showNotification i18n('forgetPassword.success.message', "forgetPassword completed wait a mail")
        } else {
            this.mainWindow.showNotification i18n('forgetPassword.failed.message', "forgetPassword failed")
        }
    }

    def registerCallback = {account ->
        if (this.storeService.register(account)) {
            this.mainWindow.showNotification i18n('registration.success.message', "registration completed wait a mail")
        } else {
            this.mainWindow.showNotification i18n('registration.failed.message', "registration failed")
        }
    }

    private def showAnonymousPanel() {
        topLayout.removeAllComponents()

        HorizontalLayout anonymousLayout = new HorizontalLayout()

        Button loginButton = new Button(i18n('button.login.label', 'login'))
        loginButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new LoginWindow(loginCallback, forgetPasswordCallback))
            }
        })
        anonymousLayout.addComponent(loginButton)

        Button registerButton = new Button(i18n('button.register.label', 'register'))
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
        label.setValue "You are logged in as ${storeService.account}"
        loggedInLayout.addComponent(label)

        Button logoutButton = new Button(i18n('button.logout.label', 'logout'))
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
        if ('confirmRegistration'.equals(name)) {
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