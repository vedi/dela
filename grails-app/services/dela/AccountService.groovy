package dela

class AccountService {

    static transactional = true

    def anonymous

    def auth(login, password) {
        def foundAccount = Account.findByLoginAndPassword(login, password)
        if (foundAccount && foundAccount.state == Account.STATE_ACTIVE) {
            return foundAccount
        } else {
            return null
        }
    }
    /**
     * @param account
     * @param setup
     * @return merged account
     */
    def saveSetup(account, setup) {
        Setup.withTransaction {
            if (!setup.account) {
                setup.account = account
            }
            assert (setup = setup.merge()), setup.errors

            account.setup = setup
            def mergedAccount = account.merge()
            assert mergedAccount, account.errors

            mergedAccount
        }
    }

    Account register(Account account) {
        assert account
        assert !account.id
        account.state = Account.STATE_CREATING
        account.role = Account.ROLE_USER
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors
        account
    }

    Account confirmRegistration(String uuid, String password) {

        assert uuid
        assert password

        Account account = Account.findByPassword(uuid)
        if (account && account.state == Account.STATE_CREATING) {
            account.state = Account.STATE_ACTIVE
            account.password = password

            assert account.save(), account.errors

            return account
        } else {
            return null
        }
    }

    Account resetPassword(email, urlStr) {
        Account account = Account.findByEmail(email)
        assert account
        account.state = Account.STATE_CREATING
        account.password = UUID.randomUUID().toString()
        assert account.save(), account.errors

        account
    }

    Setup createDefaultSetup(Account account) {
        def activeStates = [State.get(1), State.get(2)]
        def ownSubjects = Subject.findAllByOwner(account)

        return new Setup(filterSubjects: ownSubjects, filterStates: activeStates, activeSubject: ownSubjects[0])
    }
}
