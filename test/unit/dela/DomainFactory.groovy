package dela

/**
 * @author vedi
 * date 22.11.10
 * time 20:15
 */
class DomainFactory {

    def createAccount(params = [:]) {
        Account account = new Account(
                login    : 'login' + UUID.randomUUID().toString(),
                email    : 'email@mail.mu',
                password : 'password',
                role     : Account.ROLE_ANONYMOUS,
                state    : Account.STATE_BLOCKED
        )
        applyParams(account, params)
        account
    }

    def createSubject(params = [:]) {
        Subject subject = new Subject(
                name: 'subject' + UUID.randomUUID().toString(),
                description: "subject's desc",
                isPublic: false,
                owner : createAccount()
        )
        applyParams(subject, params)
        subject
    }

    def applyParams(domain, params = [:]) {
        if (params) {
            domain.properties = params
        }
    }
}
