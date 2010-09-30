package dela

import dela.context.SessionContext
import dela.meta.MetaProvider
import org.springframework.beans.factory.InitializingBean

class StoreService implements InitializingBean {

    static transactional = true
    static scope = "prototype"

    def sessionContext

    def accountService
    def subjectService
    def mailService
    def messageService

    void afterPropertiesSet() {
        initSessionContext()
    }

    def Account getAccount() {
        sessionContext.account // TODO: Remove
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
        sessionContext.account = accountService.anonymous
        sessionContext.setup = fillSetup()
    }
    
    def isLoggedIn() {
        accountService.isLoggedIn(sessionContext)
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

        String title = messageService.getConfirmRegistrationMailTitle()
        String body = messageService.getConfirmRegistrationMailBody([urlStr, uuid.toString()])

        mailService.sendMail {
            to email
            subject title
            html body
        }

    }

    def sendResetPasswordMail(String email, uuid, urlStr) {

        String title = messageService.getResetPasswordMailTitle()
        String body = messageService.getResetPasswordMailBody([urlStr, uuid.toString()])

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
            account.state = Account.STATE_ACTIVE
            account.password = password

            subjectService.createDefault(account)

            assert account.save(), account.errors // TODO: Test save of the subject

            sessionContext.account = account
            sessionContext.setup = fillSetup()

            return true
        } else {
            return false
        }
    }
    
    private def initSessionContext() {
        sessionContext = new SessionContext(metaProvider: new MetaProvider())
        sessionContext.account = accountService.anonymous
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
}
