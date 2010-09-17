package dela

class Task {
    
    static belongsTo = [subject: Subject, author: Account]

    String name

    String description

    Version subjectVersion

    Date dateCreated
    Date lastUpdated

    Double power

    State state

    static constraints = {
        description(nullable:true)
        subjectVersion(nullable:true)
    }

    static mapping = {
        subject lazy: false
        author lazy: false
        state lazy: false
        description type: 'text'
    }

}