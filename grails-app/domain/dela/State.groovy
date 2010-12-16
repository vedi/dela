package dela

class State {

    String name

    static constraints = {
        name(unique:true)
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        if (id != state.id) return false;

        return true;
    }

    def int hashCode() {
        return id != null ? id.hashCode() : 0
    }

    def String toString() {
        return name
    }

}
