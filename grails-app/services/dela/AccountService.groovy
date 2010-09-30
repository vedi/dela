package dela

class AccountService {

    static transactional = true

    def anonymous

    def saveSetup(sessionContext, setup) {
        if (isLoggedIn(sessionContext)) {
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
            sessionContext.setup = setup
        }
    }

    def isLoggedIn(sessionContext) {
        sessionContext.account != anonymous
    }

}
