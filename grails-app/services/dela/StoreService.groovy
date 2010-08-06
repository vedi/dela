package dela

import com.vaadin.Application

class StoreService {

    static transactional = true
    static scope = "session"

    final String CONFIRM_REGISTRATION_NAME = 'confirmRegistration'

    def messageSource
    def dataService
    def mailService

    private Setup setup

    def origAccount

    Application application

    def Account getAccount() {
        origAccount ?: dataService.anonymous
    }

    def Setup getSetup() {
        if (origAccount) {
            origAccount.setup ?: createSetup(origAccount)
        } else {
            setup ?: dataService.anonymous.setup ?: createSetup(dataService.anonymous)
        }
    }

    def Setup createSetup(account) {
        def activeStates = [State.get(1), State.get(2)]
        def ownSubjects = Subject.findAllByOwner(account)

        return new Setup(filterSubjects: ownSubjects, filterStates: activeStates, activeSubject: ownSubjects[0])
    }

    def setSetup(Setup setup) {
        if (origAccount) {
            if (!setup.account) {
                setup.account = origAccount
            }
            assert (setup = setup.merge()), setup.errors
            
            origAccount.setup = setup
            origAccount.merge()
        } else {
            this.setup = setup
        }
    }

    def auth(login, password) {
        assert !origAccount
        def foundAccount = Account.findByLoginAndPassword(login, password)
        if (foundAccount && foundAccount.state == Account.STATE_ACTIVE) {
            origAccount = foundAccount
            return foundAccount
        } else {
            return null
        }
        
    }
    
    def logout() {
        assert origAccount
        origAccount = null
    }
    
    def isLoggedIn() {
        origAccount != null
    }

    boolean register(Account account) {
        assert account
        assert !account.id
        account.state = Account.STATE_CREATING
        account.role = Account.ROLE_USER
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors
        
        sendRegistrationMail(account.email, account.password)

        return true
    }

    boolean resetPassword(email) {
        Account account = Account.findByEmail(email)
        assert account
        account.state = Account.STATE_CREATING
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors

        sendResetPasswordMail(account.email, account.password)

        return true
    }

    def sendRegistrationMail(String email, uuid) {

        String title = application.i18n('mail.confirmRegistration.title', 'Confirm Registration on Dela')
        String body = application.i18n('mail.confirmRegistration.body',
                'mail.confirmRegistration.body',
                [application.getWindow(CONFIRM_REGISTRATION_NAME).getURL().toString(), uuid.toString()])

        mailService.sendMail {
            to email
            subject title
            html body
        }

    }

    def sendResetPasswordMail(String email, uuid) {

        String title = application.i18n('mail.resetPassword.title', 'Reset Password on Dela')
        String body = application.i18n('mail.resetPassword.body',
                'mail.resetPassword.body',
                [application.getWindow(CONFIRM_REGISTRATION_NAME).getURL().toString(), uuid.toString()])

        mailService.sendMail {
            to email
            subject title
            html body
        }

    }

    boolean confirmRegistration(String uuid, String password) {
        assert uuid
        Account account = Account.findByPassword(uuid)
        if (account && account.state == Account.STATE_CREATING) {
            origAccount = account
            account.state = Account.STATE_ACTIVE
            account.password = password
            assert account.save(), account.errors

            return true
        } else {
            return false
        }
    }
}
