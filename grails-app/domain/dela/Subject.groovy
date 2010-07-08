package dela

class Subject {

    static hasMany = [versions: Version]

    String name = "New subject" 

    String description

    static constraints = {
        description(nullable:true)
    }

    static mapping = {
        versions lazy:false
    }

    def String toString() {
        return "$name($id)"
    }

    def boolean equals(Object obj) {
        return id?.equals(obj?.id);
    }


}
