package dela

class State {

    String name

    static constraints = {
        name(unique:true)
    }

    def boolean equals(Object obj) {
        return id?.equals(obj?.id);
    }

    def int hashCode() {
        return id != null ? id.hashCode() : 0
    }

    def String toString() {
        return "$name($id)"
    }

}
