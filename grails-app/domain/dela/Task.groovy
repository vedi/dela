package dela

class Task {
    
    static belongsTo = [subject: Subject]

    String name

    String description

    Version subjectVersion

    Date dateCreated
    Date lastUpdated

    Double power = Math.random()

    State state

    static constraints = {
        description(nullable:true)
        subjectVersion(nullable:true)
    }

    static mapping = {
        subject lazy: false
        state lazy: false
    }
}
