package dela

class Task {
    
    def storeService

    static belongsTo = [subject: Subject, author: Account]

    String name

    String description

    Version subjectVersion

    Date dateCreated
    Date lastUpdated

    Double power

    State state

    static constraints = {
        author(validator: {val, obj ->
            obj.validateAuthor(val)
        })
        description(nullable:true)
        subjectVersion(nullable:true)
    }

    static mapping = {
        subject lazy: false
        author lazy: false
        state lazy: false
        description type: 'text'
    }

    def validateAuthor(author) {
        return storeService.account.isAdmin() || (author.equals(storeService.account) &&
                (this.subject.isPublic || this.subject.owner.equals(author)))
        // TODO: Check change subject
    }

    def beforeInsert() {
        assert storeService.account.isNotAnonymous()
    }

    def beforeDelete() {
        assert storeService.account.isAdmin() || (author.equals(storeService.account) &&
                (this.subject.isPublic || this.subject.owner.equals(author)))
    }

}