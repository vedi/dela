package dela.ui

import com.vaadin.Application
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import dela.TaskService
import dela.ui.task.TaskTable
import com.vaadin.ui.*
import dela.ui.account.*

public class DelaApplication extends Application {

    final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def sessionContext

    def table

    def accountService
    def storeService
    def messageService

    def delaVaadinFacade

    def subjectButton
    def setupButton
    def accountButton

    HorizontalLayout topLayout

    ConfirmRegistrationWindow confirmRegistrationWindow
    VerticalLayout appBar

    @Override
	public void init() {
        setTheme('dela')

        accountService = getBean(dela.AccountService)
        messageService = getBean(dela.MessageService)
        storeService = getBean(dela.StoreService)
        sessionContext = storeService.sessionContext
    
        delaVaadinFacade = getBean(dela.ui.common.DelaVaadinFacade)

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

        def taskService = getBean(dela.TaskService)
        assert taskService

        def taskDataContext = taskService.createDataContext(sessionContext, TaskService.OWN_TASKS_DATA_VIEW)
        table = new TaskTable(dataContext: taskDataContext)
        table.setSizeFull()

		horizontalLayout.addComponent(table)
        horizontalLayout.setExpandRatio(table, 1.0f)

		setMainWindow(mainWindow)

	}

    private def initAppBar(HorizontalLayout horizontalLayout) {
        appBar = new VerticalLayout()
        appBar.setHeight('100%')
        appBar.setWidth('120px')

        refreshAppBarContent()

        horizontalLayout.addComponent(appBar)

        VerticalLayout spacer = new VerticalLayout()
        spacer.setWidth("10px")
        horizontalLayout.addComponent(spacer)

    }

    def refreshAppBarContent() {

        appBar.removeAllComponents()

        def accordion = new Accordion()
        accordion.setHeight('100%')

        addCommonTab(accordion)

        if (sessionContext.account?.isAdmin()) {
            addAdminTab(accordion)
        }

        appBar.addComponent(accordion)
    }

    protected def addCommonTab(Accordion accordion) {
        VerticalLayout actionTabLayout = new VerticalLayout();
        actionTabLayout.addStyleName("margintablayout");
        actionTabLayout.setMargin(false, true, false, true)
        actionTabLayout.setHeight(null as String)
        accordion.addTab(actionTabLayout, messageService.getMessage("actions.label"), null)

        subjectButton = delaVaadinFacade.createSubjectListButton(sessionContext, this)
        actionTabLayout.addComponent(subjectButton)

        setupButton = delaVaadinFacade.createOwnSetupButton(sessionContext, this)
        actionTabLayout.addComponent(setupButton)
    }

    protected def addAdminTab(Accordion accordion) {
        VerticalLayout adminTabLayout = new VerticalLayout();
        adminTabLayout.addStyleName("margintablayout");
        adminTabLayout.setMargin(false, true, false, true)
        adminTabLayout.setHeight(null as String)
        accordion.addTab(adminTabLayout, messageService.getMessage("admin.area.label"), null)

        accountButton = delaVaadinFacade.createAccountListButton(sessionContext, this)
        adminTabLayout.addComponent(accountButton)
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
            refreshAppBarContent()
            this.table.refresh()
        } else {
            this.mainWindow.showNotification(this.messageService.getAuthFailedMsg() as String)
        }
    }

    def forgetPasswordCallback = {
        this.mainWindow.addWindow(new ForgetPasswordWindow(this.resetPasswordCallback))
    }

    def resetPasswordCallback = {email ->
        if (this.storeService.resetPassword(email, getWindow(this.CONFIRM_REGISTRATION_NAME).getURL().toString())) {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordSuccessMsg() as String)
        } else {
            this.mainWindow.showNotification(this.messageService.getForgetPasswordFailedMsg() as String)
        }
    }

    def registerCallback = {account ->
        if (this.storeService.register(account, getWindow(this.CONFIRM_REGISTRATION_NAME).getURL().toString())) {
            this.mainWindow.showNotification(this.messageService.getRegistrationSuccessMsg() as String)
        } else {
            this.mainWindow.showNotification(this.messageService.getRegistrationFailedMsg() as String)
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

        Label label = new Label();
        label.setValue(messageService.getLoggedInfoMsg(sessionContext.account))
        loggedInLayout.addComponent(label)

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("10px")
        loggedInLayout.addComponent(horizontalLayout)

        Button logoutButton = new Button(messageService.getLogoutButtonLabel())
        logoutButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.storeService.logout()
                DelaApplication.this.showAnonymousPanel()
                DelaApplication.this.refreshAppBarContent()

                DelaApplication.this.table.refresh()
            }
        })
        loggedInLayout.addComponent(logoutButton)

        Button profileButton = new Button(messageService.getProfileButtonLabel())
        profileButton.addListener(new ClickListener() {
            void buttonClick(ClickEvent clickEvent) {
                DelaApplication.this.mainWindow.addWindow(new ProfileWindow(sessionContext:sessionContext)) // TODO: RF
            }
        })
        loggedInLayout.addComponent(profileButton)

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