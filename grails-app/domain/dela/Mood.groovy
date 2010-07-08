package dela

class Mood {

    String name = "New mood"
    String description

    static constraints = {
        description(nullable:true)
    }
}
