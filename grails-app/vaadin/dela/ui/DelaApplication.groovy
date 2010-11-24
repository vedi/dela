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
import dela.VaadinService
import dela.context.DataContext
import dela.ui.account.ConfirmRegistrationWindow
import dela.ui.account.ForgetPasswordWindow
import dela.ui.account.LoginWindow
import dela.ui.account.RegisterWindow
import dela.ui.task.TaskTable
import dela.AccountService
import com.vaadin.ui.Accordion
import com.vaadin.terminal.FileResource
import dela.ui.subject.SubjectListWindow
import dela.Setup
import dela.Subject

public class DelaApplication extends Application implements ClickListener {

    final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def sessionContext

    def table

    AccountService accountService
    StoreService storeService
    MessageService messageService
    def vaadinService

    def subjectButton
    def setupButton

    HorizontalLayout topLayout

    ConfirmRegistrationWindow confirmRegistrationWindow

    @Override
	public void init() {
        setTheme('dela')

        accountService = getBean(AccountService.class)
        messageService = getBean(MessageService.class)
        storeService = getBean(StoreService.class)
        vaadinService = getBean(VaadinService.class)
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

        initAppBar(horizontalLayout)

        def taskDataContext = new DataContext(sessionContext: sessionContext, metaDomain: sessionContext.metaProvider.taskMeta)
        table = new TaskTable(dataContext: taskDataContext)
        table.setSizeFull()

		horizontalLayout.addComponent(table)
        horizontalLayout.setExpandRatio(table, 1.0f)

		setMainWindow(mainWindow)

	}

    private def initAppBar(HorizontalLayout horizontalLayout) {
        VerticalLayout appBar = new VerticalLayout()
        appBar.setHeight('100%')
        appBar.setWidth('120px')
        def accordion = new Accordion()
        accordion.setHeight('100%')
        VerticalLayout tabLayout = new VerticalLayout();
        tabLayout.addStyleName("margintablayout");
        tabLayout.setMargin(false, true, false, true)
        tabLayout.setHeight(null)
        accordion.addTab(tabLayout, messageService.getMessage("actions.label"), null)

        subjectButton = new Button();
        subjectButton.caption = messageService.getEntityListCaptionMsg(Subject.simpleName.toLowerCase())
        subjectButton.setIcon(new FileResource(vaadinService.getFile('images/skin/category.png'), this))
        subjectButton.setWidth('100%')
        subjectButton.addStyleName('actionButton')
        subjectButton.addListener(this as ClickListener)
        tabLayout.addComponent(subjectButton)

        setupButton = new Button();
        setupButton.caption = messageService.getEntityListCaptionMsg(Setup.simpleName.toLowerCase())
        setupButton.setIcon(new FileResource(vaadinService.getFile('images/skin/blue_config.png'), this))
        setupButton.setWidth('100%')
        subjectButton.addStyleName('actionButton')
        setupButton.addListener(this as ClickListener)
        tabLayout.addComponent(setupButton)

        appBar.addComponent(accordion)
        horizontalLayout.addComponent(appBar)

        VerticalLayout spacer = new VerticalLayout()
        spacer.setWidth("10px")
        horizontalLayout.addComponent(spacer)

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

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == subjectButton) {
            this.mainWindow.addWindow(new SubjectListWindow(sessionContext: sessionContext))
        } else if (clickEvent.button == setupButton) {
            this.mainWindow.addWindow(new SetupWindow(sessionContext: sessionContext))
        } else {
            throw new IllegalArgumentException()
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
        if (this.storeService.resetPassword(email, getWindow(this.CONFIRM_REGISTRATION_NAME).getURL().toString())) {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordSuccessMsg())
        } else {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordFailedMsg())
        }
    }

    def registerCallback = {account ->
        if (this.storeService.register(account, getWindow(this.CONFIRM_REGISTRATION_NAME).getURL().toString())) {
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