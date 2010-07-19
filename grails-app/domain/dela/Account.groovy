package dela

class Account {

    static ANONYMOUS    = 0
    static USER         = 50
    static ADMIN        = 100

    static hasOne = [setup : Setup]
    static hasMany = [subjects : Subject]

    String login
    String email
    byte role

    static constraints = {
        setup(nullable:true)
    }

    static mapping = {
        subjects lazy: false
        setup lazy: false
    }

    def String toString() {
        return login
    }

    boolean equals(o) {
        if (o == null) return false;
        if (this.is(o)) return true;

        if (getClass() != o.class) return false;

        Account account = (Account) o;

        if (id != account.id) return false;

        return true;
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    def isNotAnonymous() {
        this.role != Account.ANONYMOUS
    }

    def isAdmin() {
        this.role == Account.ADMIN
    }
}
