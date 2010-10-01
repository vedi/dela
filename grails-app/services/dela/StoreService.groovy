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

    def saveSetup(setup) {
        if (isLoggedIn()) {
            sessionContext.account = accountService.saveSetup(sessionContext.account, setup)
        }
        sessionContext.setup = setup
    }

    def auth(login, password) {
        assert !isLoggedIn()
        def foundAccount = accountService.auth(login, password)
        if (foundAccount) {
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
        sessionContext.account != accountService.anonymous
    }

    boolean register(Account account, urlStr) {
        account = accountService.register(account)

        if (account) {
            sendRegistrationMail(account.email, account.password, urlStr)
            return true
        } else {
            return false
        }
    }

    boolean resetPassword(email, urlStr) {
        def account = accountService.resetPassword(email, urlStr)

        if (account) {
            sendResetPasswordMail(account.email, account.password, urlStr)
            return true
        } else {
            return false
        }
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

        Account account = accountService.confirmRegistration(uuid, password)
        if (account) {

            def subject = subjectService.createDefault(account)
            assert subject.save(), subject.errors

            sessionContext.account = account
            sessionContext.setup = fillSetup()

            return true
        } else {
            return false
        }
    }
    
    private def initSessionContext() {
        sessionContext = new SessionContext(metaProvider: new MetaProvider(), storeService: this)
        sessionContext.account = accountService.anonymous
        sessionContext.setup = fillSetup()
    }

    private Setup fillSetup() {
        if (!sessionContext.account.setup) {
            return accountService.createDefaultSetup(sessionContext.account)
        } else {
            return sessionContext.account.setup
        }
    }
}
