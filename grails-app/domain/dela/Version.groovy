package dela

class Version {

    static belongsTo = [subject : Subject]

    String name

    String description

    static constraints = {
        description(nullable:true)
    }
}
