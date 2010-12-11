package dela.utils

import dela.Account
import dela.Subject
import dela.State
import dela.Setup

/**
 * @author vedi
 * date 22.11.10
 * time 20:15
 */
class DomainFactory extends AbstractTestFactory {

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

    def createState(params = [:]) {
        State state = new State(
                name: 'state' + UUID.randomUUID().toString(),
        )
        applyParams(state, params)
        state
    }

    def createSetup(params = [:]) {
        Setup setup = new Setup(
                account: createAccount(save: params.save),
                activeSubject: createSubject(save: params.save),
        )
        applyParams(setup, params)
        setup
    }
}
