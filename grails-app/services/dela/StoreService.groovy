package dela

class StoreService {

    static transactional = true
    static scope = "session"

    def dataService
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
                origAccount.setup = setup
            }
            assert setup.merge(), setup.errors
            origAccount.merge()
        } else {
            this.setup = setup
        }
    }

}
