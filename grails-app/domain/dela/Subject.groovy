package dela

class Subject {

    def storeService

    static belongsTo = [owner : Account]
    static hasMany = [versions : Version]

    String name

    String description
    
    boolean isPublic

    static constraints = {
        owner(validator: {val, obj ->
            obj.validateOwner(val)
        })
        description(nullable:true)
    }

    def validateOwner(owner) {
        return storeService.account.isAdmin() || owner.equals(storeService.account)
    }

    def beforeInsert() {
        assert storeService.account.isNotAnonymous()
    }

    def beforeUpdate() {
        assert storeService.account.isAdmin() ||
                (storeService.account.isNotAnonymous() && storeService.account.equals(this.owner))
    }

    def beforeDelete() {
        beforeUpdate()
    }

    static mapping = {
        owner lazy:false
        versions lazy:false
    }

    def String toString() {
        return "$name($id)"
    }

    boolean equals(o) {
        if (o == null) return false;

        if (this.is(o)) return true;

        if (getClass() != o.class) return false;

        Subject subject = (Subject) o;

        if (id != subject.id) return false;

        return true;
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
