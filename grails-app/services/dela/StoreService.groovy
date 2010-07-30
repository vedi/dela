package dela

class StoreService {

    static transactional = true
    static scope = "session"

    def dataService
    def mailService

    private Setup setup

    def origAccount
    
    def Account getAccount() {
        origAccount ?: dataService.anonymous
    }

    def Setup getSetup() {
        if (origAccount) {
            origAccount.setup ?: createSetup()
        } else {
            setup ?: dataService.anonymous.setup ?: createSetup()
        }
    }

    def Setup createSetup() {
        return new Setup(filterSubjects: [], filterStates: [])
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
        
        sendRegistrationMail(account.email, account.password) //OPT: send in queue

        return true
    }

    boolean resetPassword(email) {
        Account account = Account.findByEmail(email)
        assert account
        account.state = Account.STATE_CREATING
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors

//        sendRegistrationMail(account.email, account.password) //OPT: send in queue

        return true
    }

    def sendRegistrationMail(String email, uuid) {
        mailService.sendMail {
            to email
            subject "Registration on dela-app" //TODO: i18n
            html "<a href='http://localhost:8080/dela-grails/confirmRegistration?uuid=$uuid'>confirm registration</a>"  //TODO: i18n
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
