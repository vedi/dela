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
import dela.AccountService
import com.vaadin.ui.Accordion
import com.vaadin.terminal.FileResource
import dela.ui.subject.SubjectListWindow
import dela.Setup
import dela.Subject
import dela.Account
import dela.ui.account.AccountListWindow
import dela.ui.account.ProfileWindow
import dela.Task
import dela.TaskService
import dela.Utils

@Mixin(Utils)
public class DelaApplication extends Application implements ClickListener {

    final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def sessionContext

    def table

    AccountService accountService
    StoreService storeService
    MessageService messageService

    def subjectButton
    def setupButton
    def accountButton

    HorizontalLayout topLayout

    ConfirmRegistrationWindow confirmRegistrationWindow
    VerticalLayout appBar

    @Override
	public void init() {
        setTheme('dela')

        accountService = getBean(AccountService.class)
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

        initAppBar(horizontalLayout)

        def taskDataContext = new DataContext(sessionContext: sessionContext, domainClass: Task, dataViewName: TaskService.OWN_TASKS_DATA_VIEW)
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
        actionTabLayout.setHeight(null)
        accordion.addTab(actionTabLayout, messageService.getMessage("actions.label"), null)

        subjectButton = new Button();
        subjectButton.caption = messageService.getEntityListCaptionMsg(Subject.simpleName.toLowerCase())
        subjectButton.setIcon(new FileResource(getFile('images/skin/category.png'), this))
        subjectButton.setWidth('100%')
        subjectButton.addStyleName('actionButton')
        subjectButton.addListener(this as ClickListener)
        actionTabLayout.addComponent(subjectButton)

        setupButton = new Button();
        setupButton.caption = messageService.getEntityListCaptionMsg(Setup.simpleName.toLowerCase())
        setupButton.setIcon(new FileResource(getFile('images/skin/blue_config.png'), this))
        setupButton.setWidth('100%')
        setupButton.addStyleName('actionButton')
        setupButton.addListener(this as ClickListener)
        actionTabLayout.addComponent(setupButton)
    }

    protected def addAdminTab(Accordion accordion) {
        VerticalLayout adminTabLayout = new VerticalLayout();
        adminTabLayout.addStyleName("margintablayout");
        adminTabLayout.setMargin(false, true, false, true)
        adminTabLayout.setHeight(null)
        accordion.addTab(adminTabLayout, messageService.getMessage("admin.area.label"), null)

        accountButton = new Button();
        accountButton.caption = messageService.getEntityListCaptionMsg(Account.simpleName.toLowerCase())
        accountButton.setIcon(new FileResource(getFile('images/skin/category.png'), this))
        accountButton.setWidth('100%')
        accountButton.addStyleName('actionButton')
        accountButton.addListener(this as ClickListener)
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

    def void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == subjectButton) {
            this.mainWindow.addWindow(new SubjectListWindow(sessionContext: sessionContext))
        } else if (clickEvent.button == setupButton) {
            this.mainWindow.addWindow(new SetupWindow(sessionContext: sessionContext))
        } else if (clickEvent.button == accountButton) {
            this.mainWindow.addWindow(new AccountListWindow(sessionContext: sessionContext))
        } else {
            throw new IllegalArgumentException()
        }
    }

    def loginCallback = {login, password ->
        def foundAccount = this.storeService.auth(login, password)
        if (foundAccount) {
            showLoggedInPanel()
            refreshAppBarContent()
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
                DelaApplication.this.mainWindow.addWindow(new ProfileWindow(sessionContext:sessionContext))
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