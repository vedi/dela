import dela.Task
import dela.Subject
import dela.Account
import dela.State

class BootStrap {

    def dataService


    def init = { servletContext ->
        initStates()
        initAnonymous()
    }

    void initStates() {
        if (!State.count()) {
            new State(name: 'active').save()
            new State(name: 'done').save()
            new State(name: 'canceled').save()
        }
    }

    void initAnonymous() {
        Account account = Account.findByRole(Account.ROLE_ANONYMOUS)
        if (!account) {
            account = new Account(login:'anonymous', email:'none', role:Account.ROLE_ANONYMOUS)
            account.save()
        }

        dataService.anonymous = account
    }

    def destroy = {
    }
} 