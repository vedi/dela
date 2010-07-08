package dela

class Version {

    static belongsTo = [subject : Subject]

    String name = "New version"

    String description

    static constraints = {
        description(nullable:true)
    }
}
