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

    def applyParams(domain, params = [:]) {
        domain.properties = params
    }
}
