package dela

import com.vaadin.Application
import dela.context.SessionContext
import dela.meta.MetaProvider
import org.springframework.beans.factory.InitializingBean

class StoreService implements InitializingBean {

    static transactional = true
    static scope = "prototype"

    static final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def sessionContext

    def commonDataService
    def mailService

    Application application

    void afterPropertiesSet() {
        initSessionContext()
    }

    def Account getAccount() {
        sessionContext.account // TODO: Remove
    }

    /**
     * @return setup from current session
     */
    def Setup getSetup() {
        sessionContext.setup //TODO: Remove
    }

    def setSetup(Setup setup) {
        if (isLoggedIn()) {
            Setup.withTransaction {
                if (!setup.account) {
                    setup.account = sessionContext.account
                }
                assert (setup = setup.merge()), setup.errors

                sessionContext.account.setup = setup
                def mergedAccount = sessionContext.account.merge()
                assert mergedAccount, sessionContext.account.errors
                
                sessionContext.account = mergedAccount
            }
        } else {
            this.sessionContext.setup = setup
        }
    }

    def auth(login, password) {
        assert !isLoggedIn()
        def foundAccount = Account.findByLoginAndPassword(login, password)
        if (foundAccount && foundAccount.state == Account.STATE_ACTIVE) {
            sessionContext.account = foundAccount
            sessionContext.setup = fillSetup()
            return foundAccount
        } else {
            return null
        }
        
    }
    
    def logout() {
        assert isLoggedIn()
        sessionContext.account = commonDataService.anonymous
        sessionContext.setup = fillSetup()
    }
    
    def isLoggedIn() {
        sessionContext.account != commonDataService.anonymous
    }

    boolean register(Account account, urlStr) {
        assert account
        assert !account.id
        account.state = Account.STATE_CREATING
        account.role = Account.ROLE_USER
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors
        
        sendRegistrationMail(account.email, account.password, urlStr)

        return true
    }

    boolean resetPassword(email, urlStr) {
        Account account = Account.findByEmail(email)
        assert account
        account.state = Account.STATE_CREATING
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors

        sendResetPasswordMail(account.email, account.password, urlStr)

        return true
    }

    def sendRegistrationMail(String email, uuid, urlStr) {

        String title = application.i18n('mail.confirmRegistration.title', 'Confirm Registration on Dela')
        String body = application.i18n('mail.confirmRegistration.body', 'mail.confirmRegistration.body',
                [urlStr, uuid.toString()])

        mailService.sendMail {
            to email
            subject title
            html body
        }

    }

    def sendResetPasswordMail(String email, uuid, urlStr) {

        String title = application.i18n('mail.resetPassword.title', 'Reset Password on Dela')
        String body = application.i18n('mail.resetPassword.body', 'mail.resetPassword.body', [urlStr, uuid.toString()])

        mailService.sendMail {
            to email
            subject title
            html body
        }

    }

    boolean confirmRegistration(String uuid, String password) {

        assert uuid
        assert password

        Account account = Account.findByPassword(uuid)
        if (account && account.state == Account.STATE_CREATING) {
            sessionContext.account = account
            sessionContext.setup = fillSetup()
            account.state = Account.STATE_ACTIVE
            account.password = password
            assert account.save(), account.errors

            createDefaultSubject(account)

            return true
        } else {
            return false
        }
    }
    
    private def initSessionContext() {
        sessionContext = new SessionContext(metaProvider: new MetaProvider())
        sessionContext.account = commonDataService.anonymous
        sessionContext.setup = fillSetup()
    }

    private Setup fillSetup() {
        if (!sessionContext.account.setup) {

            def activeStates = [State.get(1), State.get(2)]
            def ownSubjects = Subject.findAllByOwner(sessionContext.account)

            return new Setup(filterSubjects: ownSubjects, filterStates: activeStates, activeSubject: ownSubjects[0])
        } else {
            return sessionContext.account.setup
        }
    }

    private void createDefaultSubject(account) {
        def subject = new Subject(owner: account,
                name: application.i18n('default.subject.name', 'My subject'),
                description: application.i18n('default.subject.description', 'My subject'),
                isPublic: false)
        account.addToSubjects(subject)
        subject.save()
    }
}
