import dela.Task
import dela.Subject
import dela.Account

class BootStrap {

    def dataService


     def init = { servletContext ->
         initAnonymous(servletContext)
     }

    void initAnonymous(servletContext) {
        Account account = Account.findByRole(Account.ANONYMOUS)
        if (!account) {
            account = new Account(login:'anonymous', email:'none', role:Account.ANONYMOUS)
            account.save()
        }

        dataService.anonymous = account
    }

    def destroy = {
    }
} 